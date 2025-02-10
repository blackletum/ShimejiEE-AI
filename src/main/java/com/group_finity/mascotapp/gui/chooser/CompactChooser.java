package com.group_finity.mascotapp.gui.chooser;

import com.group_finity.mascot.Tr;
import com.group_finity.mascot.imageset.ShimejiProgramFolder;
import com.group_finity.mascotapp.gui.Theme;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class CompactChooser {

    private JDialog dialog;
    private final Consumer<Collection<String>> onSelection;
    private final Collection<String> currentSelection;
    private final ShimejiProgramFolder pf;
    private CompactImageSetList list;

    public CompactChooser(Consumer<Collection<String>> onSelection, Collection<String> currentSelection, ShimejiProgramFolder pf) {
        this.onSelection = onSelection;
        this.currentSelection = currentSelection;
        this.pf = pf;
    }

    protected void addCustomButtons(JPanel buttonPanel) {
        // 现在这个方法不再需要，因为我们已经在上面添加了按钮
    }

    protected JDialog getDialog() {
        return dialog;
    }

    protected void refreshList() {
        if (list != null) {
            list.refreshContent();
        }
    }

    /**
     * GUI entry point
     */
    public void createGui() {
        dialog = new JDialog((Frame) null, "Shimeji Image Set Chooser", true);
        SwingUtilities.invokeLater(() -> {
            //Set up data and selections
            try {
                CompactChooser.this.addDataToUI();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(dialog, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }

            //Set up the content pane.
            CompactChooser.this.addContentToPane(dialog.getContentPane());

            dialog.setResizable(true);

            dialog.pack();
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);
            dialog.toFront();
        });
    }

    private ArrayList<String> getSelections() {
        return list.getSelectedValuesList().stream()
                .map(Objects::toString)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private void addDataToUI() throws IOException {
        DefaultListModel<CompactImageSetPreview> listModel = new DefaultListModel<>();

        String[] allImageSets;
        try {
            allImageSets = pf.getImageSetNames().toArray(new String[0]);
        } catch (IOException e) {
            throw new IOException("Unable to load imageSets", e);
        }

        if (allImageSets.length == 0) {
            list = new CompactImageSetList(listModel);
            return;
        }

        Set<String> selected = new HashSet<>(currentSelection);

        Collection<CompactImageSetPreview> data = new ArrayList<>(allImageSets.length);
        for (String imgSet : allImageSets) {
            data.add(new CompactImageSetPreview(imgSet, pf.getIconPathForImageSet(imgSet)));
        }
        listModel.addAll(data);

        list = new CompactImageSetList(listModel);

        var selectedIndices = new ArrayList<Integer>();

        for (int j = 0; j < allImageSets.length; j++) {
            if (selected.contains(allImageSets[j])) {
                selectedIndices.add(j);
            }
        }

        // https://stackoverflow.com/questions/960431/
        list.setSelectedIndices(selectedIndices.stream().mapToInt(i -> i).toArray());

    }

    private void addContentToPane(Container pane) {
        var gbl = new GridBagLayout();
        pane.setLayout(gbl);
        GridBagConstraints constraints = new GridBagConstraints();

        //-------scroll view-------//
        var scPane = new JScrollPane(list);

        constraints.fill = GridBagConstraints.BOTH;
        constraints.weighty = 1;
        constraints.weightx = 1;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets = new Insets(9, 9, 9, 0);  // 右边距设为0

        scPane.setBorder(BorderFactory.createLineBorder(Theme.PANEL_BORDER));
        scPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scPane.setPreferredSize(new Dimension(400, 400));

        pane.add(scPane, constraints);

        //--------buttons--------//
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints buttonConstraints = new GridBagConstraints();
        buttonConstraints.gridx = 0;
        buttonConstraints.fill = GridBagConstraints.HORIZONTAL;
        buttonConstraints.insets = new Insets(0, 0, 5, 0);  // 按钮之间的间距
        buttonConstraints.weightx = 1.0;

        // 添加新Shimeji按钮
        JButton addNewButton = new JButton("Add New Shimeji");
        addNewButton.addActionListener(e -> {
            AddShimejiDialog addDialog = new AddShimejiDialog(dialog);
            addDialog.setVisible(true);
            refreshList();
        });
        buttonConstraints.gridy = 0;
        buttonPanel.add(addNewButton, buttonConstraints);

        // 使用选中按钮
        JButton buttonOK = new JButton(Tr.tr("UseSelected"));
        buttonOK.addActionListener(e -> {
            dialog.dispose();
            onSelection.accept(getSelections());
        });
        buttonConstraints.gridy = 1;
        buttonPanel.add(buttonOK, buttonConstraints);

        // 取消按钮
        JButton buttonCancel = new JButton(Tr.tr("Cancel"));
        buttonCancel.addActionListener(e -> dialog.dispose());
        buttonConstraints.gridy = 2;
        buttonPanel.add(buttonCancel, buttonConstraints);

        // 添加自定义按钮的占位符
        buttonConstraints.gridy = 3;
        buttonConstraints.weighty = 1.0;  // 让剩余空间推到底部
        buttonPanel.add(new JPanel(), buttonConstraints);

        // 将按钮面板添加到主面板
        constraints = new GridBagConstraints();
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.fill = GridBagConstraints.VERTICAL;
        constraints.insets = new Insets(9, 9, 9, 9);
        pane.add(buttonPanel, constraints);

        dialog.getRootPane().setDefaultButton(buttonOK);
    }
}
