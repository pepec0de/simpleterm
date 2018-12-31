package com.youtube;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import javax.swing.*;

/**
 *
 * @author The Glider Life
 */
public class SimpleBashGui extends JFrame {
    
    private Process process;
    private PrintWriter stdin;
    private int currentOffset;
    private int currentCmd = 0;
    private JTextArea area;
    private JScrollPane scroll;
    
    public SimpleBashGui() {
        initUI();
        redirectSystemStreams();
        initBash();
    }
    
    private void initUI() {
        setTitle("Simple Bash");
        setSize(600, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        area = new JTextArea();
        scroll = new JScrollPane(area);
        
        area.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    execCmd(getLine());
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });
        
        add(scroll);
        setVisible(true);
    }
    
    private void initBash() {
        try {
            process = Runtime.getRuntime().exec("cmd");
            
            new Thread(new PipeOutput(process.getErrorStream(), System.err)).start();
            new Thread(new PipeOutput(process.getInputStream(), System.out)).start();
            
            stdin = new PrintWriter(process.getOutputStream());
            
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    public class PipeOutput implements Runnable {
        
        private final OutputStream oStream;
        private final InputStream iStream;
        
        public PipeOutput(InputStream is, OutputStream os) {
            this.oStream = os;
            this.iStream = is;
        }

        @Override
        public void run() {
            try {
                final byte[] buffer = new byte[1024];
                for(int l = 0; (l = iStream.read(buffer)) != -1;) {
                    oStream.write(buffer, 0, l);
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        
        
    }
    
    private void redirectSystemStreams() {
        OutputStream out = new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                updateText(String.valueOf((char) b));
            }
            
            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                updateText(new String(b, off, len));
            }
            
            @Override
            public void write(byte[] b) throws IOException {
                write(b, 0, b.length);
            }
            
        };
        
        System.setOut(new PrintStream(out, true));
        System.setErr(new PrintStream(out, true));
    }
    
    private void updateText(final String text) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                area.append(text);
            }
        });
    }
    
    private void execCmd(String cmd) {
        stdin.println(cmd);
        stdin.flush();
        switch(cmd) {
            
        }
    }
    
    private String getLine() {
        String line = null;
        int start, end;
        try {
            currentOffset = area.getLineOfOffset(area.getCaretPosition());
            start = area.getLineStartOffset(currentOffset);
            end = area.getLineEndOffset(currentOffset);
            
            line = area.getText(start, (end - start));
            return line.substring(line.indexOf(">") + 1);
        } catch(Exception e) {
            
        }
        return null;
    }
    
    public static void main(String[] args) {
        new SimpleBashGui();
    }
}
