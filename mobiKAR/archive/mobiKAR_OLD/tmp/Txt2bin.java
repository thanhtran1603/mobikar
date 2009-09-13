/*
 * Txt2bin.java
 *
 * Created on 20 grudzieñ 2004, 23:36
 */

package tmp;

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
        Map map = new TreeMap();
        String line = null;
        while ((line = br.readLine())!= null){
            System.out.println("line:"+line);
            try{
            String[] strs = line.split("\t");
            String str = " ";
            if (strs != null && strs.length >= 2)
                str = strs[1];
            map.put(new Integer(strs[0]), str);
            } catch (Exception e){
                System.out.println("Opuszczam linie "+e.toString());
            }
        }
        DataOutputStream dos = new DataOutputStream(new FileOutputStream("/var/song.bin"));
        dos.writeInt(map.size());
        for (Iterator it=map.keySet().iterator(); it.hasNext();){
            Integer pos = (Integer)it.next();
            String txt = (String)map.get(pos);
            dos.writeInt(pos.intValue());
            dos.writeUTF(txt);
        }
        dos.flush();
        dos.close();
    }
    
}
