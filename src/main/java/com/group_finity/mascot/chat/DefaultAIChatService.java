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
import javax.swing.JOptionPane;

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
        try {
            initializeService();
        } catch (Exception e) {
            log.log(Level.SEVERE, "Failed to initialize chat service", e);
            handleInitializationError(e);
        }
    }
    
    private void initializeService() {
        log.info("Initializing chat service for imageSet: " + imageSet);
        
        // 验证角色配置
        if (!validateCharacterConfig()) {
            log.warning("Using default character configuration");
        }
        
        // 初始化API配置
        initializeApiConfiguration();
    }
    
    private boolean validateCharacterConfig() {
        try {
            String name = CharacterConfig.getCharacterName(imageSet);
            String personality = CharacterConfig.getPersonality(imageSet);
            String greeting = CharacterConfig.getGreeting(imageSet);
            
            return name != null && !name.trim().isEmpty() 
                && personality != null && !personality.trim().isEmpty()
                && greeting != null && !greeting.trim().isEmpty();
        } catch (Exception e) {
            log.log(Level.WARNING, "Character config validation failed", e);
            return false;
        }
    }
    
    private void initializeApiConfiguration() {
        String apiKey = ApiKeyConfigDialog.getApiKey();
        if (apiKey == null || apiKey.trim().isEmpty()) {
            handleUnconfiguredState();
            return;
        }
        
        try {
            setupChatModel(apiKey);
            setupBehaviorTools();
            setupAssistant();
            this.isConfigured = true;
            log.info("Chat service initialized successfully");
        } catch (Exception e) {
            log.log(Level.SEVERE, "Failed to setup chat configuration", e);
            handleInitializationError(e);
        }
    }
    
    private void setupChatModel(String apiKey) {
        this.model = OpenAiChatModel.builder()
            .apiKey(apiKey)
            .modelName(GPT_4_O_MINI)
            .temperature(0.7)
            .strictTools(true)
            .build();
    }
    
    private void setupBehaviorTools() {
        this.behaviorTools = new ShimejiBehaviorTools(mascot);
    }
    
    private void setupAssistant() {
        String name = CharacterConfig.getCharacterName(imageSet);
        String personality = CharacterConfig.getPersonality(imageSet);
        
        // 使用更丰富的系统提示
        String systemPrompt = String.format(
            "You are %s. %s\n" +
            "You can control my actions using various behaviors. " +
            "When the user asks you to perform an action, use the appropriate behavior tool. " +
            "Always respond in character and acknowledge when you perform an action.\n" +
            "If you encounter any errors, explain them politely to the user.",
            name, personality
        );
        
        this.assistant = AiServices.builder(ShimejiAssistant.class)
            .chatLanguageModel(model)
            .tools(behaviorTools)
            .chatMemory(MessageWindowChatMemory.withMaxMessages(20))
            .systemMessageProvider(chatMemoryId -> systemPrompt)
            .build();
    }
    
    private void handleUnconfiguredState() {
        log.warning("OpenAI API Key not configured - using fallback responses");
        this.model = null;
        this.assistant = null;
        this.behaviorTools = null;
        this.isConfigured = false;
    }
    
    private void handleInitializationError(Exception e) {
        handleUnconfiguredState();
        JOptionPane.showMessageDialog(null,
            "Failed to initialize chat service: " + e.getMessage(),
            "Initialization Error",
            JOptionPane.ERROR_MESSAGE);
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
            return getDefaultResponse(input);
        }
        
        try {
            return assistant.chat(input);
        } catch (Exception e) {
            log.log(Level.WARNING, "Failed to get AI response", e);
            return getErrorResponse(e);
        }
    }
    
    private String getDefaultResponse(String input) {
        String name = CharacterConfig.getCharacterName(imageSet);
        input = input.toLowerCase();
        
        if (input.contains("hello") || input.contains("hi")) {
            return String.format("Hello! I'm %s! (Note: AI chat is not configured)", name);
        } else if (input.contains("how are you")) {
            return "I'm doing great! How about you? (Note: AI chat is not configured)";
        } else if (input.contains("name")) {
            return String.format("I'm %s! (Note: AI chat is not configured)", name);
        }
        
        return "I can only give basic responses right now. To enable AI chat, please click the settings icon and configure your OpenAI API Key.";
    }
    
    private String getErrorResponse(Exception e) {
        return String.format(
            "I apologize, but I encountered an error while processing your message.\n" +
            "Error: %s\n" +
            "Please try again or contact support if the problem persists.",
            e.getMessage()
        );
    }
    
    @Override
    public void close() {
        log.info("Closing chat service");
        model = null;
        assistant = null;
        behaviorTools = null;
        isConfigured = false;
    }
    
    public void reloadConfig() {
        log.info("Reloading chat service configuration");
        close();
        try {
            initializeService();
        } catch (Exception e) {
            log.log(Level.SEVERE, "Failed to reload configuration", e);
            handleInitializationError(e);
        }
    }
}