package com.group_finity.mascotapp.gui.chooser;

import com.group_finity.mascot.imageset.ShimejiProgramFolder;
import com.group_finity.mascotapp.Constants;
import com.group_finity.mascotapp.gui.Theme;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import java.awt.Component;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class CompactImageSetList extends JList<CompactImageSetPreview> {

    public CompactImageSetList(DefaultListModel<CompactImageSetPreview> model) {
        super(model);

        this.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        this.setLayoutOrientation(JList.VERTICAL);
        this.setCellRenderer(new ImageSetRenderer());
    }

    public void refreshContent() {
        DefaultListModel<CompactImageSetPreview> model = (DefaultListModel<CompactImageSetPreview>) getModel();
        model.clear();
        
        try {
            Path imgDir = Constants.JAR_DIR.resolve("img");
            if (Files.exists(imgDir)) {
                try (DirectoryStream<Path> stream = Files.newDirectoryStream(imgDir)) {
                    for (Path entry : stream) {
                        if (Files.isDirectory(entry)) {
                            String imgSet = entry.getFileName().toString();
                            model.addElement(new CompactImageSetPreview(imgSet, 
                                ShimejiProgramFolder.fromFolder(Constants.JAR_DIR).getIconPathForImageSet(imgSet)));
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        revalidate();
        repaint();
    }

    private static class ImageSetRenderer implements ListCellRenderer<CompactImageSetPreview> {
        @Override
        public Component getListCellRendererComponent(JList<? extends CompactImageSetPreview> list, CompactImageSetPreview value,
                                                      int index, boolean isSelected, boolean cellHasFocus) {
            var component = value.getPanel();

            if (isSelected) {
                component.setBackground(Theme.SELECTION_HIGHLIGHT);
                component.setBorder(BorderFactory.createLineBorder(Theme.SELECTION_BORDER));
            } else if (index % 2 == 0) {
                component.setBackground(Theme.LIST_COLOR_DARK);
                component.setBorder(null);
            } else {
                component.setBackground(Theme.LIST_COLOR_LIGHT);
                component.setBorder(null);
            }

            return component;
        }
    }

}
