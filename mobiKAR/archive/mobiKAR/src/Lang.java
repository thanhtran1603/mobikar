/*
 * Locale.java
 *
 * Created on 9 paŸdziernik 2004, 17:50
 */

import java.io.*;
import java.util.*;
/**
 *
 * @author  MiKO
 * @version
 */
public class Lang {
    
    static byte m_countWords = 0;
    static public final byte OK = m_countWords++;
    static public final byte MENU = m_countWords++;
    static public final byte QUIT = m_countWords++;
    static public final byte PLAY = m_countWords++;
    static public final byte MY_SONGS = m_countWords++;
    static public final byte SETTINGS = m_countWords++;
    static public final byte HELP = m_countWords++;
    static public final byte ABOUT = m_countWords++;
    static public final byte INFO = m_countWords++;
    static public final byte DOWNLOAD = m_countWords++;
    static public final byte SERVER_SONGS = m_countWords++;
    static public final byte FONT = m_countWords++;
    static public final byte VOLUME = m_countWords++;
    static public final byte COLORS = m_countWords++;
    static public final byte ADVERTISMENT = m_countWords++;
    static public final byte LANGUAGE = m_countWords++;
    static public final byte FACE = m_countWords++;
    static public final byte STYLE = m_countWords++;
    static public final byte SIZE = m_countWords++;
    static public final byte FACE_MONOSPACE = m_countWords++;
    static public final byte FACE_PROPORTIONAL = m_countWords++;
    static public final byte FACE_SYSTEM = m_countWords++;
    static public final byte STYLE_BOLD = m_countWords++;
    static public final byte STYLE_ITALIC = m_countWords++;
    static public final byte STYLE_UNDERLINED = m_countWords++;
    static public final byte STYLE_OUTLINE = m_countWords++;
    static public final byte STYLE_SHADOW = m_countWords++;
    static public final byte SIZE_LARGE = m_countWords++;
    static public final byte SIZE_MEDIUM = m_countWords++;
    static public final byte SIZE_SMALL = m_countWords++;
    static public final byte COLOR_FOREGROUND = m_countWords++;
    static public final byte COLOR_BACKGROUND = m_countWords++;
    static public final byte COLOR_ACTIVE = m_countWords++;
    static public final byte STOP = m_countWords++;
    static public final byte RESUME = m_countWords++;
    static public final byte SING = m_countWords++;
    static public final byte TEXT_OLD = m_countWords++;
    static public final byte TEXT_ACTIVE = m_countWords++;
    static public final byte TEXT_NEW0 = m_countWords++;
    static public final byte TEXT_NEW1 = m_countWords++;
    static public final byte SETTINGS_MORE = m_countWords++;
    static public final byte PREVIEW_TIME = m_countWords++;
    static public final byte INTERLINE = m_countWords++;
    static public final byte LINEHISTORY = m_countWords++;
    static public final byte LINEBUFFOR = m_countWords++;
    static public final byte LINEBASE =m_countWords++;
    static public final byte BACKGROUNG =m_countWords++;
    static public final byte GRADIENT =m_countWords++;
    static public final byte WALLPAPPER =m_countWords++;
    static public final byte PANEL_SHOW =m_countWords++;
    static public final byte LANG_AUTO =m_countWords++;
    static public final byte GOTO_URL =m_countWords++;
    static public final byte DOWNLOAD_DESC =m_countWords++;
    static public final byte DELETE_DESC =m_countWords++;
    static public final byte ABOUT_DESC = m_countWords++;
    static public final byte CREATORS = m_countWords++;
    static public final byte VERSION = m_countWords++;
    static public final byte HELP_DESC = m_countWords++;
    static public final byte CONNECTING = m_countWords++;
    static public final byte RECEIVING = m_countWords++;
    static public final byte CONV_DESC = m_countWords++;
    static public final byte CONV_UNICODE = m_countWords++;
    static public final byte CONV_LATIN = m_countWords++;
    static public final byte RESTORE_DEFAULT = m_countWords++;
    static public final byte NET_PASSWORD = m_countWords++;
    static public final byte NET_PASSWORD_ENTER = m_countWords++;
    static public final byte NET_LOGIN = m_countWords++;
    static public final byte CMD_SHOW = m_countWords++;
    static public final byte SERVER_SONGS_ABOUT = m_countWords++;
    static public final byte MLYR_TITLE = m_countWords++;
    static public final byte MLYR_ARTIST = m_countWords++;
    static public final byte MLYR_LYRICS = m_countWords++;
    static public final byte MLYR_MUSIC = m_countWords++;
    static public final byte MLYR_CREATOR = m_countWords++;
    static public final byte MLYR_VERSION = m_countWords++;
    static public final byte MLYR_NOTE = m_countWords++;
    static public final byte MLYR_TIME = m_countWords++;
    static public final byte MLYR_TEXT = m_countWords++;
    static public final byte INTERNET_SONG = m_countWords++;
    static public final byte INTERNET_SONG_ABOUT = m_countWords++;
    static public final byte INTERNET_SONG_ENTER = m_countWords++;
    static public final byte PROBLEM = m_countWords++;
    
    
    public String[] m_dic = new String[m_countWords];
    public String m_lang = null;
    String[] m_langs = null;
    String m_convFrom = null;
    
