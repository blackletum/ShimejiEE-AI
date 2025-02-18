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
import javax.swing.text.*;

public class ChatBubbleWindow extends JDialog {
    private final JTextPane chatArea;
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
    private static final Color USER_MESSAGE_COLOR = new Color(0, 102, 204);
    private static final Color SHIMEJI_MESSAGE_COLOR = new Color(51, 51, 51);
    private static final Color SHIMEJI_NAME_COLOR = new Color(70, 70, 70);
    
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
        chatArea = new JTextPane();
        chatArea.setEditable(false);
        chatArea.setBackground(UIManager.getColor("TextArea.background"));
        chatArea.setFont(new Font("Dialog", Font.PLAIN, 14));
        chatArea.setBorder(new EmptyBorder(5, 5, 5, 5));
        chatArea.setPreferredSize(new Dimension(300, 200));
        
        // 设置初始文档样式
        StyledDocument doc = chatArea.getStyledDocument();
        SimpleAttributeSet style = new SimpleAttributeSet();
        StyleConstants.setAlignment(style, StyleConstants.ALIGN_LEFT);
        doc.setParagraphAttributes(0, doc.getLength(), style, false);
        
        // 创建滚动面板
        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        scrollPane.setBackground(UIManager.getColor("ScrollPane.background"));
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setMinimumSize(new Dimension(280, 200));
        scrollPane.setPreferredSize(new Dimension(280, 200));
        
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
        topPanel.setOpaque(true);
        topPanel.setBackground(UIManager.getColor("Panel.background"));
        topPanel.add(titleLabel, BorderLayout.WEST);
        
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // 强制子组件重绘
                for (Component comp : getComponents()) {
                    comp.repaint();
                }
            }
        };
        controlPanel.setOpaque(true);
        controlPanel.setBackground(UIManager.getColor("Panel.background"));
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
        inputPanel.setOpaque(true);
        inputPanel.setBackground(UIManager.getColor("Panel.background"));
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
        setMinimumSize(getSize());
        
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
        
        // 添加窗口焦点监听器，确保内容正确显示
        addWindowFocusListener(new WindowFocusListener() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                repaint();  // 获得焦点时重绘
            }
            
            @Override
            public void windowLostFocus(WindowEvent e) {
                repaint();  // 失去焦点时重绘
            }
        });
        
        // 修改主面板大小
        mainPanel.setMinimumSize(new Dimension(300, 400));  // 改小尺寸
        mainPanel.setPreferredSize(new Dimension(300, 400));
        
        // 添加重绘定时器
        Timer repaintTimer = new Timer(100, e -> {
            repaint();  // 定期重绘整个窗口
            for (Component comp : controlPanel.getComponents()) {
                comp.repaint();
            }
        });
        repaintTimer.start();
    }
    
    private JButton createIconButton(String text, String tooltip) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                // 确保按钮始终重绘
                setContentAreaFilled(true);
                setOpaque(true);
                super.paintComponent(g);
            }
        };
        button.setToolTipText(tooltip);
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        button.setFocusPainted(false);
        button.setBackground(UIManager.getColor("Panel.background"));
        button.setFont(new Font("Dialog", Font.PLAIN, 16));
        
        // 添加鼠标事件监听器来强制重绘
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.repaint();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.repaint();
            }
        });
        
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
            String shimejName = CharacterConfig.getCharacterName(imageSet);
            // 在新线程中处理AI响应
            new Thread(() -> {
                try {
                    String response = chatService.chat(message);
                    SwingUtilities.invokeLater(() -> 
                        appendMessage(shimejName, response));
                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> 
                        appendMessage("Error", "Failed to get response: " + e.getMessage()));
                }
            }).start();
        }
    }
    
    private void appendMessage(String sender, String message) {
        StyledDocument doc = chatArea.getStyledDocument();
        
        if (sender.equals("You")) {
            // 用户消息靠右对齐，使用普通样式
            SimpleAttributeSet style = new SimpleAttributeSet();
            StyleConstants.setAlignment(style, StyleConstants.ALIGN_RIGHT);
            
            try {
                doc.insertString(doc.getLength(), message + "\n\n", style);  // 添加两个换行
                doc.setParagraphAttributes(doc.getLength() - message.length() - 2, 
                                         message.length() + 2, style, false);
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        } else {
            // Shimeji消息处理
            SimpleAttributeSet baseStyle = new SimpleAttributeSet();
            StyleConstants.setAlignment(baseStyle, StyleConstants.ALIGN_LEFT);
            
            // 名字样式
            SimpleAttributeSet nameStyle = new SimpleAttributeSet();
            StyleConstants.setBold(nameStyle, true);
            StyleConstants.setForeground(nameStyle, new Color(70, 70, 70));
            
            // 引号内容样式
            SimpleAttributeSet quoteStyle = new SimpleAttributeSet();
            StyleConstants.setForeground(quoteStyle, new Color(153, 101, 21));
            
            // 加粗样式 
            SimpleAttributeSet boldStyle = new SimpleAttributeSet();
            StyleConstants.setBold(boldStyle, true);
            StyleConstants.setForeground(boldStyle, new Color(0, 150, 136));  // Material Design Teal
            
            // 斜体样式 
            SimpleAttributeSet italicStyle = new SimpleAttributeSet();
            StyleConstants.setItalic(italicStyle, true);
            StyleConstants.setForeground(italicStyle, new Color(33, 150, 243));  // Material Design Blue
            
            try {
                // 添加发送者名称
                doc.insertString(doc.getLength(), sender + ": ", nameStyle);
                
                // 处理消息内容
                int pos = 0;
                while (pos < message.length()) {
                    // 检查Markdown语法
                    if (message.startsWith("**", pos)) {
                        // 处理加粗文本
                        int endPos = message.indexOf("**", pos + 2);
                        if (endPos != -1) {
                            String boldText = message.substring(pos + 2, endPos);
                            doc.insertString(doc.getLength(), boldText, boldStyle);
                            pos = endPos + 2;
                            continue;
                        }
                    } else if (message.startsWith("*", pos)) {
                        // 处理斜体文本
                        int endPos = message.indexOf("*", pos + 1);
                        if (endPos != -1) {
                            String italicText = message.substring(pos + 1, endPos);
                            doc.insertString(doc.getLength(), italicText, italicStyle);
                            pos = endPos + 1;
                            continue;
                        }
                    } else if (message.charAt(pos) == '"') {
                        // 处理引号内容
                        int endPos = message.indexOf('"', pos + 1);
                        if (endPos != -1) {
                            String quotedText = message.substring(pos + 1, endPos);
                            doc.insertString(doc.getLength(), "\"" + quotedText + "\"", quoteStyle);
                            pos = endPos + 1;
                            continue;
                        }
                    }
                    
                    // 如果没有匹配到特殊格式，就输出普通字符
                    doc.insertString(doc.getLength(), String.valueOf(message.charAt(pos)), baseStyle);
                    pos++;
                }
                
                // 添加两个换行来增加间距
                doc.insertString(doc.getLength(), "\n\n", baseStyle);
                
                // 设置整个段落的对齐方式
                doc.setParagraphAttributes(doc.getLength() - message.length() - sender.length() - 4,
                                         message.length() + sender.length() + 4, baseStyle, false);
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }
        
        // 滚动到最新消息
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