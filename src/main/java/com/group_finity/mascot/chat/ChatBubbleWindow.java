package com.group_finity.mascot.chat;

import com.group_finity.mascot.Mascot;
import com.group_finity.mascot.config.CharacterConfig;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.ui.FlatButtonBorder;
import com.formdev.flatlaf.ui.FlatRoundBorder;

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
        
        // 设置 FlatLaf 主题
        FlatLightLaf.setup();
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize FlatLaf");
        }
        
        setUndecorated(true);
        setAlwaysOnTop(true);
        
        // 创建主面板，使用圆角边框
        JPanel mainPanel = new JPanel(new BorderLayout(5, 5));
        mainPanel.setBorder(new FlatRoundBorder());
        mainPanel.setBackground(UIManager.getColor("Panel.background"));
        
        // 创建聊天区域
        chatArea = new JTextArea(10, 30);
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setFont(new Font("Dialog", Font.PLAIN, 14));
        chatArea.setBackground(UIManager.getColor("TextArea.background"));
        
        // 创建滚动面板
        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        scrollPane.setBackground(UIManager.getColor("ScrollPane.background"));
        
        // 创建输入框
        inputField = new JTextField(30);
        inputField.addActionListener(e -> sendMessage());
        inputField.setFont(new Font("Dialog", Font.PLAIN, 14));
        inputField.setBorder(new FlatRoundBorder());
        
        // 创建按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        buttonPanel.setOpaque(false);
        
        // 创建标题标签
        titleLabel = new JLabel(CharacterConfig.getCharacterName(imageSet));
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 14));
        titleLabel.setBorder(new EmptyBorder(5, 10, 5, 0));
        
        // 创建按钮
        settingsButton = createIconButton("⚙", "Settings");
        JButton characterButton = createIconButton("👤", "Character Settings");
        JButton closeButton = createIconButton("×", "Close");
        
        // 设置按钮事件
        settingsButton.addActionListener(e -> showSettings());
        characterButton.addActionListener(e -> showCharacterSettings());
        closeButton.addActionListener(e -> dispose());
        
        // 创建顶部面板
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(titleLabel, BorderLayout.WEST);
        
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        controlPanel.setOpaque(false);
        controlPanel.add(characterButton);
        controlPanel.add(settingsButton);
        controlPanel.add(closeButton);
        topPanel.add(controlPanel, BorderLayout.EAST);
        
        // 创建发送按钮
        sendButton = new JButton("Send");
        sendButton.addActionListener(e -> sendMessage());
        sendButton.setBorder(new FlatButtonBorder());
        
        // 创建输入面板
        JPanel inputPanel = new JPanel(new BorderLayout(5, 0));
        inputPanel.setOpaque(false);
        inputPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        
        // 组装主面板
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(inputPanel, BorderLayout.SOUTH);
        
        // 添加拖动功能
        addDragSupport(mainPanel);
        
        setContentPane(mainPanel);
        pack();
        
        // 设置窗口样式
        setBackground(new Color(255, 255, 255, 240));
        getRootPane().setWindowDecorationStyle(JRootPane.NONE);
        
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
    
    private JButton createIconButton(String text, String tooltip) {
        JButton button = new JButton(text);
        button.setToolTipText(tooltip);
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setFont(new Font("Dialog", Font.PLAIN, 16));
        return button;
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