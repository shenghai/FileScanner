package com.gshai.filescannerui;

import com.gshai.filescanner.*;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

/**
 * Created by Shenghai on 2015/8/18.
 */
public class FileScannerUI extends JFrame {
    private Scanner runningScanner = null;

    @Override
    protected void frameInit() {
        super.frameInit();
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        JLabel label = new JLabel("Search Path: ");

        label.setHorizontalAlignment(SwingConstants.RIGHT);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        add(label, c);

        final JTextField basePathField = new JTextField(30);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 0;
        add(basePathField, c);

        JLabel label2 = new JLabel("Keyword: ");
        label2.setHorizontalAlignment(SwingConstants.RIGHT);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 1;
        add(label2, c);

        final JTextField keywordField = new JTextField(30);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 1;
        add(keywordField, c);

        JLabel label3 = new JLabel("Filter: ");
        label3.setHorizontalAlignment(SwingConstants.RIGHT);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 2;
        add(label3, c);

        final JTextField filterField = new JTextField(30);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 2;
        add(filterField, c);

        JLabel label4 = new JLabel("example: f:exclude:*.jar|*.zip|.*|abc*;f:include:*.txt|*.java;d:exclude:.*;");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 3;
        add(label4, c);


        JPanel buttons = new JPanel();
        JButton searchButton = new JButton("Search");
        buttons.add(searchButton);
        JButton cancelButton = new JButton("Cancel");
        buttons.add(cancelButton);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 4;
        add(buttons, c);


        final JTextArea textArea = new JTextArea();
//        ScrollPane scrollPane = new ScrollPane();
//        scrollPane.add(textArea);
        textArea.setSize(800, 500);
        textArea.setEditable(false);

        JScrollPane scroll = new JScrollPane (textArea);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
//        textArea.setContentType("text/html");
//        textArea.setLineWrap(true);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 5;
        c.gridwidth = 2;
        c.ipady = 500;
        c.ipadx = 800;
        add(scroll, c);

        final String lineBreak = System.getProperty("line.separator");
        final Printer printer = new Printer() {
            public void println(String msg) {
                textArea.append(msg);
                textArea.append(lineBreak);
            }
        };

        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                textArea.setText("");
                String keyword = keywordField.getText();
                if (keyword == null || keyword.trim().equals("")) {
                    textArea.setText("need a keyword to search");
                    return;
                }
                new Thread(){
                    @Override
                    public void run() {
//                        FileScannerClient.findFilesByContext(basePathField.getText(), keywordField.getText(), printer, filterField.getText());
                        Scanner scanner = new Scanner() {
                            @Override
                            public void testMatch(ScanEntry entry) throws Exception {
                                String keyword = keywordField.getText();
                                if (checkMultiLineEqual(entry, keyword.split("\\s+"))) {
                                    getPrinter().println(entry.getPath());
                                }
                            }
                        };
                        runningScanner = scanner;
                        scanner.addNameFilter(filterField.getText());
                        scanner.setPrinter(printer);
                        long start = System.currentTimeMillis();
                        scanner.scan(basePathField.getText());
                        long end = System.currentTimeMillis();
                        printer.println("Scanner takes " + (end - start) + " ms to search " + scanner.getTotalCount() + " files");

                    }
                }.start();

            }
        });
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (runningScanner != null) {
                    runningScanner.stopScan();
                }
            }
        });
    }

    private JPanel top() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        panel.add(new JLabel("Search Path:"));
        panel.add(new JTextField(50));
        return panel;
    }

    private static void addAButton(String text, Container container) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        container.add(button);
    }

    private void addSearchCondition(JPanel container) {
        addAButton("Button 1", container);
        addAButton("Button 2", container);
        addAButton("Button 3", container);
        addAButton("Long-Named Button 4", container);
        addAButton("5", container);
    }

    private static void createAndShowGUI() {
        // Create and set up the window.
        FileScannerUI frame = new FileScannerUI();
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}
