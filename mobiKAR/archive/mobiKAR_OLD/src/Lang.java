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
    static public final byte RENAME = m_countWords++;
    static public final byte DELETE = m_countWords++;
    static public final byte DOWNLOAD = m_countWords++;
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
    static public final byte TEXT_ACTIVE = m_countWords++;
    static public final byte TEXT_BEFORE = m_countWords++;
    static public final byte TEXT_AFTER = m_countWords++;
    static public final byte SETTINGS_MORE = m_countWords++;
    static public final byte PREVIEW_TIME = m_countWords++;
    static public final byte INTERLINE = m_countWords++;
    static public final byte LINEHISTORY = m_countWords++;
    static public final byte LINEBUFFOR = m_countWords++;
    static public final byte LINEBASE =m_countWords++;
    static public final byte BACKGROUNG =m_countWords++;
    static public final byte PLAINCOLOR =m_countWords++;
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

    public String[] m_dic = new String[m_countWords];
    public String m_lang = null;
    String[] m_langs = null;
    
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
            if (locale != null)
                m_lang = locale.substring(0, 2);
        }
        else{
            m_lang = m_langs[lang-1];
        }
        DataInputStream dis = null;
        try{
            dis = new DataInputStream(getClass().getResourceAsStream("/loc/"+m_lang));
        } catch (Exception e){
            if (MainMIDlet.DEBUG) System.err.println("Nie znaleziono jezyka: "+e);
            dis = new DataInputStream(getClass().getResourceAsStream("/loc/en"));
        }
        try{
            for (int i=0; i<m_dic.length; i++){
                m_dic[i] = dis.readUTF();
            }
        }
        catch (Exception e){
            if (MainMIDlet.DEBUG) System.err.println("Lang: "+e);
        }
        if (MainMIDlet.DEBUG) System.err.println("m_lang: "+m_lang);
    }
    public String[] getLangs(){
        if (m_langs != null)
            return m_langs;
        DataInputStream dis = new DataInputStream(getClass().getResourceAsStream("/loc.dir"));
        Vector vec = new Vector();
        while (true) {
            try{
                vec.addElement(dis.readUTF());
            }
            catch (EOFException eofe){
                m_langs = new String[vec.size()];
                for (int i=0; i<vec.size(); i++)
                    m_langs[i] = (String)vec.elementAt(i);
                return m_langs;
            }
            catch (Exception e){
                if (MainMIDlet.DEBUG) e.printStackTrace();
                return m_langs;
            }
        }
    }
    
}
