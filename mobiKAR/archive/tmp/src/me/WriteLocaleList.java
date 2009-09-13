package me;
/*
 * WriteLocaleList.java
 *
 * Created on 9 paüdziernik 2004, 18:18
 */
import java.io.*;
/**
 *
 * @author  MiKO
 */
public class WriteLocaleList {
    
    /** Creates a new instance of WriteLocaleList */
    public WriteLocaleList() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception{
        // TODO code application logic here
        DataOutputStream dos = new DataOutputStream(new FileOutputStream("/projects/java/ME/GameRealTimeTemplate/res/loc"));
        dos.writeInt(2); // Ile ich jest na liúcie
        dos.writeUTF("pl");
        dos.writeUTF("en");
        dos.close();
        dos = new DataOutputStream(new FileOutputStream("/projects/java/ME/GameRealTimeTemplate/res/pl"));
        dos.writeUTF("•∆ £—”åèØπÊÍ≥ÒÛúüø()<>[]");
        dos.writeUTF("ACELNOSZZacelnoszz      ");
        dos.writeUTF("W≥πczyÊ düwiÍki?");
        dos.writeUTF("Start");
        dos.writeUTF("Opcje");
        dos.writeUTF("Najlepsi");
        dos.writeUTF("Pomoc");
        dos.writeUTF("O autorach");
        dos.writeUTF("Koniec");

        dos.close();

        dos = new DataOutputStream(new FileOutputStream("/projects/java/ME/GameRealTimeTemplate/res/en"));
        dos.writeUTF("");
        dos.writeUTF("");
        dos.writeUTF("Enable Sound?");
        dos.writeUTF("Start");
        dos.writeUTF("Options");
        dos.writeUTF("High scores");
        dos.writeUTF("Help");
        dos.writeUTF("About");
        dos.writeUTF("Quit");

        dos.close();
    }
    
}
