package com.group_finity.mascot.config;

import com.group_finity.mascotapp.Constants;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.*;
import java.util.Properties;

public class CharacterConfigDialog extends JDialog {
    private final JTextField nameField;
    private final JTextArea personalityArea;
    private final JTextField greetingField;
    private final String imageSet;
    private final Runnable onSaveCallback;
    
    public CharacterConfigDialog(String imageSet, Runnable onSaveCallback) {
        super((Frame) null, "Character Configuration", true);
        this.imageSet = imageSet;
        this.onSaveCallback = onSaveCallback;
        
        setLayout(new BorderLayout(10, 10));
        
        // 加载当前配置
        Properties props = new Properties();
        try {
            Path configPath = Constants.JAR_DIR.resolve("img").resolve(imageSet).resolve("character.properties");
            if (Files.exists(configPath)) {
                try (InputStream in = Files.newInputStream(configPath)) {
                    props.load(in);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // 创建表单面板
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // 名称
        nameField = new JTextField(30);
        nameField.setText(props.getProperty("name", "Shimeji"));
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        formPanel.add(nameField, gbc);
        
        // 个性描述
        personalityArea = new JTextArea(5, 30);
        personalityArea.setText(props.getProperty("personality", ""));
        personalityArea.setLineWrap(true);
        personalityArea.setWrapStyleWord(true);
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Personality:"), gbc);
        gbc.gridx = 1;
        formPanel.add(new JScrollPane(personalityArea), gbc);
        
        // 欢迎语
        greetingField = new JTextField(30);
        greetingField.setText(props.getProperty("greeting", ""));
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Greeting:"), gbc);
        gbc.gridx = 1;
        formPanel.add(greetingField, gbc);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        saveButton.addActionListener(e -> {
            try {
                // 保存配置
                String name = nameField.getText().trim();
                String personality = personalityArea.getText().trim();
                String greeting = greetingField.getText().trim();
                
                // 使用 CharacterConfig 来保存配置
                CharacterConfig.saveCharacterConfig(imageSet, name, personality, greeting);
                
                // 显示保存成功提示
                JOptionPane.showMessageDialog(
                    this,
                    "Character settings saved successfully!\nThe changes will take effect immediately.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
                );
                
                // 如果有回调，执行回调来更新聊天窗口
                if (onSaveCallback != null) {
                    onSaveCallback.run();
                }
                
                dispose();
            } catch (Exception ex) {
                // 显示错误信息
                JOptionPane.showMessageDialog(
                    this,
                    "Failed to save character settings:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
                ex.printStackTrace();
            }
        });
        
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        // 添加到主面板
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        // 设置对话框属性
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
}