/*
 * Store.java
 *
 * Created on 27 wrzesieñ 2004, 22:47
 */
import java.io.*;
import java.util.*;
import javax.microedition.rms.*;

/**
 *
 * @author  MiKO
 * @version
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
        2048,  // FONT
                0xF0C000,  // INT_COLOR_NEW0
                0xF07200,  // INT_COLOR_NEW1
                0xA093FF,  // INT_COLOR_ACTIVE
                0x808080,  // INT_COLOR_OLD
                0x705D00,  // INT_COLOR_OUTLINE
                0x000000,  // INT_COLOR_SHADOW
                0xf0eac2,  // INT_COLOR_BACKGROUND
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
    final String m_nameSettings = "settings";
    final String m_songDir = "/song/";
    boolean m_isModified = false;
    // nazwy plików w archiwum
    String[] m_songNames = null;
    // nazwy piosenek z archiwum plus ewentualna piosenka z RMS
    String[] m_songTitles = null;
    public Store(){
        read();
    }
    private void read(){
        if (MainMIDlet.DEBUG) System.err.println("Store.read()");
        RecordStore store=null;
        try {
            DataInputStream str=null;
            try{
                store=RecordStore.openRecordStore(m_nameSettings,true);
                byte[] data=store.getRecord(1);
                str=new DataInputStream(new ByteArrayInputStream(data));
            } catch(Exception ee){
                if (MainMIDlet.DEBUG) System.out.println("B³¹d w odczycie z RMS, zostan¹ u¿yte domyœlne z ./cfg.bin");
                if (MainMIDlet.DEBUG) ee.printStackTrace();
                str=new DataInputStream(getClass().getResourceAsStream("/cfg.bin"));
            }
            restore(str);
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
            Vector lista = new Vector();
            for (int i=0; true; i++){
                InputStream is = getClass().getResourceAsStream("/song/"+i+".mlyr");
                String lyricTitle = null;
                String lyricArtist = null;
                if (is == null)
                    break;
                DataInputStream dis = new DataInputStream(is);
                final int TAG_MLYR = ('m'<<24)|('L'<<16)|('Y'<<8)|('R');
                final int TAG_TITLE = ('T'<<24)|('I'<<16)|('T'<<8)|('L');
                final int TAG_ARTIST = ('A'<<24)|('R'<<16)|('T'<<8)|('I');
                int tag = dis.readInt();
                int size = dis.readInt();
                if (tag != TAG_MLYR){
                    System.out.println("To niejest format mLYR");
                    return;
                }
                do{
                    tag = dis.readInt();
                    size = dis.readInt();
                    if (tag == TAG_TITLE){
                        lyricTitle = dis.readUTF();
                    } else if (tag == TAG_ARTIST){
                        lyricArtist = dis.readUTF();
                    } else{
                        dis.skip(size);
                    }
                    if (lyricTitle != null && lyricArtist != null)
                        break;
                } while(true);
                dis.close();
                lista.addElement(lyricTitle+" - "+lyricArtist);
            }
            int size = lista.size();
            m_songNames = new String[size];
            m_songTitles = new String[size];
            for(int i=0; i<size; i++){
                m_songNames[i] = ""+i;
                m_songTitles[i] = (String)lista.elementAt(i);
            }
            if (MainMIDlet.DEBUG) System.err.println("Lista piosenek wczytana");
            // jeœli jest piosenka w RMSie to dodaj j¹ do listy
            checkSongRMS();
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
    public void restore(){
        restore(new DataInputStream(getClass().getResourceAsStream("/cfg.bin")));
    }
    private void restore(DataInputStream str){
        try{
            int[] integers = new int[m_integers.length];
            for (int i=0; i<integers.length; i++){
                integers[i]=str.readInt();
            }
            String[] strings = new String[m_strings.length];
            for (int i=0; i<strings.length; i++){
                strings[i]=str.readUTF();
            }
            str.close();
            // Dane zosta³y prawid³owo odczytane
            m_integers = integers;
            integers = null;
            m_strings = strings;
            strings = null;
            System.gc();
            m_isModified = false;
        } catch(Exception e) {
            if (MainMIDlet.DEBUG) System.err.println(e.toString());//printStackTrace();
        }
    }
    public Song getSongActual(){
        return getSong(m_integers[INT_SONG]);
    }
    public Song getSong(int idx){
        Song ret = null;
        if (MainMIDlet.DEBUG) System.err.println("getActualSong()");
        if (idx < m_songNames.length){
            ret =  new Song(MainMIDlet.m_instance.m_lang.convert(m_songTitles[idx]),
                    getClass().getResourceAsStream(m_songDir + m_songNames[idx]+".mlyr"),
                    m_songDir + m_songNames[idx]+".midi"
                    );
        } else{
            ret = getSongRms();
        }
        return ret;
    }
    public String[] getSongNames(){
        String[] ret = new String[m_songTitles.length];
        for (int i=0; i<ret.length; i++)
            ret[i] = MainMIDlet.m_instance.m_lang.convert(m_songTitles[i]);
        return ret;
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
    private void clearSongRMS(){
            try{
                RecordStore.deleteRecordStore("title");
            } catch (Exception e){
                if (MainMIDlet.DEBUG) e.printStackTrace();
            }
            try{
                RecordStore.deleteRecordStore("midi");
            } catch (Exception e){
                if (MainMIDlet.DEBUG) e.printStackTrace();
            }
            try{
                RecordStore.deleteRecordStore("mlyr");
            } catch (Exception e){
                if (MainMIDlet.DEBUG) e.printStackTrace();
            }
        
    }
    public void setSongRms(String title, byte[] dataMidi, byte[] dataMlyr) throws Exception{
        try{
            clearSongRMS();
            RecordStore store=null;
            byte[] data = null;
           
            store=RecordStore.openRecordStore("midi",true);
            store.addRecord(dataMidi,0,dataMidi.length);
            store.closeRecordStore();
            
            store=RecordStore.openRecordStore("mlyr",true);
            store.addRecord(dataMlyr,0,dataMlyr.length);
            store.closeRecordStore();
            if (MainMIDlet.HISTORY) MainMIDlet.m_instance.history.addElement("Store OK");
            
            store=RecordStore.openRecordStore("title",true);
            ByteArrayOutputStream bstr=new ByteArrayOutputStream();
            DataOutputStream str=new DataOutputStream(bstr);
            str.writeUTF(title);
            data = bstr.toByteArray();
            store.addRecord(data,0,data.length);
            store.closeRecordStore();

            checkSongRMS();
            if (MainMIDlet.HISTORY) MainMIDlet.m_instance.history.addElement("setSongRms() OK");
        } catch(Exception e){
            if (MainMIDlet.DEBUG) System.out.println("B³¹d w zapisie piosenki do RMS");
            if (MainMIDlet.DEBUG) e.printStackTrace();
            // wyczysc zle wpisane dane
            clearSongRMS();
            throw e;
        }
    }
    public Song getSongRms(){
        Song ret = null;
        if (MainMIDlet.DEBUG) System.err.println("Store.read()");
        try{
            RecordStore store=null;
            store=RecordStore.openRecordStore("title",false);
            byte[] data=store.getRecord(1);
            DataInputStream titleStream = new DataInputStream(new ByteArrayInputStream(data));
            String title = titleStream.readUTF();
            store.closeRecordStore();
            store=RecordStore.openRecordStore("midi",false);
            data=store.getRecord(1);
            InputStream midiStream = new ByteArrayInputStream(data);
            store.closeRecordStore();
            store=RecordStore.openRecordStore("mlyr",false);
            data=store.getRecord(1);
            InputStream mlyrStream = new ByteArrayInputStream(data);
            store.closeRecordStore();
            ret = new Song(title, mlyrStream, midiStream);
        } catch(Exception e){
            if (MainMIDlet.DEBUG) System.out.println("B³¹d w odczycie piosenki z RMS");
            if (MainMIDlet.DEBUG) e.printStackTrace();
        }
        return ret;
    }
    private void checkSongRMS(){
        String titleSongRms = null;
        try{
            RecordStore store=RecordStore.openRecordStore("title",false);
            byte[] data=store.getRecord(1);
            DataInputStream titleStream = new DataInputStream(new ByteArrayInputStream(data));
            titleSongRms = titleStream.readUTF();
            store.closeRecordStore();
        } catch (Exception e){
            // nothing
        }
        if (titleSongRms != null){
            if (m_songTitles.length == m_songNames.length){
                String[] newSongTitles = new String[m_songTitles.length+1];
                for (int i=0; i<m_songTitles.length; i++)
                    newSongTitles[i] = m_songTitles[i];
                m_songTitles = newSongTitles;
            }
            m_songTitles[m_songTitles.length-1] = titleSongRms;
        }
    }
}
