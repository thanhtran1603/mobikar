package tmp;
/*
 * WriteLocaleList.java
 *
 * Created on 9 paŸdziernik 2004, 18:18
 */
import java.io.*;
/**
 *
 * @author  MiKO
 */
public class WriteUTFList {
    
    /** Creates a new instance of WriteLocaleList */
    public WriteUTFList() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception{
        // TODO code application logic here
        DataOutputStream dos = new DataOutputStream(
                new FileOutputStream("/projects/java/ME/mobiKAR/mobiKAR/res/songs/01/song.dir"));
        dos.writeInt(1);
        dos.writeUTF("01");
        dos.writeUTF("Barka - pieœñ religijna");
        dos.close();
        System.out.println("done!");
    }
}
