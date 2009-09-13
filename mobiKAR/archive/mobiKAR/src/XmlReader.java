/*
 * XmlReader.java
 *
 * Created on 14 listopad 2003, 21:06
 */

/**
 *
 * @author  MiKO
 */
import javax.microedition.io.*;
import java.io.*;

public class XmlReader extends Reader {
    InputStream m_input = null;
    StringBuffer m_buffer = new StringBuffer();
    
    /** Creates a new instance of XmlReader */
    public XmlReader(InputStream input) {
        m_input = input;
        readAhead = new int[3];
        prepareForNextChar();
    }
    public void close() throws IOException{
        if(m_input != null) {
            m_input.close();
        }
    }
    
    public int read(char ac[], int i, int j)
    throws IOException {
        int k = 0;
        boolean flag = false;
        if(j == 0)
            return 0;
        newRead = true;
        while(k < j) {
            int l = getByteOfCurrentChar(0);
            if(l < 0)
                if(l == -1 && k == 0)
                    return -1;
                else
                    return k;
            byte byte0=0;
            int i1=0;
            switch(l >> 4){
                case 0: // '\0'
                case 1: // '\001'
                case 2: // '\002'
                case 3: // '\003'
                case 4: // '\004'
                case 5: // '\005'
                case 6: // '\006'
                case 7: // '\007'
                    byte0 = 0;
                    i1 = l;
                    break;
                    
                case 12: // '\f'
                case 13: // '\r'
                    byte0 = 1;
                    i1 = l & 0x1f;
                    break;
                    
                case 14: // '\016'
                    byte0 = 2;
                    i1 = l & 0xf;
                    break;
                    
                case 8: // '\b'
                case 9: // '\t'
                case 10: // '\n'
                case 11: // '\013'
                default:
                    if (MainMIDlet.DEBUG) throw new UTFDataFormatException("invalid first byte " + Integer.toBinaryString(l));
            }
            for(int k1 = 1; k1 <= byte0; k1++) {
                int j1 = getByteOfCurrentChar(k1);
                if(j1 == -2)
                    return k;
                if(j1 == -1)
                    if (MainMIDlet.DEBUG) throw new UTFDataFormatException("partial character");
                if((j1 & 0xc0) != 128)
                    if (MainMIDlet.DEBUG) throw new UTFDataFormatException("invalid byte " + Integer.toBinaryString(j1));
                i1 = (i1 << 6) + (j1 & 0x3f);
            }
            
            ac[i + k] = (char)i1;
            k++;
            prepareForNextChar();
        }
        //        if (MainMIDlet.debug) System.err.print(new String(ac));
        return k;
    }
    
    private int getByteOfCurrentChar(int i)
    throws IOException {
        if(readAhead[i] != -2)
            return readAhead[i];
        if(!newRead && m_input.available() <= 0) {
            return -2;
        } else {
            if (m_buffer.length() > 0){
                int ret = (int)m_buffer.charAt(0);
                m_buffer.deleteCharAt(0);
                return ret;
            }
//            while (m_input.available() <= 0){
//                try{
//                    Thread.sleep(20);
//                } catch (Exception e){
//                    if (MainMIDlet.DEBUG) System.err.println(e.toString());
//                }
//            }
            int available = m_input.available();
            do{
                int ch = m_input.read();
                if (ch == -1)
                    break;
                m_buffer.append((char) ch);
                available--;
            } while (available > 0);
            if (m_buffer.length() == 0)
                return -1;
            readAhead[i] = (int)m_buffer.charAt(0);
            m_buffer.deleteCharAt(0);
            newRead = false;
            return readAhead[i];
        }
    }
    
    private void prepareForNextChar() {
        readAhead[0] = -2;
        readAhead[1] = -2;
        readAhead[2] = -2;
    }
    
    
    private static final int NO_BYTE = -2;
    private int readAhead[];
    private boolean newRead;
    
}
