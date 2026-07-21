package com.example.mcp;

import com.ligero.Ligero;
import com.ligero.mcp.McpServer;

import java.util.Map;

/**
 * A Model Context Protocol server exposing two tools over Streamable HTTP.
 * Point an MCP client (Claude Desktop, the MCP Inspector) at
 * {@code http://localhost:8080/mcp}.
 */
public final class McpServerApp {

    public static void main(String[] args) throws Exception {
        Ligero app = Ligero.create(8080);

        McpServer mcp = McpServer.create("ligero-demo", "1.0.0")
            .tool("add", "Add two numbers",
                McpServer.objectSchema(Map.of(
                    "a", McpServer.numberParam("first addend"),
                    "b", McpServer.numberParam("second addend")), "a", "b"),
                arguments -> String.valueOf(
                    ((Number) arguments.get("a")).doubleValue()
                        + ((Number) arguments.get("b")).doubleValue()))
            .tool("reverse", "Reverse a string",
                McpServer.objectSchema(Map.of(
                    "text", McpServer.stringParam("text to reverse")), "text"),
                arguments -> new StringBuilder(String.valueOf(arguments.get("text")))
                    .reverse().toString());

        app.use(mcp.http("/mcp"));
        app.start();
    }
}
