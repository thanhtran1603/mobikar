/*
 * Store.java
 *
 * Created on 27 wrzesieñ 2004, 22:47
 */

import javax.microedition.io.*;
import java.io.*;
import javax.microedition.rms.*;

/**
 *
 * @author  MiKO
 * @version
 */

public class Store {
    static byte m_countIntegers = 0;
    static public final int INT_FONT = m_countIntegers++;
    static public final int INT_COLOR_FOREGROUND = m_countIntegers++;
    static public final int INT_COLOR_BACKGROUND = m_countIntegers++;
    static public final int INT_COLOR_ACTIVE = m_countIntegers++;
    static public final int INT_COLOR_READ = m_countIntegers++;
    static public final int INT_COLOR_OUTLINE = m_countIntegers++;
    static public final int INT_COLOR_SHADOW = m_countIntegers++;
    static public final int INT_PREVIEW = m_countIntegers++;
    static public final int INT_INTERLINE = m_countIntegers++;
    static public final int INT_LINEHISTORY = m_countIntegers++;
    static public final int INT_LINEBUFFOR = m_countIntegers++;
    static public final int INT_LINEBASE =m_countIntegers++;
    static public final int INT_BACKGROUND =m_countIntegers++;
    static public final int INT_VOLUME =m_countIntegers++;
    static public final int INT_LANGS =m_countIntegers++;
    static public final int INT_SONG =m_countIntegers++;
    static byte m_countStrings = 0;
    static public final int STR_LANG = m_countStrings++;
    public int[] m_integers = {
        2048,  // FONT
        0xFFFFFF,
        0x0E4F0F,
        0xFFFF60,
        0x608040,
        0x202000,
        0x202000,
        100,
        2,
        1,
        5,
        -3,
        7, // t³o: 0 - czysty kolor
        80, // Volume
        0, // 0 - automatic language
        0, // 0 - pierwsza piosenka wbudowana
    };
    public String[] m_strings = {
        "pl",
    };
    final String m_nameSettings = "settings";
    boolean m_isModified = false;
    String[] m_songNames = null;
    String[] m_songTitles = null;
    int[] m_songDurations = null;
    public Store(){
        read();
    }
    private void read(){
        if (MainMIDlet.DEBUG) System.err.println("Store.read()");
        RecordStore store=null;
        try {
            store=RecordStore.openRecordStore(m_nameSettings,true);
            byte[] data=store.getRecord(1);
            DataInputStream str=new DataInputStream(new ByteArrayInputStream(data));
            for (int i=0; i<m_integers.length; i++){
                m_integers[i]=str.readInt();
            }
            for (int i=0; i<m_strings.length; i++){
                m_strings[i]=str.readUTF();
            }
            str.close();
            System.gc();
            m_isModified = false;
        } catch(Exception e) {
            if (MainMIDlet.DEBUG) System.out.println("B³¹d w odczycie danych, zostan¹ u¿yte domyœlne");
            if (MainMIDlet.DEBUG) e.printStackTrace();
        }
        
        if(store!=null) {
            try {
                store.closeRecordStore();
            } catch(Exception e) {
                if (MainMIDlet.DEBUG) e.printStackTrace();
            }
        }
        
        try{
            DataInputStream dis = new DataInputStream(getClass().getResourceAsStream("/song.dir"));
            int size = dis.readInt();
            m_songNames = new String[size];
            m_songTitles = new String[size];
            m_songDurations = new int[size];
            for(int i=0; i<size; i++){
                m_songNames[i] = dis.readUTF();
                m_songTitles[i] = dis.readUTF();
                m_songDurations[i] = dis.readInt();
            }
            if (MainMIDlet.DEBUG) System.err.println("Lista piosenek wczytana");
        } catch (Exception e){
            if (MainMIDlet.DEBUG) e.printStackTrace();
        }
        if (MainMIDlet.DEBUG) System.err.println("Store.read()... done");
    }
    public void write(){
        RecordStore store=null;
        try {
            RecordStore.deleteRecordStore(m_nameSettings);
            store=RecordStore.openRecordStore(m_nameSettings,true);
            ByteArrayOutputStream bstr=new ByteArrayOutputStream();
            DataOutputStream str=new DataOutputStream(bstr);
            for (int i=0; i<m_integers.length; i++){
                if (MainMIDlet.DEBUG) System.err.println("Store.write(): m_integers["+i+"]:"+m_integers[i]);
                str.writeInt(m_integers[i]);
            }
            for (int i=0; i<m_strings.length; i++){
                if (MainMIDlet.DEBUG) System.err.println("Store.write(): m_strings["+i+"]:"+m_strings[i]);
                str.writeUTF(m_strings[i]);
            }
            byte data[]=bstr.toByteArray();
            store.addRecord(data,0,data.length);
            store.closeRecordStore();
            m_isModified = false;
        } catch(Exception e) {
            if (MainMIDlet.DEBUG) System.err.println(e.toString());//printStackTrace();
        }
    }
    
    public void close(){
        if (m_isModified){
            write();
            m_isModified = false;
        }
    }
    
    public Song getActualSong(){
        if (MainMIDlet.DEBUG) System.err.println("getActualSong()");
        int idx = m_integers[INT_SONG];
        return new Song(m_songTitles[idx], 
            getClass().getResourceAsStream("/song/"+m_songNames[idx]+".m"),
            getClass().getResourceAsStream("/song/"+m_songNames[idx]+".t"),
            m_songDurations[idx]
        );
    }
    public String[] getSongNames(){
        return m_songTitles;
    }
    public String[] getAdvNames(){
        return new String[]{"KomKom"};
    }
    public int getInteger(int index){
        return m_integers[index];
    }
    public void setInteger(int index, int value){
        m_integers[index] = value;
        m_isModified = true;
    }
    public String getString(int index){
        return m_strings[index];
    }
    public void setString(int index, String value){
        m_strings[index] = value;
        m_isModified = true;
    }
}
