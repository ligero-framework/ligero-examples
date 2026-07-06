import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Zero-dependency load driver used by run.sh to compare frameworks fairly.
 *
 * Usage: java Load <url> <concurrency> <durationSeconds>
 * Prints one line: "requests=<n> throughput=<req/s> p50=<ms> p99=<ms> errors=<n>"
 *
 * Each virtual thread loops sending GET <url> for the whole window; latencies
 * are recorded in nanoseconds and reduced to p50/p99 at the end.
 */
public final class Load {

    public static void main(String[] args) throws Exception {
        String url = args[0];
        int concurrency = Integer.parseInt(args[1]);
        long durationMs = Long.parseLong(args[2]) * 1000L;

        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder(URI.create(url)).GET().build();

        // warm up (JIT + connection pool) — not counted
        for (int i = 0; i < 200; i++) {
            client.send(request, HttpResponse.BodyHandlers.discarding());
        }

        AtomicLong errors = new AtomicLong();
        List<long[]> perThread = new ArrayList<>();
        List<Thread> threads = new ArrayList<>();
        long deadline = System.nanoTime() + durationMs * 1_000_000L;

        for (int t = 0; t < concurrency; t++) {
            long[] latencies = new long[4_000_000];
            perThread.add(latencies);
            final int[] count = {0};
            Thread thread = Thread.ofVirtual().start(() -> {
                while (System.nanoTime() < deadline) {
                    long start = System.nanoTime();
                    try {
                        HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
                        long elapsed = System.nanoTime() - start;
                        if (response.statusCode() >= 400) {
                            errors.incrementAndGet();
                        } else if (count[0] < latencies.length) {
                            latencies[count[0]++] = elapsed;
                        }
                    } catch (Exception e) {
                        errors.incrementAndGet();
                    }
                }
                latencies[latencies.length - 1] = count[0]; // stash count in last slot
            });
            threads.add(thread);
        }
        for (Thread thread : threads) {
            thread.join();
        }

        List<Long> all = new ArrayList<>();
        for (long[] latencies : perThread) {
            int count = (int) latencies[latencies.length - 1];
            for (int i = 0; i < count; i++) {
                all.add(latencies[i]);
            }
        }
        all.sort(null);
        long total = all.size();
        double seconds = durationMs / 1000.0;
        double throughput = total / seconds;
        double p50 = percentileMs(all, 0.50);
        double p99 = percentileMs(all, 0.99);

        System.out.printf("requests=%d throughput=%.0f p50=%.2f p99=%.2f errors=%d%n",
            total, throughput, p50, p99, errors.get());
    }

    private static double percentileMs(List<Long> sorted, double q) {
        if (sorted.isEmpty()) {
            return 0;
        }
        int index = (int) Math.min(sorted.size() - 1, Math.round(q * (sorted.size() - 1)));
        return sorted.get(index) / 1_000_000.0;
    }
}
