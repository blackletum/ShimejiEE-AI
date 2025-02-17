package com.group_finity.mascot.tools;

import com.group_finity.mascot.Mascot;
import dev.langchain4j.agent.tool.Tool;
import com.group_finity.mascot.config.Configuration;
import com.group_finity.mascot.exception.BehaviorInstantiationException;
import com.group_finity.mascot.exception.CantBeAliveException;
import java.io.IOException;
import java.awt.Desktop;
import java.io.File;
import java.net.URI;
import javax.swing.filechooser.FileSystemView;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

public class ShimejiBehaviorTools {
    private final Mascot mascot;
    private final String userDesktopPath;
    private final String publicDesktopPath;

    public ShimejiBehaviorTools(Mascot mascot) {
        this.mascot = mascot;
        this.userDesktopPath = FileSystemView.getFileSystemView().getHomeDirectory().getPath();
        this.publicDesktopPath = System.getenv("PUBLIC") + "\\Desktop";
    }

    @Tool("Makes the Shimeji chase the mouse cursor")
    public void chaseMouse() throws BehaviorInstantiationException, CantBeAliveException {
        setBehavior("ChaseMouse");
    }

    @Tool("Makes the Shimeji sit and face the mouse cursor")
    public void sitAndFaceMouse() throws BehaviorInstantiationException, CantBeAliveException {
        setBehavior("SitAndFaceMouse");
    }

    @Tool("Makes the Shimeji sit and spin its head")
    public void sitAndSpinHead() throws BehaviorInstantiationException, CantBeAliveException {
        setBehavior("SitAndSpinHead");
    }

    @Tool("Makes the Shimeji fall")
    public void fall() throws BehaviorInstantiationException, CantBeAliveException {
        setBehavior("Fall");
    }

    @Tool("Makes the Shimeji look like being dragged")
    public void dragged() throws BehaviorInstantiationException, CantBeAliveException {
        setBehavior("Dragged");
    }

    @Tool("Makes the Shimeji look like being thrown")
    public void thrown() throws BehaviorInstantiationException, CantBeAliveException {
        setBehavior("Thrown");
    }

    @Tool("Makes the Shimeji pull up")
    public void pullUp() throws BehaviorInstantiationException, CantBeAliveException {
        setBehavior("PullUp");
    }

    @Tool("Makes the Shimeji split into two")
    public void splitIntoTwo() throws BehaviorInstantiationException, CantBeAliveException {
        setBehavior("SplitIntoTwo");
    }

    @Tool("Makes the Shimeji stand up")
    public void standUp() throws BehaviorInstantiationException, CantBeAliveException {
        setBehavior("StandUp");
    }

    @Tool("Makes the Shimeji sit down")
    public void sitDown() throws BehaviorInstantiationException, CantBeAliveException {
        setBehavior("SitDown");
    }

    @Tool("Makes the Shimeji sit while dangling legs")
    public void sitWhileDanglingLegs() throws BehaviorInstantiationException, CantBeAliveException {
        setBehavior("SitWhileDanglingLegs");
    }

    @Tool("Makes the Shimeji lie down")
    public void lieDown() throws BehaviorInstantiationException, CantBeAliveException {
        setBehavior("LieDown");
    }

    @Tool("Makes the Shimeji hold onto wall")
    public void holdOntoWall() throws BehaviorInstantiationException, CantBeAliveException {
        setBehavior("HoldOntoWall");
    }

    @Tool("Makes the Shimeji fall from wall")
    public void fallFromWall() throws BehaviorInstantiationException, CantBeAliveException {
        setBehavior("FallFromWall");
    }

    @Tool("Makes the Shimeji hold onto ceiling")
    public void holdOntoCeiling() throws BehaviorInstantiationException, CantBeAliveException {
        setBehavior("HoldOntoCeiling");
    }