    /**
     *
     * @param lang - numer jezyka 0 - auto, 1 - pierwszy jêzyk z listy
     */
    public Lang(int lang){
        if (MainMIDlet.DEBUG) System.err.println("Lang("+lang+")");
        m_langs = getLangs(); //  i tak tam jest ten zapis
        if (lang > m_langs.length){
            lang = 0;
        }
        if (lang == 0){
            String locale = System.getProperty("microedition.locale");
            if (locale != null && locale.length()>=2)
                m_lang = locale.substring(0, 2);
            else
                m_lang = m_langs[0]; // pierwszy jezyk z listy
        } else{
            m_lang = m_langs[lang-1];
        }
        DataInputStream dis = null;
        try{
            dis = new DataInputStream(getClass().getResourceAsStream("/loc/"+m_lang));
            // musi byæ test czytania, gdy¿ w przypadku czytania z nieistniej¹cego pliku dis nie wywala b³êdu
            dis.read();
            dis.close();
            // czytam ca³e dla pewnoœci dzia³ania, nie mam czsu sprwdziæ .reset()-a
            dis = new DataInputStream(getClass().getResourceAsStream("/loc/"+m_lang));
        } catch (Exception e){
            if (MainMIDlet.DEBUG) System.err.println("Nie znaleziono jezyka: "+e);
            m_lang = m_langs[0];
            dis = new DataInputStream(getClass().getResourceAsStream("/loc/"+m_lang));
        }
        try{
            for (int i=0; i<m_dic.length; i++){
                m_dic[i] = dis.readUTF();
            }
            //try{ Thread.sleep(3000);} catch (Exception e){}
            m_convFrom = m_dic[CONV_UNICODE];
            int bkgFlags = MainMIDlet.m_instance.m_store.getInteger(Store.INT_BACKGROUND);
            if ((bkgFlags & MainMIDlet.FLAG_LATIN) == MainMIDlet.FLAG_LATIN){
                convert();
            }
        } catch (Exception e){
            if (MainMIDlet.DEBUG) System.err.println("Lang: "+e);
        }
        if (MainMIDlet.DEBUG) System.err.println("m_lang: "+m_lang);
    }
    public String[] getLangs(){
        if (m_langs != null)
            return m_langs;
        // ustawiam na wypadek jakis bledów
        m_langs = new String[]{"en"};
        DataInputStream dis = new DataInputStream(getClass().getResourceAsStream("/loc.dir"));
        Vector vec = new Vector();
        try{
            int listSize = dis.readInt();
            m_langs = new String[listSize];
            for (int i=0; i<listSize; i++){
                m_langs[i] = dis.readUTF();
            }
        } catch (Exception e){
            if (MainMIDlet.DEBUG) e.printStackTrace();
        }
        return m_langs;
    }
    private void convert(){
        String convLatin = m_dic[CONV_LATIN];
        for (int i=0; i<m_dic.length; i++){
            for(int j=0; j<convLatin.length(); j++){
                m_dic[i] = m_dic[i].replace(m_convFrom.charAt(j), m_dic[CONV_LATIN].charAt(j));
            }
        }
    }
    public String convert(String text){
        String ret = text;
        int bkgFlags = MainMIDlet.m_instance.m_store.getInteger(Store.INT_BACKGROUND);
        if ((bkgFlags & MainMIDlet.FLAG_LATIN) == MainMIDlet.FLAG_LATIN){
            String convLatin = m_dic[CONV_LATIN];
            for(int j=0; j<convLatin.length(); j++){
                ret = ret.replace(m_convFrom.charAt(j), m_dic[CONV_LATIN].charAt(j));
            }
        }
        return ret;
    }
}
