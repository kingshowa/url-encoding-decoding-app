package com.example;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.apache.commons.codec.binary.Base32;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws IOException {

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/", new MyHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
        System.out.println("Server started on http://localhost:8080");
    }

    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                // Serve the HTML form
                String response = "<html>" +
                                  "<head><title>Message Encoder/Decoder</title></head>" +
                                  "<body>" +
                                  "<h1>Message Encoder/Decoder</h1>" +
                                  "<form method=\"POST\">" +
                                  "<label for=\"url\">Enter Message:</label>" +
                                  "<input type=\"text\" id=\"url\" name=\"url\" required>" +
                                  "<br><br>" +
                                  "<label for=\"action\">Choose Action:</label>" +
                                  "<select id=\"action\" name=\"action\">" +
                                  "<option value=\"encode\">Encode</option>" +
                                  "<option value=\"decode\">Decode</option>" +
                                  "</select>" +
                                  "<br><br>" +
                                  "<button type=\"submit\">Submit</button>" +
                                  "</form>" +
                                  "</body>" +
                                  "</html>";

                exchange.sendResponseHeaders(200, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } else if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                // Process form submission
                InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
                BufferedReader reader = new BufferedReader(isr);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                Map<String, String> parameters = parseFormData(sb.toString());

                String url = parameters.get("url");
                String action = parameters.get("action");
                String result;

                Base32 base32 = new Base32();

                if ("encode".equalsIgnoreCase(action)) {
                    result = base32.encodeToString(url.getBytes(StandardCharsets.UTF_8));
                } else if ("decode".equalsIgnoreCase(action)) {
                    result = new String(base32.decode(url), StandardCharsets.UTF_8);
                } else {
                    result = "Invalid action!";
                }
                // Respond with the result
                String response = String.format(
                        "<html>" +
                        "<head><title>Result</title></head>" +
                        "<body>" +
                        "<h1>Result</h1>" +
                        "<p>Original Message: %s</p>" +
                        "<p>Processed Message: %s</p>" +
                        "<a href=\"/\">Go Back</a>" +
                        "</body>" +
                        "</html>",
                        url, result
                );

                exchange.sendResponseHeaders(200, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }

        // Helper method to parse form data
        private Map<String, String> parseFormData(String formData) {
            Map<String, String> map = new HashMap<>();
            String[] pairs = formData.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=");
                String key = decodeURIComponent(keyValue[0]);
                String value = keyValue.length > 1 ? decodeURIComponent(keyValue[1]) : "";
                map.put(key, value);
            }
            return map;
        }

        private String decodeURIComponent(String component) {
            try {
                return java.net.URLDecoder.decode(component, StandardCharsets.UTF_8.name());
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
