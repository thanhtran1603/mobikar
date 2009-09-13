package tmp;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
/*
 * NewMain.java
 *
 * Created on 27 maj 2005, 18:18
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

/**
 *
 * @author a
 */
public class Store {
    static byte m_countIntegers = 0;
    static public final int INT_FONT = m_countIntegers++;
    static public final int INT_COLOR_NEW0 = m_countIntegers++;
    static public final int INT_COLOR_NEW1 = m_countIntegers++;
    static public final int INT_COLOR_ACTIVE = m_countIntegers++;
    static public final int INT_COLOR_OLD = m_countIntegers++;
    static public final int INT_COLOR_OUTLINE = m_countIntegers++;
    static public final int INT_COLOR_SHADOW = m_countIntegers++;
    static public final int INT_COLOR_BACKGROUND = m_countIntegers++;
    static public final int INT_COLOR_GRADIENT0 = m_countIntegers++;
    static public final int INT_COLOR_GRADIENT9 = m_countIntegers++;
    static public final int INT_PREVIEW = m_countIntegers++;
    static public final int INT_INTERLINE = m_countIntegers++;
    static public final int INT_LINEHISTORY = m_countIntegers++;
    static public final int INT_LINEBUFFOR = m_countIntegers++;
    static public final int INT_LINEBASE = m_countIntegers++;
    static public final int INT_BACKGROUND = m_countIntegers++;
    static public final int INT_VOLUME = m_countIntegers++;
    static public final int INT_LANGS =  m_countIntegers++;
    static public final int INT_SONG = m_countIntegers++;
    static byte m_countStrings = 0;
    static public final int STR_LANG = m_countStrings++;
    static public final int STR_PASSWORD = m_countStrings++;
    static public final int STR_INTERNET_SONG = m_countStrings++;
    public int[] m_integers = {
        0,//2048,  // FONT
                0xF0C000,  // INT_COLOR_NEW0
                0xF07200,  // INT_COLOR_NEW1
                0xA093FF,  // INT_COLOR_ACTIVE
                0x808080,  // INT_COLOR_OLD
                0x705D00,  // INT_COLOR_OUTLINE
                0x000000,  // INT_COLOR_SHADOW
                0x004050,  // INT_COLOR_BACKGROUND
                0xf0eac2,  // INT_COLOR_GRADIENT0
                0xf0eac2,  // INT_COLOR_GRADIENT9
                100,  // INT_PREVIEW
                2,  // INT_INTERLINE
                1,  // INT_LINEHISTORY
                5,  // INT_LINEBUFFOR
                -3,  // INT_LINEBASE
                2 | 4 | 8 , // INT_BACKGROUND: 0 - czysty kolor <- FLAG_LATIN tu posadzone
                80, // INT_VOLUME
                0, // INT_LANGS; 0 - automatic language
                0, // INT_SONG; 0 - pierwsza piosenka wbudowana
    };
    /* FALSOLKI
        0xF0C000,  // INT_COLOR_NEW0
        0xF07000,  // INT_COLOR_NEW1
        0xF00000,  // INT_COLOR_ACTIVE
        0x006080,  // INT_COLOR_OLD
        0x008090,  // INT_COLOR_OUTLINE
        0x000000,  // INT_COLOR_SHADOW
        0x003e50,  // INT_COLOR_BACKGROUND
        0x608090,  // INT_COLOR_GRADIENT0
        0x3080C0,  // INT_COLOR_GRADIENT9
     
     */
    public String[] m_strings = {
        "pl",
        "",
        "http://www.mobikar.net/dzieweczka.xml",
    };
    
    /** Creates a new instance of NewMain */
    public Store() {
    }
    public void write(String fileName){
        try {
            DataOutputStream str=new DataOutputStream(new FileOutputStream(fileName));
            for (int i=0; i<m_integers.length; i++){
                str.writeInt(m_integers[i]);
            }
            for (int i=0; i<m_strings.length; i++){
                str.writeUTF(m_strings[i]);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        new Store().write("D:\\projects\\java\\ME\\mobiKAR\\mobiKAR\\res\\global\\cfg.bin");
    }
    
}
