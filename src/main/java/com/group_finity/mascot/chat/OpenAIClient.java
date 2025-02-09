package com.group_finity.mascot.chat;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class OpenAIClient implements AutoCloseable {
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private final String apiKey;
    private final HttpClient client;

    public OpenAIClient(String apiKey) {
        this.apiKey = apiKey;
        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    public String createChatCompletion(String message) throws IOException, InterruptedException {
        String requestBody = String.format("""
                {
                    "model": "gpt-3.5-turbo",
                    "messages": [
                        {"role": "system", "content": "You are a cute and friendly Shimeji character."},
                        {"role": "user", "content": "%s"}
                    ]
                }""", message);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request, 
                HttpResponse.BodyHandlers.ofString());

        // Simple response parsing - in production you should use proper JSON parsing
        String responseBody = response.body();
        int contentStart = responseBody.indexOf("\"content\":\"") + 11;
        int contentEnd = responseBody.indexOf("\"", contentStart);
        return responseBody.substring(contentStart, contentEnd);
    }

    @Override
    public void close() {
        // HttpClient doesn't need explicit cleanup
    }
} 