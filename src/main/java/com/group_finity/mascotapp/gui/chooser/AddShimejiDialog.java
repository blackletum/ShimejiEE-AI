package com.group_finity.mascotapp.gui.chooser;

import com.group_finity.mascotapp.Constants;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.*;
import java.util.Properties;
import java.nio.charset.StandardCharsets;

public class AddShimejiDialog extends JDialog {
    private final JTextField nameField;
    private JFileChooser fileChooser;
    
    public AddShimejiDialog(Window owner) {
        super(owner, "Add New Shimeji", ModalityType.APPLICATION_MODAL);
        
        setLayout(new BorderLayout(10, 10));
        
        // 创建主面板
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Shimeji 名称输入
        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(new JLabel("Shimeji Name:"), gbc);
        
        nameField = new JTextField(20);
        gbc.gridx = 1;
        mainPanel.add(nameField, gbc);
        
        // 选择文件按钮
        JButton selectFileButton = new JButton("Select Shimeji Files");
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(selectFileButton, gbc);
        
        // 说明标签
        JLabel infoLabel = new JLabel("<html>Please select a folder containing Shimeji images.<br>" +
            "The folder should include: shime1.png, shime2.png, etc.</html>");
        gbc.gridy = 2;
        mainPanel.add(infoLabel, gbc);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addButton = new JButton("Add");
        JButton cancelButton = new JButton("Cancel");
        
        // 初始化文件选择器
        fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setMultiSelectionEnabled(false);
        
        selectFileButton.addActionListener(e -> {
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                selectFileButton.setText("Selected: " + selectedFile.getName());
            }
        });
        
        addButton.addActionListener(e -> {
            try {
                String shimejiFolderName = nameField.getText().trim();
                if (shimejiFolderName.isEmpty()) {
                    JOptionPane.showMessageDialog(this, 
                        "Please enter a name for your Shimeji", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                File sourceDir = fileChooser.getSelectedFile();
                if (sourceDir == null || !sourceDir.exists()) {
                    JOptionPane.showMessageDialog(this, 
                        "Please select a valid Shimeji files folder", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // 创建目标目录
                Path targetDir = Constants.JAR_DIR.resolve("img").resolve(shimejiFolderName);
                Files.createDirectories(targetDir);
                
                // 复制文件
                File[] files = sourceDir.listFiles((dir, name) -> 
                    name.toLowerCase().endsWith(".png") || 
                    name.toLowerCase().endsWith(".properties"));
                
                if (files == null || files.length == 0) {
                    JOptionPane.showMessageDialog(this, 
                        "No valid Shimeji files found in the selected folder", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                for (File file : files) {
                    Files.copy(file.toPath(), 
                        targetDir.resolve(file.getName()), 
                        StandardCopyOption.REPLACE_EXISTING);
                }
                
                // 创建默认的 character.properties
                Path characterProps = targetDir.resolve("character.properties");
                if (!Files.exists(characterProps)) {
                    Properties props = new Properties();
                    props.setProperty("name", shimejiFolderName);
                    props.setProperty("personality", "a cute and friendly desktop companion");
                    props.setProperty("greeting", "Hello! How can I help you today?");
                    
                    // 使用 UTF-8 编码写入文件
                    try (OutputStreamWriter writer = new OutputStreamWriter(
                            Files.newOutputStream(characterProps), StandardCharsets.UTF_8)) {
                        props.store(writer, "Character configuration (UTF-8 encoding)");
                    }
                }
                
                JOptionPane.showMessageDialog(this, 
                    "Shimeji added successfully!\nPlease restart the application to use your new Shimeji.", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                dispose();
                
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error adding Shimeji: " + ex.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });
        
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);
        
        // 添加到主窗口
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // 设置对话框属性
        pack();
        setLocationRelativeTo(owner);
    }
} 