package com.group_finity.mascot.chat;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.prefs.Preferences;

public class ApiKeyConfigDialog extends JDialog {
    private static final String PREF_API_KEY = "openai.api.key";
    private final JTextField apiKeyField;
    private final Preferences prefs;
    private boolean apiKeyUpdated = false;
    private String originalKey;
    
    public ApiKeyConfigDialog(Window owner) {
        super(owner, "OpenAI API Configuration", ModalityType.APPLICATION_MODAL);
        this.prefs = Preferences.userNodeForPackage(DefaultAIChatService.class);
        
        setLayout(new BorderLayout(10, 10));
        
        // 创建主面板
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        
        // API Key 说明
        JLabel infoLabel = new JLabel("<html>Please enter your OpenAI API Key.<br/>You can get it from: https://platform.openai.com/api-keys</html>");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 10, 0);
        mainPanel.add(infoLabel, gbc);
        
        // API Key 输入框
        JLabel apiKeyLabel = new JLabel("API Key:");
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(0, 0, 5, 5);
        mainPanel.add(apiKeyLabel, gbc);
        
        apiKeyField = new JPasswordField(40);
        originalKey = prefs.get(PREF_API_KEY, "");
        apiKeyField.setText(originalKey);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        mainPanel.add(apiKeyField, gbc);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        saveButton.addActionListener(e -> {
            String apiKey = apiKeyField.getText().trim();
            if (!apiKey.equals(originalKey)) {
                if (!apiKey.isEmpty()) {
                    prefs.put(PREF_API_KEY, apiKey);
                } else {
                    prefs.remove(PREF_API_KEY);
                }
                apiKeyUpdated = true;
            }
            dispose();
        });
        
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(owner);
    }
    
    public static String getApiKey() {
        Preferences prefs = Preferences.userNodeForPackage(DefaultAIChatService.class);
        return prefs.get(PREF_API_KEY, null);
    }
    
    public boolean isApiKeyUpdated() {
        return apiKeyUpdated;
    }
}