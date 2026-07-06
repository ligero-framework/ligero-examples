#!/usr/bin/env bash
#
# Reproducible framework comparison: builds each app, then measures cold-JVM
# startup (median of $RUNS fresh launches), resident memory after warmup, and
# throughput on GET /products. Same app, same JVM flags, one at a time.
#
# Usage:  ./run.sh            # build + measure, writes results.md
#         RUNS=7 CONC=100 DUR=15 ./run.sh
#
set -u

HERE=$(cd "$(dirname "$0")" && pwd)
PORT=${PORT:-8099}
CONC=${CONC:-50}
DUR=${DUR:-10}
RUNS=${RUNS:-5}

APPS=("ligero-app" "ligero-jetty-app" "springboot-app" "javalin-app")
LABELS=("Ligero (JDK engine)" "Ligero (Jetty engine)" "Spring Boot (MVC/Tomcat)" "Javalin (Jetty)")

command -v curl >/dev/null || { echo "curl required"; exit 1; }
echo "Compiling load driver..."
javac -d "$HERE/bench" "$HERE/bench/Load.java" || exit 1

free_port() { fuser -k ${PORT}/tcp >/dev/null 2>&1; sleep 0.5; }

wait_ready() { # $1 = pid
  while ! curl -sf "http://localhost:${PORT}/products" >/dev/null 2>&1; do
    kill -0 "$1" 2>/dev/null || return 1
    sleep 0.02
  done
}

median() { # reads numbers on stdin
  sort -n | awk '{a[NR]=$1} END{ if(NR%2){print a[(NR+1)/2]} else {printf "%.0f\n",(a[NR/2]+a[NR/2+1])/2} }'
}

declare -A STARTUP RSS THROUGHPUT P50 P99 DIST

for i in "${!APPS[@]}"; do
  app="${APPS[$i]}"
  label="${LABELS[$i]}"
  bin="$HERE/$app/build/install/$app/bin/$app"
  echo ""
  echo "=== $label ==="

  echo "  building..."
  ( cd "$HERE/$app" && gradle installDist --console=plain -q >/tmp/build.log 2>&1 ) \
    || { echo "  build failed"; tail -5 /tmp/build.log; continue; }
  DIST[$app]=$(du -sm "$HERE/$app/build/install/$app/lib" 2>/dev/null | cut -f1)

  # --- startup: median of RUNS cold launches ---
  starts=""
  last_pid=""
  for run in $(seq 1 "$RUNS"); do
    free_port
    t0=$(date +%s%N)
    "$bin" "$PORT" >/tmp/app.log 2>&1 &
    pid=$!
    if ! wait_ready "$pid"; then echo "  app died on run $run"; tail -8 /tmp/app.log; break; fi
    t1=$(date +%s%N)
    ms=$(( (t1 - t0) / 1000000 ))
    starts+="$ms"$'\n'
    echo "  run $run: ${ms} ms"
    if [ "$run" -eq "$RUNS" ]; then last_pid=$pid; else kill "$pid" 2>/dev/null; wait "$pid" 2>/dev/null; fi
  done
  STARTUP[$app]=$(printf '%s' "$starts" | grep -v '^$' | median)

  # --- keep last instance alive for RSS + throughput ---
  if [ -n "$last_pid" ] && kill -0 "$last_pid" 2>/dev/null; then
    echo "  load: ${CONC} connections for ${DUR}s..."
    out=$(java -cp "$HERE/bench" Load "http://localhost:${PORT}/products" "$CONC" "$DUR")
    echo "  $out"
    THROUGHPUT[$app]=$(echo "$out" | grep -o 'throughput=[0-9]*' | cut -d= -f2)
    P50[$app]=$(echo "$out" | grep -o 'p50=[0-9.]*' | cut -d= -f2)
    P99[$app]=$(echo "$out" | grep -o 'p99=[0-9.]*' | cut -d= -f2)
    rss_kb=$(grep VmRSS /proc/"$last_pid"/status 2>/dev/null | awk '{print $2}')
    RSS[$app]=$(( ${rss_kb:-0} / 1024 ))
    echo "  RSS after load: ${RSS[$app]} MB"
    kill "$last_pid" 2>/dev/null; wait "$last_pid" 2>/dev/null
  fi
done
free_port

# --- results.md ---
RESULTS="$HERE/results.md"
{
  echo "# Framework comparison — results"
  echo ""
  echo "Same products CRUD app (\`GET/POST /products\`, in-memory store) built in"
  echo "each framework and launched identically (\`installDist\` -> start script,"
  echo "no JVM tuning). Measured on this machine:"
  echo ""
  echo "- $(java -version 2>&1 | grep -i version | grep -v 'Picked up' | head -1)"
  echo "- CPUs: $(nproc), RAM: $(free -m | awk '/Mem:/{print $2}') MB"
  echo "- Startup = median of ${RUNS} cold JVM launches (process exec -> first HTTP 200)."
  echo "- Throughput = \`GET /products\`, ${CONC} connections, ${DUR}s, after warmup."
  echo "- RSS = resident memory (\`/proc/<pid>/status\` VmRSS) right after the load run."
  echo "- Date: $(date -u +%Y-%m-%d)"
  echo ""
  echo "| Framework | Startup (ms) | RSS (MB) | Throughput (req/s) | p50 (ms) | p99 (ms) | Dist libs (MB) |"
  echo "|---|--:|--:|--:|--:|--:|--:|"
  for i in "${!APPS[@]}"; do
    app="${APPS[$i]}"
    printf "| %s | %s | %s | %s | %s | %s | %s |\n" \
      "${LABELS[$i]}" "${STARTUP[$app]:-–}" "${RSS[$app]:-–}" \
      "${THROUGHPUT[$app]:-–}" "${P50[$app]:-–}" "${P99[$app]:-–}" "${DIST[$app]:-–}"
  done
  echo ""
  echo "> Reproduce: \`cd comparison && ./run.sh\` (needs Ligero in mavenLocal:"
  echo "> \`./gradlew publishToMavenLocal\` in the framework repo)."
} > "$RESULTS"

echo ""
echo "=================================================="
cat "$RESULTS"
