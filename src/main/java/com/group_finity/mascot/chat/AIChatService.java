package com.group_finity.mascot.chat;

public interface AIChatService {
    String chat(String input);
    boolean isConfigured();
    void close();
} 