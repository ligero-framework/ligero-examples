# mcp-server — a Model Context Protocol server

Exposes two tools (`add`, `reverse`) to an LLM over MCP (JSON-RPC 2.0,
Streamable HTTP) with `ligero-mcp`.

## Install the module in your own project

```bash
ligero add mcp        # adds ligero-mcp + generates a starter McpConfig.java
```

## Run this example

```bash
./gradlew run          # MCP endpoint at http://localhost:8080/mcp
```

## Try it (raw JSON-RPC)

```bash
# list the tools
curl -s localhost:8080/mcp -H 'Content-Type: application/json' \
  -d '{"jsonrpc":"2.0","id":1,"method":"tools/list"}'

# call one
curl -s localhost:8080/mcp -H 'Content-Type: application/json' \
  -d '{"jsonrpc":"2.0","id":2,"method":"tools/call",
       "params":{"name":"add","arguments":{"a":2,"b":3}}}'
```

Or connect Claude Desktop / the [MCP Inspector](https://modelcontextprotocol.io)
to `http://localhost:8080/mcp` and call the tools interactively.

Docs: https://doc.ligeroframework.com/guides/mcp
