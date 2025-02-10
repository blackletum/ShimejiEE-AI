package com.group_finity.mascotapp.gui.chooser;

import com.group_finity.mascot.imageset.ShimejiProgramFolder;
import javax.swing.*;
import java.awt.*;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.ArrayList;
import java.nio.file.*;
import java.io.IOException;
import com.group_finity.mascotapp.Constants;

public final class ImageSetChooserUtils {

    /**
     * Displays UI for choosing imageSets.
     *
     * @param onSelection consumer for an array of all selected image sets after
     *                    the selection has been completed.
     */
    public static void askUserForSelection(Consumer<Collection<String>> onSelection, Collection<String> currentSelection, ShimejiProgramFolder pf) {
        CompactChooser chooser = new CompactChooser(onSelection, currentSelection, pf) {
            @Override
            protected void addCustomButtons(JPanel buttonPanel) {
                JButton addNewButton = new JButton("Add New Shimeji");
                addNewButton.addActionListener(e -> {
                    AddShimejiDialog addDialog = new AddShimejiDialog(getDialog());
                    addDialog.setVisible(true);
                    // 刷新列表
                    refreshList();
                });
                buttonPanel.add(addNewButton, 0); // 添加到最左边
            }
        };
        chooser.createGui();
    }

    private static Collection<String> getAvailableImageSets() {
        try {
            Path imgDir = Constants.JAR_DIR.resolve("img");
            if (!Files.exists(imgDir)) {
                return new ArrayList<>();
            }
            
            Collection<String> sets = new ArrayList<>();
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(imgDir)) {
                for (Path entry : stream) {
                    if (Files.isDirectory(entry)) {
                        sets.add(entry.getFileName().toString());
                    }
                }
            }
            return sets;
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
