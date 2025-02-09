package com.group_finity.mascot.chat;

import com.group_finity.mascot.config.CharacterConfig;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

public class ChatBubbleWindow extends JDialog {
    private final JTextArea chatArea;
    private final JTextField inputField;
    private AIChatService chatService;
    private Point dragStart;
    private final String imageSet;
    private JLabel titleLabel;
    
    public ChatBubbleWindow(Component owner, AIChatService chatService) {
        super(SwingUtilities.getWindowAncestor(owner));
        this.chatService = chatService;
        this.imageSet = (chatService instanceof DefaultAIChatService) ? 
            ((DefaultAIChatService) chatService).getImageSet() : "shimeji";
        setUndecorated(true);
        
        enableEvents(AWTEvent.WINDOW_EVENT_MASK | AWTEvent.KEY_EVENT_MASK | AWTEvent.FOCUS_EVENT_MASK);
        
        setBackground(new Color(0, 0, 0, 0));
        
        JPanel rootPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                if (g instanceof Graphics2D g2d) {
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.95f));
                    g2d.setColor(new Color(255, 255, 255));
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                }
            }
        };
        rootPanel.setLayout(new BorderLayout());
        rootPanel.setOpaque(false);
        rootPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        
        JPanel titlePanel = new JPanel(new BorderLayout(5, 0));
        titlePanel.setOpaque(true);
        titlePanel.setBackground(new Color(240, 240, 240));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        titlePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                dragStart = e.getPoint();
                SwingUtilities.convertPointToScreen(dragStart, titlePanel);
                dragStart.x -= getX();
                dragStart.y -= getY();
            }
        });
        
        titlePanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Point current = e.getPoint();
                SwingUtilities.convertPointToScreen(current, titlePanel);
                setLocation(current.x - dragStart.x, current.y - dragStart.y);
            }
        });
        
        chatArea = new JTextArea(8, 30);
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setBackground(new Color(250, 250, 250));
        chatArea.setFont(new Font("Dialog", Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setOpaque(true);
        scrollPane.getViewport().setOpaque(true);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        
        inputField = new JTextField(30);
        inputField.setBackground(new Color(255, 255, 255));
        inputField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        
        inputField.setEditable(true);
        inputField.setEnabled(true);
        inputField.setFocusable(true);
        
        inputField.addActionListener(e -> sendMessage());
        
        // 创建标题栏右侧按钮面板
        JPanel titleButtonPanel = createTitleButtonPanel();
        // 创建发送按钮
        JButton sendButton = createSendButton();
        
        JPanel inputPanel = new JPanel(new BorderLayout(5, 0));
        inputPanel.setOpaque(true);
        inputPanel.setBackground(new Color(240, 240, 240));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        
        String characterName = getCharacterName();
        titleLabel = new JLabel("Chat with " + characterName);
        titleLabel.setOpaque(false);
        titlePanel.add(titleLabel, BorderLayout.WEST);
        titlePanel.add(titleButtonPanel, BorderLayout.EAST);
        
        JPanel contentPanel = new JPanel(new BorderLayout(0, 5));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        
        rootPanel.add(titlePanel, BorderLayout.NORTH);
        rootPanel.add(contentPanel, BorderLayout.CENTER);
        rootPanel.add(inputPanel, BorderLayout.SOUTH);
        
        setContentPane(rootPanel);
        
        setModal(false);
        setAlwaysOnTop(true);
        setFocusable(true);
        setFocusableWindowState(true);
        setAutoRequestFocus(true);
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e) {
                SwingUtilities.invokeLater(() -> {
                    inputField.requestFocusInWindow();
                });
            }
        });
        
        pack();
        
        String greeting = CharacterConfig.getGreeting(imageSet);
        appendMessage(characterName + ": " + greeting);
    }
    
    private String getCharacterName() {
        String name = CharacterConfig.getCharacterName(imageSet);
        return name != null ? name : "Shimeji";
    }
    
    private JPanel createTitleButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        buttonPanel.setOpaque(false);
        
        final JButton characterConfigButton = new JButton("👤");
        characterConfigButton.setFocusPainted(false);
        characterConfigButton.setContentAreaFilled(true);
        characterConfigButton.setBackground(new Color(240, 240, 240));
        characterConfigButton.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
        characterConfigButton.addActionListener(e -> CharacterConfig.showConfigDialog(imageSet, this::reloadChatService));
        
        final JButton configButton = createConfigButton();
        final JButton closeButton = createCloseButton();
        
        buttonPanel.add(characterConfigButton);
        buttonPanel.add(configButton);
        buttonPanel.add(closeButton);
        
        return buttonPanel;
    }
    
    private JButton createConfigButton() {
        final JButton configButton = new JButton("⚙");
        configButton.setFocusPainted(false);
        configButton.setContentAreaFilled(true);
        configButton.setBackground(new Color(240, 240, 240));
        configButton.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
        configButton.addActionListener(e -> showConfigDialog());
        return configButton;
    }
    
    private JButton createCloseButton() {
        final JButton closeButton = new JButton("×");
        closeButton.setFocusPainted(false);
        closeButton.setContentAreaFilled(true);
        closeButton.setBackground(new Color(240, 240, 240));
        closeButton.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
        closeButton.addActionListener(e -> setVisible(false));
        return closeButton;
    }
    
    private JButton createSendButton() {
        final JButton sendButton = new JButton("Send");
        sendButton.setFocusPainted(false);
        sendButton.setContentAreaFilled(true);
        sendButton.setBackground(new Color(240, 240, 240));
        sendButton.addActionListener(e -> sendMessage());
        return sendButton;
    }
    
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        SwingUtilities.invokeLater(this::repaint);
    }
    
    private void sendMessage() {
        String input = inputField.getText().trim();
        if (!input.isEmpty()) {
            appendMessage("You: " + input);
            String response = chatService.chat(input);
            appendMessage(getCharacterName() + ": " + response);
            inputField.setText("");
            inputField.requestFocusInWindow();
        }
    }
    
    public void appendMessage(String message) {
        chatArea.append(message + "\n");
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }
    
    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
            requestFocus();
            SwingUtilities.invokeLater(() -> {
                inputField.requestFocusInWindow();
            });
        }
    }
    
    public void showAboveMascot(Point mascotLocation) {
        setLocation(mascotLocation.x, mascotLocation.y - getHeight());
        setVisible(true);
        toFront();
        SwingUtilities.invokeLater(() -> {
            requestFocus();
            inputField.requestFocusInWindow();
        });
    }
    
    private void updateChatService(DefaultAIChatService newService) {
        if (newService.isConfigured()) {
            if (chatService != null) {
                chatService.close();
            }
            chatService = newService;
            appendMessage("System: API Key configured successfully!");
        }
    }
    
    private void showConfigDialog() {
        final ApiKeyConfigDialog dialog = new ApiKeyConfigDialog(SwingUtilities.getWindowAncestor(this));
        dialog.setVisible(true);
        
        // 如果API key已更改，重新创建聊天服务
        if (ApiKeyConfigDialog.getApiKey() != null) {
            String currentImageSet = "shimeji"; // 默认值
            if (chatService instanceof DefaultAIChatService) {
                currentImageSet = ((DefaultAIChatService) chatService).getImageSet();
            }
            DefaultAIChatService newService = new DefaultAIChatService(currentImageSet);
            updateChatService(newService);
        }
    }
    
    public void reloadChatService() {
        if (chatService != null) {
            chatService.close();
        }
        // 重新创建聊天服务
        chatService = new DefaultAIChatService(imageSet);
        
        // 更新窗口标题
        String characterName = CharacterConfig.getCharacterName(imageSet);
        String greeting = CharacterConfig.getGreeting(imageSet);
        
        System.out.println("Reloading chat service for imageSet: " + imageSet);
        System.out.println("Character name: " + characterName);
        System.out.println("Greeting: " + greeting);
        
        // 更新标题标签
        titleLabel.setText("Chat with " + characterName);
        
        // 显示更新消息
        appendMessage("\n--- Character settings updated ---");
        appendMessage(characterName + ": " + greeting);
        
        // 重新布局窗口
        revalidate();
        repaint();
    }
}