package me;
/*
 * Txt2bin.java
 *
 * Created on 20 grudzieñ 2004, 23:36
 */


import java.io.*;
import java.util.*;

/**
 *
 * @author  MiKO
 */

public class Txt2bin {
    
    /** Creates a new instance of Txt2bin */
    public Txt2bin() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception{
        String filename = "/projects/java/ME/HighwaySurvival/tmp/text.txt";
        if (args.length > 0){
            filename = args[0];
        }
        File file = new File(filename);
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
        DataOutputStream dos = new DataOutputStream(new FileOutputStream(file.getAbsolutePath()+".bin"));
        String line = null;
        while ((line = br.readLine())!= null){
            System.out.println("line:"+line);
            if (line.startsWith("#"))
                continue;
            if (line.startsWith("String")){
                int count = 1;
                if (line.indexOf(",") != -1){
                    count = Integer.parseInt(line.trim().substring(line.indexOf(",")+1));
                }
                for (int i=0; i<count; i++){
                    line = br.readLine();
                    dos.writeUTF(line);
                }
            }
            else if (line.startsWith("Long")){
                int radix = 10;
                if (line.indexOf(",") != -1){
                    radix = Integer.parseInt(line.trim().substring(line.indexOf(",")+1));
                }
                line = br.readLine().trim();
                long val = Long.parseLong(line,radix);
                dos.writeLong(val);
            }
            else if (line.startsWith("Integer")){
                int radix = 10;
                if (line.indexOf(",") != -1){
                    radix = Integer.parseInt(line.trim().substring(line.indexOf(",")+1));
                }
                line = br.readLine().trim();
                int val = Integer.parseInt(line,radix);
                dos.writeInt(val);
            }
            else if (line.startsWith("Byte")){
                int radix = 10;
                if (line.indexOf(",") != -1){
                    radix = Integer.parseInt(line.trim().substring(line.indexOf(",")+1));
                }
                line = br.readLine().trim();
                byte val = Byte.parseByte(line,radix);
                dos.writeByte(val);
            }
            
        }
        dos.flush();
        dos.close();
    }
    
}
