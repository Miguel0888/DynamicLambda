package org.example;

import com.sun.net.httpserver.HttpServer;
import org.example.controller.rest.CodeExecutionHandler;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Main {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/run", new CodeExecutionHandler());
        server.start();
        System.out.println("Server läuft auf http://localhost:8080/run");
    }
}
