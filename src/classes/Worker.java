/*
 * ====================================================================
 *
 * Follett Software Company
 *
 * Copyright (c) 2018 Follett Software Company
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, is not permitted without a written agreement
 * from Follett Software Company.
 *
 * ====================================================================
 */
package classes;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;


public class Worker extends JPanel implements ActionListener {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static JFrame resultFrame;

    private static JTextArea prodSourceVersions;
    private static JTextArea locSourceVersions;
    private static JTextArea resultVersions;
    private static JFileChooser fc;
    private static File resultFile;
    private static String resultString;

    public static void main(String[] args) {
        appWindow();

        resultFrame.setVisible(true);
        resultFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    public static void compareVersions() {
        String prodVersionString =
                prodSourceVersions.getText().trim().replace("\t", "").replace("\n", "").replace("\r\n", "");
        String locVersionString =
                locSourceVersions.getText().trim().replace("\t", "").replace("\n", "").replace("\r\n", "");
        List<String> prodVersionsList = Arrays.asList(prodVersionString.split(","));
        List<String> locVersionsList = Arrays.asList(locVersionString.split(","));

        StringBuilder result = new StringBuilder();
        String delimiter = "";
        for (String version : prodVersionsList) {
            if (!locVersionsList.contains(version)) {
                result.append(delimiter);
                delimiter = ", ";
                result.append(version);
            }
        }
        resultString = result.toString();
        resultVersions.setText(resultString);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // do things
    }

    public synchronized static void appWindow() {
        resultFrame = new JFrame("DB Version Compare");
        File resultPath = new File(System.getProperty("user.dir"));
        fc = new JFileChooser(resultPath);

        FileFilter filter = new FileNameExtensionFilter("Text file (*.txt)", "txt");
        fc.addChoosableFileFilter(filter);
        fc.setFileFilter(filter);

        resultFrame.setBounds(0, 0, 1300, 650);

        prodSourceVersions = new JTextArea();
        prodSourceVersions.setText("");
        prodSourceVersions.setEditable(true);
        prodSourceVersions.setLineWrap(true);

        locSourceVersions = new JTextArea();
        locSourceVersions.setText("");
        locSourceVersions.setEditable(true);
        locSourceVersions.setLineWrap(true);

        resultVersions = new JTextArea();
        resultVersions.setText("");
        resultVersions.setEditable(false);
        resultVersions.setLineWrap(true);

        JButton saveFile = new JButton("Save Results");
        saveFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnVal = fc.showSaveDialog(null);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File f = fc.getSelectedFile();
                    String filepath = f.getAbsolutePath();
                    String filename = f.getName();

                    if (!filename.contains(".txt")) {
                        resultFile = new File(filepath + ".txt");
                    } else {
                        resultFile = f;
                    }

                    try {
                        Files.write(Paths.get(resultFile.getAbsolutePath()), resultString.getBytes());
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
        JButton gbutton = new JButton("Isolate Missing Versions");
        gbutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                compareVersions();
            }
        });

        JPanel generateControls = new JPanel();
        generateControls.setLayout(new FlowLayout());
        generateControls.add(gbutton);

        JPanel textFields = new JPanel();
        textFields.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        JScrollPane prodScrollPane = new JScrollPane(prodSourceVersions, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        prodScrollPane.setPreferredSize(new Dimension(400, 500));
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        textFields.add(prodScrollPane, c);

        JScrollPane locScrollPane = new JScrollPane(locSourceVersions, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        locScrollPane.setPreferredSize(new Dimension(400, 500));
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 0;
        textFields.add(locScrollPane, c);

        JScrollPane resultScrollPane = new JScrollPane(resultVersions, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        resultScrollPane.setPreferredSize(new Dimension(400, 500));
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 2;
        c.gridy = 0;
        textFields.add(resultScrollPane, c);

        JLabel prodLabel = new JLabel("Production Version List (paste here)   ");
        prodLabel.setHorizontalAlignment(SwingConstants.CENTER);
        c.gridx = 0;
        c.gridy = 1;
        textFields.add(prodLabel, c);

        JLabel locLabel = new JLabel("Local Version List (paste here)   ");
        locLabel.setHorizontalAlignment(SwingConstants.CENTER);
        c.gridx = 1;
        c.gridy = 1;
        textFields.add(locLabel, c);

        JLabel resultLabel = new JLabel("Results (missing versions shown here)   ");
        resultLabel.setHorizontalAlignment(SwingConstants.CENTER);
        c.gridx = 2;
        c.gridy = 1;
        textFields.add(resultLabel, c);

        JPanel saveControls = new JPanel();
        saveControls.setLayout(new FlowLayout());
        saveControls.add(saveFile);

        resultFrame.getContentPane().add(generateControls, BorderLayout.NORTH);
        resultFrame.getContentPane().add(textFields, BorderLayout.CENTER);
        resultFrame.getContentPane().add(saveControls, BorderLayout.SOUTH);
    }

}
