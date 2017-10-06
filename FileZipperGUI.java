package com.cs.assignment2;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.ZoneId;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.swing.*;
import net.miginfocom.swing.MigLayout;


public class FileZipperGUI extends JFrame {
    //Important things needed for this GUI

    JTextArea textArea;
    JButton loadButton;
    JButton zipButton;
    JFileChooser fc;
    JPanel panel;
    Logger log = Logger.getLogger(FileZipperGUI.class.getName());
    String filepath ="";
    private SwingWorker<Integer, String> zipWorker;
    private SwingWorker<Integer, String> loadWorker;
    private int retnVal;
    private float sizeofOrgFile, zipSize, dOP;
    private BasicFileAttributes views1 =null;
    public static void main(String[] args) {

        new FileZipperGUI();
    }

    public FileZipperGUI() {
        textArea = new JTextArea(40, 40);
        loadButton = new JButton("Load");
        zipButton = new JButton("Zip File");
        fc = new JFileChooser();
        panel= new JPanel();
        log = Logger.getLogger(FileZipperGUI.class.getName());

        // naming and layout
        log.info("GUI OPENED");
        setTitle("Assignment 2");
        setLayout(new MigLayout());
        panel.setLayout(new MigLayout());
        textArea.setLineWrap(true);
        textArea.setEditable(false);

        //add swing components to the panel
        panel.add(textArea, "wrap");
        panel.add(loadButton, "wrap");
        panel.add(zipButton, "wrap");
        JScrollPane jp = new JScrollPane(textArea);
        jp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        jp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        add(jp);
        add(panel);
        log.setLevel(Level.INFO);

        try {
            loadButton.addActionListener(new ActionListener() {
                 @Override
                 public void actionPerformed(ActionEvent e) {
                     if(e.getSource() == loadButton) {
                         retnVal = fc.showOpenDialog(FileZipperGUI.this);
                         if(retnVal == JFileChooser.APPROVE_OPTION){
                             log.info("File Chooser opened");
                             long start = System.currentTimeMillis();// start Time
                             loadProgressFile(fc.getSelectedFile());
                             long endTime = System.currentTimeMillis();
                             log.info("File Displayed in text area");
                             JOptionPane.showMessageDialog(null, "File in text area\n"+ "Duration: " + ((endTime - start)) + "msec");
                         }

                     }
                 }
            });
            zipButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if(e.getSource() == zipButton) {
                        zipFileProgress(fc.getSelectedFile().getAbsolutePath());
                    }
                }
            });
        } catch (Exception ee) {
            ee.printStackTrace();
        }
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(700, 400));
        pack();
        setVisible(true);
    }

    void loadProgressFile(File file) {
        loadWorker = new  SwingWorker<Integer, String> (){

            @Override
            protected Integer doInBackground () throws InterruptedException {
                try {
                    log.info("File Selected");
                    filepath = file.getAbsolutePath();
                    BufferedReader readerFile = new BufferedReader(new FileReader(file));
                    String str;
                    StringBuilder sb = new StringBuilder();
                    while ((str = readerFile.readLine()) != null) {
                        sb.append(str + "\n\r");
                    }
                    System.out.println("publish");
                    publish(sb.toString());
                } catch (Exception io) {
                    io.printStackTrace();
                }
                //Thread.sleep(2000L);
                return 1;
            }

            @Override
            protected void process(List<String> chunks) {
                for(String line : chunks) {
                    //System.out.println("hi");
                    textArea.append(line);
                }
            }

            protected void done () {
                try {
                    zipButton.setEnabled(true);// path selected so
                } catch (Exception ie) {
                    ie.printStackTrace();
                }
            }
        };

        loadWorker.execute();
    }

    void zipFileProgress(String filepath){
        zipWorker = new SwingWorker<Integer, String>() {
            public Integer doInBackground ()throws InterruptedException {
                try {
                    byte[] buffer = new byte[1024];
                    int len;
                    File file = new File("/Users/sid/Desktop/big.zip");
                    FileOutputStream fos = new FileOutputStream(file);
                    ZipOutputStream zos = new ZipOutputStream(fos);
                    ZipEntry ze = new ZipEntry("/Users/sid/Desktop/big.txt");
                    zos.putNextEntry(ze);
                    FileInputStream fis = new FileInputStream(filepath);
                    float fileSize = (float) fis.getChannel().size() / 1000000;
                    sizeofOrgFile = fileSize;
                    while ((len = fis.read(buffer)) > 0) {
                        zos.write(buffer, 0, len);

                    }
                    float zipFileSize = (float) file.length() / 1000000;
                    zipSize = zipFileSize;
                    float percentage = ((fileSize - zipFileSize) / fileSize) * 100;
                    dOP = percentage;
                    fis.close();
                    zos.closeEntry();
                    zos.close();
                    log.info("File Zipped");
                    Path p = Paths.get("/Users/sid/Desktop/big.zip");
                    BasicFileAttributes view = Files.getFileAttributeView(p, BasicFileAttributeView.class).readAttributes();
                    views1 = view;
                } catch (Exception ee) {
                    ee.printStackTrace();
                }
                Thread.sleep(2000L);
                return 1;
            }
        public void done () {
            try {
                JOptionPane.showMessageDialog(null, "Size of the original size: " + sizeofOrgFile + "MB\n"
                        + "Size of the zip file: " + zipSize + "MB\n"
                        + "Percentage: " + dOP + "%\n"
                        + "Created: " + views1.creationTime().toInstant().atZone(ZoneId.systemDefault()));
                Thread.sleep(2000L);
            } catch (Exception ie) {
                ie.printStackTrace();
            }

        }
        };
        zipWorker.execute();
    }
}


