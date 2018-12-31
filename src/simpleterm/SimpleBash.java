package com.youtube;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 *
 * @author pepe
 */
public class SimpleBash {
    
    private Scanner sc;
    private Process process;
    private PrintWriter stdin;
    
    public SimpleBash() {
        try {
            process = Runtime.getRuntime().exec("cmd");
            
            new Thread(new PipeOutput(process.getInputStream(), System.out)).start();
            new Thread(new PipeOutput(process.getErrorStream(), System.err)).start();
            
            stdin = new PrintWriter(process.getOutputStream());
            sc = new Scanner(System.in);
            while(true) {
                String cmd = sc.next();
                cmd += sc.nextLine();
                stdin.println(cmd);
                stdin.flush();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    public class PipeOutput implements Runnable {
        
        private final InputStream iStream;
        private final OutputStream oStream;
        
        public PipeOutput(InputStream is, OutputStream os) {
            this.iStream = is;
            this.oStream = os;
        }
        
        @Override
        public void run() {
            try {
                final byte[] buffer = new byte[1024];
                for(int l = 0; (l = iStream.read(buffer)) != -1;) {
                    oStream.write(buffer, 0 , l);
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        
    }
    
    public static void main(String[] args) {
        new SimpleBash();
    }
}
