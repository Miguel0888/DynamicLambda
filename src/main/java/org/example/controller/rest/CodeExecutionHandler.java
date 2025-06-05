package org.example.controller.rest;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.example.compiler.api.InMemoryJavaCompiler;
import org.example.compiler.api.DynamicClassBuilder;
import org.example.controller.rest.dto.CodeRequest;
import org.example.controller.rest.dto.CodeResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;


/**
 * Handles HTTP requests for executing user-provided Java code.
 * Expects a POST request with JSON body containing the code to execute.
 */
public class CodeExecutionHandler implements HttpHandler {

    private final Gson gson = new Gson();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        String body = new BufferedReader(new InputStreamReader(exchange.getRequestBody()))
                .lines().collect(Collectors.joining("\n"));

        CodeRequest request = gson.fromJson(body, CodeRequest.class);

        String result;
        try {
            String className = "UserCode";
            String source = DynamicClassBuilder.wrapAsCallable(className, request.getCode());
            Callable<String> callable = new InMemoryJavaCompiler().compile("UserCode", source, Callable.class);
            String output = callable.call();

            result = gson.toJson(new CodeResponse(String.valueOf(output)));
        } catch (Exception e) {
            result = gson.toJson(new CodeResponse("Fehler: " + e.getMessage()));
        }

        byte[] response = result.getBytes("UTF-8");
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(200, response.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response);
        }
    }
}
