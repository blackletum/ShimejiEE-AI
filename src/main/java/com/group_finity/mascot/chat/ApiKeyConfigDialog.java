package com.group_finity.mascot.chat;

import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.ui.FlatRoundBorder;
import com.formdev.flatlaf.ui.FlatButtonBorder;

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
        
        // 设置 FlatLaf 主题
        FlatLightLaf.setup();
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize FlatLaf");
        }
        
        // 设置窗口属性
        setResizable(false);
        
        // 创建主面板
        JPanel mainPanel = new JPanel(new BorderLayout(8, 8));
        mainPanel.setBorder(new EmptyBorder(12, 12, 12, 12));
        mainPanel.setBackground(UIManager.getColor("Panel.background"));
        
        // 创建内容面板
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 0, 4, 0);
        
        // API Key 说明
        JLabel infoLabel = new JLabel("Please enter your OpenAI API Key:");
        infoLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        contentPanel.add(infoLabel, gbc);
        
        // API Key 输入框
        apiKeyField = new JPasswordField(25);
        originalKey = prefs.get(PREF_API_KEY, "");
        apiKeyField.setText(originalKey);
        apiKeyField.setBorder(new FlatRoundBorder());
        apiKeyField.setFont(new Font("Dialog", Font.PLAIN, 12));
        gbc.gridy = 1;
        gbc.insets = new Insets(8, 0, 12, 0);
        contentPanel.add(apiKeyField, gbc);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttonPanel.setOpaque(false);
        
        // 保存按钮
        JButton saveButton = new JButton("Save");
        saveButton.setFont(new Font("Dialog", Font.PLAIN, 12));
        saveButton.setBorder(new FlatButtonBorder());
        
        // 取消按钮
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Dialog", Font.PLAIN, 12));
        cancelButton.setBorder(new FlatButtonBorder());
        
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
        
        // 组装主面板
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
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