    @Tool("Makes the Shimeji walk along the work area floor")
    public void walkAlongWorkAreaFloor() throws BehaviorInstantiationException, CantBeAliveException {
        setBehavior("WalkAlongWorkAreaFloor");
    }

    @Tool("Makes the Shimeji run along the work area floor")
    public void runAlongWorkAreaFloor() throws BehaviorInstantiationException, CantBeAliveException {
        setBehavior("RunAlongWorkAreaFloor");
    }

    @Tool("Opens the default web browser with the specified URL")
    public String openWebBrowser(String url) {
        try {
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "https://" + url;
            }
            Desktop.getDesktop().browse(new URI(url));
            return "Successfully opened " + url + " in the web browser.";
        } catch (Exception e) {
            return "Failed to open web browser: " + e.getMessage();
        }
    }

    @Tool("Opens the specified file or application using the default program")
    public String openFile(String path) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                return "File not found: " + path;
            }
            Desktop.getDesktop().open(file);
            return "Successfully opened " + path;
        } catch (IOException e) {
            return "Failed to open file: " + e.getMessage();
        }
    }

    @Tool("Opens Windows Notepad")
    public String openNotepad() {
        try {
            Runtime.getRuntime().exec("notepad.exe");
            return "Successfully opened Notepad";
        } catch (IOException e) {
            return "Failed to open Notepad: " + e.getMessage();
        }
    }

    @Tool("Opens Windows Calculator")
    public String openCalculator() {
        try {
            Runtime.getRuntime().exec("calc.exe");
            return "Successfully opened Calculator";
        } catch (IOException e) {
            return "Failed to open Calculator: " + e.getMessage();
        }
    }

    @Tool("Opens Windows Paint")
    public String openPaint() {
        try {
            Runtime.getRuntime().exec("mspaint.exe");
            return "Successfully opened Paint";
        } catch (IOException e) {
            return "Failed to open Paint: " + e.getMessage();
        }
    }

    @Tool("Opens Windows Explorer")
    public String openExplorer() {
        try {
            Runtime.getRuntime().exec("explorer.exe");
            return "Successfully opened Windows Explorer";
        } catch (IOException e) {
            return "Failed to open Windows Explorer: " + e.getMessage();
        }
    }

    @Tool("Opens the specified program using its command")
    public String runProgram(String command) {
        try {
            // 基本安全检查
            if (command.contains("rm ") || command.contains("del ") || 
                command.contains("format ") || command.toLowerCase().contains("shutdown")) {
                return "Sorry, this command is not allowed for security reasons.";
            }
            Runtime.getRuntime().exec(command);
            return "Successfully executed: " + command;
        } catch (IOException e) {
            return "Failed to execute command: " + e.getMessage();
        }
    }

    private List<File> getAllDesktopFiles() {
        List<File> allFiles = new ArrayList<>();
        
        // 获取用户桌面文件
        File userDesktop = new File(userDesktopPath);
        File[] userFiles = userDesktop.listFiles();
        if (userFiles != null) {
            allFiles.addAll(Arrays.asList(userFiles));
        }
        
        // 获取公共桌面文件
        File publicDesktop = new File(publicDesktopPath);
        File[] publicFiles = publicDesktop.listFiles();
        if (publicFiles != null) {
            allFiles.addAll(Arrays.asList(publicFiles));
        }
        
        return allFiles;
    }

    @Tool("Lists all programs and files on the desktop")
    public String listDesktopItems() {
        try {
            List<File> files = getAllDesktopFiles();
            if (files.isEmpty()) {
                return "No items found on desktop.";
            }

            StringBuilder result = new StringBuilder("Items on desktop:\n");
            for (File file : files) {
                if (!file.isHidden()) {
                    String fileName = file.getName();
                    String fileType = "";
                    String location = file.getParent().equals(userDesktopPath) ? " (User Desktop)" : " (Public Desktop)";
                    
                    if (fileName.endsWith(".lnk")) {
                        fileType = " (Shortcut)";
                    } else if (fileName.endsWith(".exe")) {
                        fileType = " (Program)";
                    }
                    
                    // 移除文件扩展名以便于识别
                    String displayName = fileName;
                    int dotIndex = fileName.lastIndexOf('.');
                    if (dotIndex > 0) {
                        displayName = fileName.substring(0, dotIndex);
                    }
                    result.append("- ").append(displayName).append(fileType).append(location).append("\n");
                }
            }
            return result.toString();
        } catch (Exception e) {
            return "Failed to list desktop items: " + e.getMessage();
        }
    }

    @Tool("Opens a program or file from the desktop by its name")
    public String openDesktopItem(String itemName) {
        try {
            List<File> files = getAllDesktopFiles();
            if (files.isEmpty()) {
                return "Cannot access desktop.";
            }

            // 尝试查找最匹配的文件名
            File bestMatch = null;
            String lowerItemName = itemName.toLowerCase();
            
            // 首先尝试完全匹配（忽略扩展名）
            for (File file : files) {
                if (file.isHidden()) continue;
                
                String fileName = file.getName().toLowerCase();
                String nameWithoutExt = fileName;
                int dotIndex = fileName.lastIndexOf('.');
                if (dotIndex > 0) {
                    nameWithoutExt = fileName.substring(0, dotIndex);
                }
                
                if (nameWithoutExt.equals(lowerItemName)) {
                    bestMatch = file;
                    break;
                }
            }
            
            // 如果没有找到完全匹配，尝试部分匹配
            if (bestMatch == null) {
                for (File file : files) {
                    if (file.isHidden()) continue;
                    
                    String fileName = file.getName().toLowerCase();
                    String nameWithoutExt = fileName;
                    int dotIndex = fileName.lastIndexOf('.');
                    if (dotIndex > 0) {
                        nameWithoutExt = fileName.substring(0, dotIndex);
                    }
                    
                    if (nameWithoutExt.contains(lowerItemName)) {
                        bestMatch = file;
                        break;
                    }
                }
            }

            if (bestMatch != null) {
                Desktop.getDesktop().open(bestMatch);
                String location = bestMatch.getParent().equals(userDesktopPath) ? " from User Desktop" : " from Public Desktop";
                return "Successfully opened " + bestMatch.getName() + location;
            } else {
                return "Could not find '" + itemName + "' on either desktop. Use 'listDesktopItems' to see available items.";
            }
        } catch (IOException e) {
            return "Failed to open item: " + e.getMessage();
        }
    }

    @Tool("Searches for a program or file on the desktop")
    public String searchDesktopItems(String searchTerm) {
        try {
            List<File> files = getAllDesktopFiles();
            if (files.isEmpty()) {
                return "Cannot access desktop.";
            }

            List<String> matches = new ArrayList<>();
            String lowerSearchTerm = searchTerm.toLowerCase();
            
            for (File file : files) {
                if (!file.isHidden() && file.getName().toLowerCase().contains(lowerSearchTerm)) {
                    String location = file.getParent().equals(userDesktopPath) ? " (User Desktop)" : " (Public Desktop)";
                    matches.add(file.getName() + location);
                }
            }

            if (matches.isEmpty()) {
                return "No items found matching '" + searchTerm + "' on either desktop.";
            }

            StringBuilder result = new StringBuilder("Found these items:\n");
            for (String match : matches) {
                result.append("- ").append(match).append("\n");
            }
            return result.toString();
        } catch (Exception e) {
            return "Failed to search desktop items: " + e.getMessage();
        }
    }

    private void setBehavior(String behaviorName) throws BehaviorInstantiationException, CantBeAliveException {
        try {
            Configuration conf = mascot.getOwnImageSet().getConfiguration();
            mascot.setBehavior(conf.buildBehavior(behaviorName));
        } catch (Exception e) {
            throw new BehaviorInstantiationException("Failed to set behavior: " + behaviorName, e);
        }
    }
} 