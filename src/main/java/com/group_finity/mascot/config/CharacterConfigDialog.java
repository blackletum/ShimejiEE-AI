package com.group_finity.mascot.config;

import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.ui.FlatRoundBorder;
import com.formdev.flatlaf.ui.FlatButtonBorder;
import com.group_finity.mascotapp.Constants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.*;
import java.nio.file.*;
import java.util.Properties;
import java.nio.charset.StandardCharsets;

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
        
        // 设置 FlatLaf 主题
        FlatLightLaf.setup();
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize FlatLaf");
        }
        
        // 设置窗口属性
        setResizable(false);
        
        // 加载当前配置
        Properties props = new Properties();
        try {
            Path configPath = Constants.JAR_DIR.resolve("img").resolve(imageSet).resolve("character.properties");
            if (Files.exists(configPath)) {
                try (InputStreamReader reader = new InputStreamReader(
                        Files.newInputStream(configPath), StandardCharsets.UTF_8)) {
                    props.load(reader);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // 创建主面板
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(UIManager.getColor("Panel.background"));
        
        // 创建标题面板
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        JLabel titleLabel = new JLabel("Character Settings");
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 16));
        titleLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        titlePanel.add(titleLabel, BorderLayout.WEST);
        
        // 创建表单面板
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // 名称
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setFont(new Font("Dialog", Font.BOLD, 12));
        nameField = new JTextField(25);
        nameField.setText(props.getProperty("name", "Shimeji"));
        nameField.setBorder(new FlatRoundBorder());
        nameField.setFont(new Font("Dialog", Font.PLAIN, 12));
        
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(nameLabel, gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(nameField, gbc);
        
        // 个性描述
        JLabel personalityLabel = new JLabel("Personality:");
        personalityLabel.setFont(new Font("Dialog", Font.BOLD, 12));
        personalityArea = new JTextArea(4, 25);
        personalityArea.setText(props.getProperty("personality", ""));
        personalityArea.setLineWrap(true);
        personalityArea.setWrapStyleWord(true);
        personalityArea.setFont(new Font("Dialog", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(personalityArea);
        scrollPane.setBorder(new FlatRoundBorder());
        
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.weightx = 0;
        gbc.insets = new Insets(8, 4, 4, 4);
        formPanel.add(personalityLabel, gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(scrollPane, gbc);
        
        // 欢迎语
        JLabel greetingLabel = new JLabel("Greeting:");
        greetingLabel.setFont(new Font("Dialog", Font.BOLD, 12));
        greetingField = new JTextField(25);
        greetingField.setText(props.getProperty("greeting", ""));
        greetingField.setBorder(new FlatRoundBorder());
        greetingField.setFont(new Font("Dialog", Font.PLAIN, 12));
        
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.weightx = 0;
        gbc.insets = new Insets(4, 4, 4, 4);
        formPanel.add(greetingLabel, gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(greetingField, gbc);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        JButton saveButton = new JButton("Save");
        saveButton.setFont(new Font("Dialog", Font.PLAIN, 12));
        saveButton.setBorder(new FlatButtonBorder());
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Dialog", Font.PLAIN, 12));
        cancelButton.setBorder(new FlatButtonBorder());
        
        saveButton.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                String personality = personalityArea.getText().trim();
                String greeting = greetingField.getText().trim();
                
                CharacterConfig.saveCharacterConfig(imageSet, name, personality, greeting);
                
                JOptionPane.showMessageDialog(
                    this,
                    "Character settings saved successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
                );
                
                if (onSaveCallback != null) {
                    onSaveCallback.run();
                }
                
                dispose();
            } catch (Exception ex) {
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
        
        // 组装主面板
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
}