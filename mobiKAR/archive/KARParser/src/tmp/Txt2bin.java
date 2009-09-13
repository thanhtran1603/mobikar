package tmp;
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
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("/var/song.txt"), "UTF-8"));
        DataOutputStream dos = new DataOutputStream(new FileOutputStream("/var/song.bin"));
        String line = null;
        while ((line = br.readLine())!= null){
            System.out.println("line:"+line);
            dos.writeUTF(line);
        }
        dos.flush();
        dos.close();
    }
    
}
