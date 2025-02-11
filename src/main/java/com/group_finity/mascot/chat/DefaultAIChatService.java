package com.group_finity.mascot.chat;

import com.group_finity.mascot.Mascot;
import com.group_finity.mascot.config.CharacterConfig;
import com.group_finity.mascot.tools.ShimejiBehaviorTools;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;

import java.util.logging.Level;
import java.util.logging.Logger;

import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_O_MINI;

public class DefaultAIChatService implements AIChatService {
    private static final Logger log = Logger.getLogger(DefaultAIChatService.class.getName());
    private ChatLanguageModel model;
    private boolean isConfigured;
    private final String imageSet;
    private ShimejiAssistant assistant;
    private ShimejiBehaviorTools behaviorTools;
    private final Mascot mascot;
    
    public DefaultAIChatService(String imageSet, Mascot mascot) {
        this.imageSet = imageSet;
        this.mascot = mascot;
        initializeService();
    }
    
    private void initializeService() {
        System.out.println("Creating DefaultAIChatService with imageSet: " + imageSet);
        String apiKey = ApiKeyConfigDialog.getApiKey();
        if (apiKey == null || apiKey.isEmpty()) {
            log.warning("OpenAI API Key not configured - using fallback responses");
            this.model = null;
            this.assistant = null;
            this.behaviorTools = null;
            this.isConfigured = false;
        } else {
            this.model = OpenAiChatModel.builder()
                .apiKey(apiKey)
                .modelName(GPT_4_O_MINI)
                .temperature(0.7)
                .strictTools(true)
                .build();
            this.behaviorTools = new ShimejiBehaviorTools(mascot);
            
            // 获取角色信息
            String personality = CharacterConfig.getPersonality(imageSet);
            String systemPrompt = personality + "\nYou can control my actions using various behaviors. " +
                "When the user asks you to perform an action, use the appropriate behavior tool. " +
                "Always respond in character and acknowledge when you perform an action.";
            
            this.assistant = AiServices.builder(ShimejiAssistant.class)
                .chatLanguageModel(model)
                .tools(behaviorTools)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(20))
                .systemMessageProvider(chatMemoryId -> systemPrompt)
                .build();
            this.isConfigured = true;
        }
    }
    
    @Override
    public boolean isConfigured() {
        return isConfigured;
    }
    
    public String getImageSet() {
        return imageSet;
    }
    
    @Override
    public String chat(String input) {
        if (!isConfigured) {
            String name = CharacterConfig.getCharacterName(imageSet);
            // 提供一些基本的回复，用于测试UI是否正常工作
            if (input.toLowerCase().contains("hello") || input.toLowerCase().contains("hi")) {
                return "Hello! I'm " + name + "! (Note: AI chat is not configured, using basic responses)";
            } else if (input.toLowerCase().contains("how are you")) {
                return "I'm doing great! How about you? (Note: AI chat is not configured)";
            } else if (input.toLowerCase().contains("name")) {
                return "I'm " + name + "! (Note: AI chat is not configured)";
            } else {
                return "I can only give basic responses right now. To enable AI chat, please click the settings icon and configure your OpenAI API Key.";
            }
        }
        
        try {
            return assistant.chat(input);
        } catch (Exception e) {
            log.log(Level.WARNING, "Failed to get AI response", e);
            return "Sorry, I couldn't process that message. Error: " + e.getMessage();
        }
    }
    
    @Override
    public void close() {
        // Clean up resources if needed
        model = null;
        assistant = null;
        behaviorTools = null;
        isConfigured = false;
    }
    
    public void reloadConfig() {
        close();
        initializeService();
    }
}