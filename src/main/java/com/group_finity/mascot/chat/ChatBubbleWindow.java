package com.group_finity.mascot.chat;

import com.group_finity.mascot.Mascot;
import com.group_finity.mascot.config.CharacterConfig;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

public class ChatBubbleWindow extends JDialog {
    private final JTextArea chatArea;
    private final JTextField inputField;
    private final JButton sendButton;
    private final JButton settingsButton;
    private AIChatService chatService;
    private final Mascot mascot;
    private Point lastLocation;
    private boolean isDragging = false;
    private Point dragStart;
    private final String imageSet;
    private JLabel titleLabel;
    
    public ChatBubbleWindow(Window owner, Mascot mascot) {
        super(owner);
        this.mascot = mascot;
        this.imageSet = mascot.getImageSet();
        setUndecorated(true);
        setAlwaysOnTop(true);
        
        // 创建聊天区域
        chatArea = new JTextArea(10, 30);
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        
        // 创建滚动面板
        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        // 创建输入框
        inputField = new JTextField(30);
        inputField.addActionListener(e -> sendMessage());
        
        // 创建发送按钮
        sendButton = new JButton("发送");
        sendButton.addActionListener(e -> sendMessage());
        
        // 创建设置按钮
        settingsButton = new JButton("⚙");
        settingsButton.addActionListener(e -> showSettings());
        
        // 创建角色设置按钮
        JButton characterButton = new JButton("👤");
        characterButton.addActionListener(e -> showCharacterSettings());
        
        // 创建关闭按钮
        JButton closeButton = new JButton("×");
        closeButton.addActionListener(e -> dispose());
        
        // 创建按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        buttonPanel.add(characterButton);
        buttonPanel.add(settingsButton);
        buttonPanel.add(closeButton);
        
        // 创建输入面板
        JPanel inputPanel = new JPanel(new BorderLayout(5, 0));
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        
        // 设置主面板
        JPanel mainPanel = new JPanel(new BorderLayout(5, 5));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        mainPanel.add(buttonPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(inputPanel, BorderLayout.SOUTH);
        
        // 添加拖动功能
        addDragSupport(mainPanel);
        
        setContentPane(mainPanel);
        pack();
        
        // 初始化聊天服务
        chatService = new DefaultAIChatService(mascot.getImageSet(), mascot);
        
        // 显示欢迎消息
        if (chatService.isConfigured()) {
            String shimejName = CharacterConfig.getCharacterName(imageSet);
            String greeting = CharacterConfig.getGreeting(imageSet);
            appendMessage(shimejName, greeting != null && !greeting.isEmpty() ? greeting : "Chat is ready! Type your message and press Enter to chat.");
        } else {
            String shimejName = CharacterConfig.getCharacterName(imageSet);
            appendMessage(shimejName, "Please configure your OpenAI API Key in settings to enable chat.");
        }
    }
    
    private void addDragSupport(JPanel panel) {
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                isDragging = true;
                dragStart = e.getPoint();
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                isDragging = false;
                lastLocation = getLocation();
            }
        });
        
        panel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (isDragging) {
                    Point current = e.getLocationOnScreen();
                    setLocation(current.x - dragStart.x, 
                              current.y - dragStart.y);
                }
            }
        });
    }
    
    private void sendMessage() {
        String message = inputField.getText().trim();
        if (!message.isEmpty()) {
            appendMessage("You", message);
            inputField.setText("");
            
            // 在新线程中处理AI响应
            new Thread(() -> {
                try {
                    String response = chatService.chat(message);
                    SwingUtilities.invokeLater(() -> 
                        appendMessage(mascot.getImageSet(), response));
                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> 
                        appendMessage("Error", "Failed to get response: " + e.getMessage()));
                }
            }).start();
        }
    }
    
    private void appendMessage(String sender, String message) {
        chatArea.append(sender + ": " + message + "\n");
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }
    
    private void showSettings() {
        ApiKeyConfigDialog dialog = new ApiKeyConfigDialog(this);
        dialog.setVisible(true);
        
        // 如果API密钥已更改，重新创建聊天服务
        if (dialog.isApiKeyUpdated()) {
            if (chatService != null) {
                chatService.close();
            }
            chatService = new DefaultAIChatService(mascot.getImageSet(), mascot);
            if (chatService.isConfigured()) {
                String shimejName = CharacterConfig.getCharacterName(imageSet);
                appendMessage(shimejName, "API Key updated successfully!");
            }
        }
    }
    
    private void showCharacterSettings() {
        CharacterConfig.showConfigDialog(mascot.getImageSet(), () -> {
            // 当保存角色设置后，重新加载聊天服务以应用新的个性设置
            String shimejName = CharacterConfig.getCharacterName(imageSet);
            appendMessage(shimejName, "Updating my personality settings...");
            reloadChatService();
        });
    }
    
    public void reloadChatService() {
        if (chatService != null) {
            chatService.close();
        }
        chatService = new DefaultAIChatService(mascot.getImageSet(), mascot);
        String shimejName = CharacterConfig.getCharacterName(imageSet);
        String greeting = CharacterConfig.getGreeting(imageSet);
        
        // 显示更新成功消息
        appendMessage(shimejName, "My personality settings have been updated! Let me introduce myself again...");
        
        // 显示新的问候语
        if (greeting != null && !greeting.isEmpty()) {
            appendMessage(shimejName, greeting);
        }
        
        // 提示用户可以继续对话
        appendMessage(shimejName, "You can continue chatting with me now!");
    }
    
    @Override
    public void setVisible(boolean visible) {
        if (visible && lastLocation != null) {
            setLocation(lastLocation);
        }
        super.setVisible(visible);
        if (visible) {
            inputField.requestFocusInWindow();
        }
    }
    
    public void showAboveMascot(Point mascotLocation) {
        Point location = new Point(mascotLocation);
        location.y -= getHeight() + 10; // Position above mascot with some padding
        
        // Ensure window stays within screen bounds
        Rectangle screenBounds = GraphicsEnvironment.getLocalGraphicsEnvironment()
            .getDefaultScreenDevice().getDefaultConfiguration().getBounds();
            
        if (location.x + getWidth() > screenBounds.width) {
            location.x = screenBounds.width - getWidth();
        }
        if (location.x < 0) {
            location.x = 0;
        }
        if (location.y < 0) {
            location.y = mascotLocation.y + 10; // Show below if not enough space above
        }
        
        setLocation(location);
        setVisible(true);
        inputField.requestFocus();
    }
}