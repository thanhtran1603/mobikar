/*
 * ConvertText.java
 *
 * Created on 19 listopad 2005, 18:40
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/**
 *
 * @author a
 */
public class ConvertText {
    
    /** Creates a new instance of ConvertText */
    public ConvertText() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception{
        // TODO code application logic here
        String input="Z poni??szej listy wybierz jeden z tematów FAQ, a nastÄ™pnie który?› do przeczytania. Je?›li masz jakie?› pytania, których nie ma w tej sekcji, prosimy skontaktowaÄ‡ siÄ™ z nami.";
        byte[] buf = input.getBytes("ISO-8859-1");
        String output = new String(buf,"UTF-8");
        System.out.println(">"+output);
    }
    
}
