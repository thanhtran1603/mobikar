/*
 * MainMIDlet.java
 *
 * Created on 26 wrzesieñ 2004, 22:37
 */
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

/**
 *
 * @author  MiKO
 * @version
 */
public class MainMIDlet extends MIDlet implements CommandListener{
    public static final boolean DEBUG = false;
    public static final boolean HISTORY = false;
//#if SND_MOT    
//#     public final boolean DOWNLOADABLE = false;
//#else    
    public final boolean DOWNLOADABLE = (getAppProperty("mobiKAR-login") != null);
//#endif    
    public final boolean GUARD = (getAppProperty("mobiKAR-login") != null);
    public static MainMIDlet m_instance;
    Display m_display;
    Displayable m_screen;
    public Intro m_intro = null;
    // usawiane w intro
    public static int m_width = -1;
    public static int m_height = -1;
    
    public Store m_store = null;
    public Lang m_lang = null;
    Command m_commands[] = null;
    
    Engine m_engine = null;
    Form m_form = null;
    ColorChooser m_colorChooser = null;
    
    Vector m_songsServerList = null;
    
    //debug
    public static java.util.Vector history = new java.util.Vector();
    
    public final static int FLAG_FONT_OUTLINE = 1024;
    public final static int FLAG_FONT_SHADOW = 2048;
    public final static int FLAG_GRADIENT = 1;
    public final static int FLAG_BACKGROUND = 2;
    public final static int FLAG_PANEL = 4;
    public final static int FLAG_ADVERTISMENT = 8;
    public final static int FLAG_LATIN = 16;
    
    public final String m_jadLogin = getAppProperty("mobiKAR-login");
    public final String m_jadPassword = getAppProperty("mobiKAR-password");
    public final String m_jadSize = getAppProperty("MIDlet-Jar-Size");
    
    public void startApp() {
        m_instance = this;
        m_display = Display.getDisplay(this);
        if (MainMIDlet.DEBUG) System.err.println("A");
        m_store = new Store();
        int l = m_store.getInteger(Store.INT_LANGS);
        m_lang = new Lang(m_store.getInteger(Store.INT_LANGS));
        createCommands();
        m_intro = new Intro(m_display); // startuje automatycznie
        //check_Point(AlertType.ALARM);
    }
    
    public void pauseApp() {
        if (m_store != null)
            m_store.write();
    }
    
    public void destroyApp(boolean unconditional) {
        m_display.setCurrent(null);
        notifyDestroyed();
    }
    
    public void startUI(){
        if (MainMIDlet.DEBUG) System.err.println("startUI()");
        m_intro = null;
        checkPassword();
    }
    ////////////////////////////////////////////////////////////
    static final byte CMD_EXIT = 0;
    static final byte CMD_MENU = 1;
    static final byte CMD_OK = 2;
    static final byte CMD_DOWNLOAD = 3;
    static final byte CMD_PLAY = 4;
//    static final byte CMD_FONTS = 5;
    static final byte CMD_STOP = 6;
    static final byte CMD_RESUME = 7;
    static final byte CMD_SING = 8;
    static final byte CMD_GOTO = 9;
    static final byte CMD_SHOW = 10;
    
    public static final short EXIT = 1 << CMD_EXIT;
    public static final short MENU = 1 << CMD_MENU;
    public static final short OK = 1 << CMD_OK;
    public static final short DOWNLOAD = 1 << CMD_DOWNLOAD;
    public static final short PLAY = 1 << CMD_PLAY;
//    public static final short FONTS = 1 << CMD_FONTS;
    public static final short STOP = 1 << CMD_STOP;
    public static final short RESUME = 1 << CMD_RESUME;
    public static final short SING = 1 << CMD_SING;
    public static final short GOTO = 1 << CMD_GOTO;
    public static final short SHOW = 1 << CMD_SHOW;
    // uwazaj na przekroczenie zakresu
    
    void createCommands(){
        m_commands = new Command[]{
            new Command(m_lang.m_dic[Lang.QUIT],   Command.EXIT, 99),
            new Command(m_lang.m_dic[Lang.MENU],   Command.BACK, 99),
            new Command(m_lang.m_dic[Lang.OK],     Command.OK, 7),
            new Command(m_lang.m_dic[Lang.DOWNLOAD],     Command.ITEM, 3),
            new Command(m_lang.m_dic[Lang.PLAY],     Command.SCREEN, 0),
            new Command(m_lang.m_dic[Lang.FONT],     Command.SCREEN, 0),
            new Command(m_lang.m_dic[Lang.STOP],     Command.SCREEN, 0),
            new Command(m_lang.m_dic[Lang.RESUME],     Command.SCREEN, 0),
            new Command(m_lang.m_dic[Lang.SING],     Command.SCREEN, 0),
            new Command(m_lang.m_dic[Lang.GOTO_URL],     Command.SCREEN, 0),
            new Command(m_lang.m_dic[Lang.CMD_SHOW],     Command.SCREEN, 0),
        };
    }
    
    
    public void setView(Displayable displayable, long flagsOfView){
        if (MainMIDlet.DEBUG) System.err.println("MainMIDlet.setView("+displayable+", "+flagsOfView+")");
        if (m_screen != null){
            for (int i=0; i<m_commands.length; i++){
                //if (MainMIDlet.DEBUG) System.err.println("MainMIDlet.setView() m_commands["+i+"]: "+m_commands[i]);
                if (m_commands[i] != null)
                    m_screen.removeCommand(m_commands[i]);
            }
        }
        if (MainMIDlet.DEBUG) System.err.println("MainMIDlet.setView() A");
        if (m_screen != displayable){
            m_screen = displayable;
            m_screen.setCommandListener(this);
            m_display.setCurrent(m_screen);
        }
        if (MainMIDlet.DEBUG) System.err.println("MainMIDlet.setView() B");
        for (int i=0; i<m_commands.length; i++){
            int maska = 1 << i;
            if ((flagsOfView & maska) == maska){
                m_screen.addCommand(m_commands[i]);
            }
        }
        if (MainMIDlet.DEBUG) System.err.println("MainMIDlet.setView() ... done");
    }
    public void commandAction(Command c, Displayable s) {
        if (c == m_commands[CMD_EXIT]) {
            cmdExit();
        } else if (c == m_commands[CMD_MENU]) {
            if (m_engine != null)
                m_engine.cmdStop();
            cmdMenu();
//    } else if (c == m_commands[CMD_FONTS]) {
//        cmdFonts();
        } else if (c == m_commands[CMD_SING]) {
            if (m_engine != null)
                m_engine.cmdSing();
        } else if (c == m_commands[CMD_STOP]) {
            if (m_engine != null)
                m_engine.cmdStop();
        } else if (c == m_commands[CMD_GOTO]) {
            String url = ((StringItem)m_form.get(1)).getText();
            Platform platform = new Platform(this);
            cmdMenu();
            boolean success = platform.platformRequest(url);
            
        } else{
            // listy i OK-eje
            switch (m_activeForm){
                case FORM_LIST_MENU:{
                    if (c == List.SELECT_COMMAND ){
                        switch(((List)s).getSelectedIndex()){
                            case 0:{
                                cmdPlay();
                                break;
                            }
                            case 1:{
                                cmdListSongsMine();
                                break;
                            }
                            case 2:{
                                cmdSettings();
                                break;
                            }
                            case 3:{
                                cmdHelp();
                                break;
                            }
                            case 4:{
                                cmdInfo();
                                break;
                            }
                            case 5:{
                                cmdExit();
                                break;
                            }
                        }
                    }
                    break;
                }
                case FORM_LIST_SONGS:{
                    if (c == m_commands[CMD_SHOW]) {
                        int idx = ((List)s).getSelectedIndex();
                        int songs_count = m_store.getSongNames().length;
                        if (idx >= songs_count){
                            if (DOWNLOADABLE){
                                if (idx - songs_count == 0){
                                    // przedostatnia pozycja
                                    cmdInfoSongFromServer();
                                    return;
                                } else if (idx - songs_count == 1){
                                    // ostania pozycja
                                    cmdInfoSongFromInternet();
                                    return;
                                }
                            }
                        } else if (idx >= 0){
                            cmdInfoSong(m_store.getSong(idx));
                        }
                    } else if (c == List.SELECT_COMMAND ){
                        int idx = ((List)s).getSelectedIndex();
                        int songs_count = m_store.getSongNames().length;
                        if (idx >= songs_count){
                            if (DOWNLOADABLE){
                                if (idx - songs_count == 0){
                                    cmdServerSongs();
                                    return;
                                } else if (idx - songs_count == 1){
                                    cmdInternetSong();
                                    return;
                                }
                            }
                        } else if (idx >= 0){
                            m_store.setInteger(Store.INT_SONG, idx);
                            cmdPlay();
                        }
                    }
                    break;
                }
                case FORM_LIST_SETTINGS:{
                    if (c == List.SELECT_COMMAND ){
                        switch(((List)s).getSelectedIndex()){
                            case 0:{
                                cmdFonts();
                                break;
                            }
                            case 1:{
                                cmdColors();
                                break;
                            }
                            case 2:{
                                cmdSettingsMore();
                                break;
                            }
                            case 3:{
                                cmdLanguage();
                                break;
                            }
                            case 4:{
                                m_store.restore();
                                cmdMenu();
                                break;
                            }
                        }
                    }
                    break;
                }
                case FORM_FONTS:{
                    if (c == m_commands[CMD_OK]){
                        int font = m_store.getInteger(Store.INT_FONT);
                        ChoiceGroup faceChoice = (ChoiceGroup)m_form.get(0);
                        ChoiceGroup styleChoice = (ChoiceGroup)m_form.get(1);
                        ChoiceGroup sizeChoice = (ChoiceGroup)m_form.get(2);
                        font &= ~(Font.FACE_MONOSPACE|Font.FACE_PROPORTIONAL|Font.FACE_SYSTEM);
                        switch (faceChoice.getSelectedIndex()) {
                            case 0: font |=  Font.FACE_SYSTEM; break;
                            case 1: font |= Font.FACE_MONOSPACE; break;
                            case 2: font |=  Font.FACE_PROPORTIONAL; break;
                        }
                        font &= ~(Font.STYLE_BOLD|Font.STYLE_ITALIC|Font.STYLE_UNDERLINED|FLAG_FONT_OUTLINE|FLAG_FONT_SHADOW);
                        if (styleChoice.isSelected(0))
                            font |=  Font.STYLE_BOLD;
                        if (styleChoice.isSelected(1))
                            font |=  Font.STYLE_ITALIC;
                        if (styleChoice.isSelected(2))
                            font |=  Font.STYLE_UNDERLINED;
                        if (styleChoice.isSelected(3))
                            font |=  FLAG_FONT_OUTLINE;
                        if (styleChoice.isSelected(4))
                            font |=  FLAG_FONT_SHADOW;
                        font &= ~(Font.SIZE_LARGE|Font.SIZE_MEDIUM|Font.SIZE_SMALL);
                        switch (sizeChoice.getSelectedIndex()) {
                            case 0: font |=  Font.SIZE_SMALL; break;
                            case 1: font |=  Font.SIZE_MEDIUM; break;
                            case 2: font |=  Font.SIZE_LARGE; break;
                        }
                        m_store.setInteger(Store.INT_FONT, font);
                        cmdSettings();
                    }
                    break;
                }
                case FORM_LIST_COLORS:{
                    if (c == m_commands[CMD_OK]){
                        if (m_colorChooser != null){
                            //palette = { background, after, active, foreground, outline, shadow };
                            m_store.setInteger(Store.INT_COLOR_BACKGROUND, m_colorChooser.palette[0]);
                            m_store.setInteger(Store.INT_COLOR_OLD, m_colorChooser.palette[1]);
                            m_store.setInteger(Store.INT_COLOR_ACTIVE, m_colorChooser.palette[2]);
                            m_store.setInteger(Store.INT_COLOR_NEW0, m_colorChooser.palette[3]);
                            m_store.setInteger(Store.INT_COLOR_NEW1, m_colorChooser.palette[4]);
                            m_store.setInteger(Store.INT_COLOR_OUTLINE, m_colorChooser.palette[5]);
                            m_store.setInteger(Store.INT_COLOR_SHADOW, m_colorChooser.palette[6]);
                            m_store.setInteger(Store.INT_COLOR_GRADIENT0, m_colorChooser.palette[7]);
                            m_store.setInteger(Store.INT_COLOR_GRADIENT9, m_colorChooser.palette[8]);
                            m_colorChooser = null;
                        }
                        cmdSettings();
                    }
                    break;
                }
                case FORM_LIST_SETTINGSMORE:{
                    if (c == m_commands[CMD_OK]){
                        try{
                            m_store.setInteger(Store.INT_VOLUME, Integer.parseInt(((TextField)m_form.get(0)).getString()));
                            m_store.setInteger(Store.INT_PREVIEW, Integer.parseInt(((TextField)m_form.get(1)).getString()));
                            m_store.setInteger(Store.INT_INTERLINE, Integer.parseInt(((TextField)m_form.get(2)).getString()));
                            m_store.setInteger(Store.INT_LINEHISTORY, Integer.parseInt(((TextField)m_form.get(3)).getString()));
                            m_store.setInteger(Store.INT_LINEBUFFOR, Integer.parseInt(((TextField)m_form.get(4)).getString()));
                            m_store.setInteger(Store.INT_LINEBASE, Integer.parseInt(((TextField)m_form.get(5)).getString()));
                            ChoiceGroup bkgChoice = (ChoiceGroup)m_form.get(6);
                            int bkg = 0;
                            if (bkgChoice.isSelected(0))
                                bkg |=  FLAG_GRADIENT;
                            if (bkgChoice.isSelected(1))
                                bkg |=  FLAG_BACKGROUND;
                            if (bkgChoice.isSelected(2))
                                bkg |=  FLAG_PANEL;
                            if (bkgChoice.isSelected(3))
                                bkg |=  FLAG_ADVERTISMENT;
                            if (bkgChoice.isSelected(4))
                                bkg |=  FLAG_LATIN;
                            m_store.setInteger(Store.INT_BACKGROUND, bkg);
                            m_lang = new Lang(m_store.getInteger(Store.INT_LANGS));
                            createCommands();
                        } catch (Exception e){
                            if (MainMIDlet.DEBUG) System.err.println(e.toString());
                        }
                        cmdSettings();
                    }
                    break;
                }
                case FORM_LIST_LANGS:{
                    if (c == m_commands[CMD_OK]){
                        ChoiceGroup langChoice = (ChoiceGroup)m_form.get(0);
                        m_store.setInteger(Store.INT_LANGS, langChoice.getSelectedIndex());
                        m_lang = new Lang(m_store.getInteger(Store.INT_LANGS));
                        createCommands();
                        cmdSettings();
                    }
                    break;
                }
                case FORM_LIST_INFO:{
                    if (c == List.SELECT_COMMAND ){
                        int idx = ((List)s).getSelectedIndex();
                        if (idx == 0){
                            cmdAbout();
                        } else{
                            cmdAdv(idx-1);
                            // tu trza wstawiæ cosik z reklam
                        }
                    }
                    break;
                }
                case FORM_LIST_SONGS_SERVER:{
                    if (c == m_commands[CMD_DOWNLOAD]){
                        int idx = ((List)s).getSelectedIndex();
                        cmdDownload(idx);
                    }
                    break;
                }
                case FORM_PASSWORD:{
                    if (c == m_commands[CMD_OK]){
                        try{
                            m_store.setString(Store.STR_PASSWORD, ((TextField)m_form.get(1)).getString());
                            checkPassword();
                        } catch (Exception e){
                            if (MainMIDlet.DEBUG) System.err.println(e.toString());
                        }
                    }
                    break;
                }
                case FORM_INTERNET_SONG:{
                    if (c == m_commands[CMD_OK]){
                        try{
                            m_store.setString(Store.STR_INTERNET_SONG, ((TextField)m_form.get(1)).getString());
                            cmdDownloadSongFromURL(m_store.getString(Store.STR_INTERNET_SONG));
                        } catch (Exception e){
                            if (MainMIDlet.DEBUG) System.err.println(e.toString());
                        }
                    }
                    break;
                }
                
            }
        }
        
        
        
        
    }
    
/////////////////////////////////////////////////////////////
    
    static final byte FORM_LIST_MENU = 0;
    static final byte FORM_LIST_SONGS = 1;
    static final byte FORM_LIST_SETTINGS = 2;
    static final byte FORM_FONTS = 3;
    static final byte FORM_LIST_COLORS = 4;
    static final byte FORM_LIST_SETTINGSMORE = 5;
    static final byte FORM_LIST_LANGS = 6;
    static final byte FORM_LIST_INFO = 7;
    static final byte FORM_LIST_ADV = 8;
    static final byte FORM_LIST_DOWNLOAD = 9;
    static final byte FORM_LIST_DELETE = 10;
    static final byte FORM_LIST_ABOUT = 11;
    static final byte FORM_LIST_SERVERSONGS = 12;
    static final byte FORM_LIST_SONGS_SERVER = 13;
    static final byte FORM_PASSWORD = 14;
    static final byte FORM_INTERNET_SONG = 15;
    
    public int m_activeForm = -1;
    
    public void cmdPassword(){
        m_form = new Form(m_lang.m_dic[Lang.NET_PASSWORD]);
        m_form.append(m_lang.m_dic[Lang.NET_LOGIN] + m_jadLogin);
        m_form.append(new TextField(m_lang.m_dic[Lang.NET_PASSWORD_ENTER], "", 30, TextField.PASSWORD));
        m_activeForm = FORM_PASSWORD;
        setView(m_form, EXIT|OK);
    }
    public void cmdMenu(){
        if (MainMIDlet.DEBUG) System.err.println("cmdMenu()");
        if (m_engine != null){
            m_engine.destroy();
            m_engine = null;
        }
        if (MainMIDlet.DEBUG) System.err.println("cmdMenu() 2");
        List menu = new List(m_lang.m_dic[Lang.MENU], List.IMPLICIT);
        if (MainMIDlet.DEBUG) System.err.println("cmdMenu() 3 m_store:"+m_store);
        String[] songNames = m_store.getSongNames();
        if (MainMIDlet.DEBUG) System.err.println("cmdMenu() 4");
        int song_idx = m_store.getInteger(Store.INT_SONG);
        if (MainMIDlet.DEBUG) System.err.println("cmdMenu() 4a song_idx:"+song_idx+", songNames.length:"+songNames.length);
        if (song_idx >= songNames.length){
            song_idx = 0;
            m_store.setInteger(Store.INT_SONG, song_idx);
        }
        if (MainMIDlet.DEBUG) System.err.println("cmdMenu() 5");
        String songName = songNames[song_idx];
        if (MainMIDlet.DEBUG) System.err.println("cmdMenu() 6");
        menu.append(m_lang.m_dic[Lang.PLAY]+" ("+songName+")", null);
        menu.append(m_lang.m_dic[Lang.MY_SONGS], null);
        menu.append(m_lang.m_dic[Lang.SETTINGS], null);
        menu.append(m_lang.m_dic[Lang.HELP], null);
        menu.append(m_lang.m_dic[Lang.INFO], null);
        menu.append(m_lang.m_dic[Lang.QUIT], null);
        m_activeForm = FORM_LIST_MENU;
        setView(menu, EXIT);
    }
    public void cmdExit(){
        if (m_store != null)
            m_store.close();
        destroyApp(true);
    }
    public void cmdListSongsMine(){
        List list = new List(m_lang.m_dic[Lang.MY_SONGS], List.IMPLICIT);
        String songs[] = m_store.getSongNames();
        int actualSong = m_store.getInteger(Store.INT_SONG);
        for (int i=0; i<songs.length; i++){
            list.append((i+1)+(i==actualSong?"!":".")+" "+songs[i], null);
        }
        if (DOWNLOADABLE){
            list.append(" ( "+m_lang.m_dic[Lang.SERVER_SONGS]+" )", null);
            list.append(" ( "+m_lang.m_dic[Lang.INTERNET_SONG]+" )", null);
        }
        m_activeForm = FORM_LIST_SONGS;
        setView(list, MENU|SHOW);
    }
    public void cmdListSongsServer(){
        List list = new List(m_lang.m_dic[Lang.SERVER_SONGS], List.IMPLICIT);
        for (int i=0; i<m_songsServerList.size(); i++){
            String[] item = (String[])m_songsServerList.elementAt(i);
            list.append(item[1], null);
        }
        m_activeForm = FORM_LIST_SONGS_SERVER;
        setView(list, MENU|DOWNLOAD);
    }
    public void cmdSettings(){
        List menu = new List(m_lang.m_dic[Lang.SETTINGS], List.IMPLICIT);
        menu.append(m_lang.m_dic[Lang.FONT], null);
        menu.append(m_lang.m_dic[Lang.COLORS], null);
        menu.append(m_lang.m_dic[Lang.SETTINGS_MORE], null);
        menu.append(m_lang.m_dic[Lang.LANGUAGE], null);
        menu.append(m_lang.m_dic[Lang.RESTORE_DEFAULT], null);
        m_activeForm = FORM_LIST_SETTINGS;
        setView(menu, MENU);
    }
    public void cmdFonts(){
        m_form = new Form(m_lang.m_dic[Lang.FONT]);
        int font = m_store.getInteger(Store.INT_FONT);
        ChoiceGroup faceChoice = new ChoiceGroup(m_lang.m_dic[Lang.FACE], Choice.EXCLUSIVE);
        faceChoice.append(m_lang.m_dic[Lang.FACE_SYSTEM], null);
        faceChoice.append(m_lang.m_dic[Lang.FACE_MONOSPACE], null);
        faceChoice.append(m_lang.m_dic[Lang.FACE_PROPORTIONAL], null);
        ChoiceGroup styleChoice = new ChoiceGroup(m_lang.m_dic[Lang.STYLE], Choice.MULTIPLE);
        styleChoice.append(m_lang.m_dic[Lang.STYLE_BOLD], null);
        styleChoice.append(m_lang.m_dic[Lang.STYLE_ITALIC], null);
        styleChoice.append(m_lang.m_dic[Lang.STYLE_UNDERLINED], null);
        styleChoice.append(m_lang.m_dic[Lang.STYLE_OUTLINE], null);
        styleChoice.append(m_lang.m_dic[Lang.STYLE_SHADOW], null);
        ChoiceGroup sizeChoice = new ChoiceGroup(m_lang.m_dic[Lang.SIZE], Choice.EXCLUSIVE);
        sizeChoice.append(m_lang.m_dic[Lang.SIZE_SMALL] , null);
        sizeChoice.append(m_lang.m_dic[Lang.SIZE_MEDIUM] , null);
        sizeChoice.append(m_lang.m_dic[Lang.SIZE_LARGE] , null);
        int fontFlags = MainMIDlet.m_instance.m_store.getInteger(Store.INT_FONT);
        int face = 0;
        if ((fontFlags & Font.FACE_MONOSPACE) == Font.FACE_MONOSPACE)
            faceChoice.setSelectedIndex(1, true);
        else if ((fontFlags & Font.FACE_PROPORTIONAL) == Font.FACE_PROPORTIONAL)
            faceChoice.setSelectedIndex(2, true);
        else
            faceChoice.setSelectedIndex(0, true);
        int style = 0;
        if ((fontFlags & Font.STYLE_BOLD) == Font.STYLE_BOLD)
            styleChoice.setSelectedIndex(0, true);
        if ((fontFlags & Font.STYLE_ITALIC) == Font.STYLE_ITALIC)
            styleChoice.setSelectedIndex(1, true);
        if ((fontFlags & Font.STYLE_UNDERLINED) == Font.STYLE_UNDERLINED)
            styleChoice.setSelectedIndex(2, true);
        if ((fontFlags & MainMIDlet.FLAG_FONT_OUTLINE) == MainMIDlet.FLAG_FONT_OUTLINE)
            styleChoice.setSelectedIndex(3, true);
        if ((fontFlags & MainMIDlet.FLAG_FONT_SHADOW) == MainMIDlet.FLAG_FONT_SHADOW)
            styleChoice.setSelectedIndex(4, true);
        int size = 0;
        if ((fontFlags & Font.SIZE_SMALL) == Font.SIZE_SMALL)
            sizeChoice.setSelectedIndex(0, true);
        else if ((fontFlags & Font.SIZE_LARGE) == Font.SIZE_LARGE)
            sizeChoice.setSelectedIndex(2, true);
        else
            sizeChoice.setSelectedIndex(1, true);
        
        m_form.append(faceChoice);
        m_form.append(styleChoice);
        m_form.append(sizeChoice);
        
        m_activeForm = FORM_FONTS;
        setView(m_form, MENU|OK);
    }
    public void cmdColors(){
        int foreground0 = m_store.getInteger(Store.INT_COLOR_NEW0);
        int foreground1 = m_store.getInteger(Store.INT_COLOR_NEW1);
        int background = m_store.getInteger(Store.INT_COLOR_BACKGROUND);
        int active = m_store.getInteger(Store.INT_COLOR_ACTIVE);
        int after = m_store.getInteger(Store.INT_COLOR_OLD);
        int outline = m_store.getInteger(Store.INT_COLOR_OUTLINE);
        int shadow = m_store.getInteger(Store.INT_COLOR_SHADOW);
        int gradient0 = m_store.getInteger(Store.INT_COLOR_GRADIENT0);
        int gradient9 = m_store.getInteger(Store.INT_COLOR_GRADIENT9);
        int[] palette = { background, after, active, foreground0, foreground1, outline, shadow, gradient0, gradient9 };
        int font = m_store.getInteger(Store.INT_FONT);
        m_colorChooser = new ColorChooser(m_display.isColor(), m_display.numColors(), palette, font);
        m_activeForm = FORM_LIST_COLORS;
        setView(m_colorChooser, MENU|OK);
    }
    public void cmdSettingsMore(){
        m_form = new Form(m_lang.m_dic[Lang.SETTINGS_MORE]);
        if (m_store.getInteger(Store.INT_VOLUME) > 100)
            m_store.setInteger(Store.INT_VOLUME,100);
        m_form.append(new TextField(m_lang.m_dic[Lang.VOLUME], ""+m_store.getInteger(Store.INT_VOLUME), 3, TextField.NUMERIC));
        m_form.append(new TextField(m_lang.m_dic[Lang.PREVIEW_TIME], ""+m_store.getInteger(Store.INT_PREVIEW), 4, TextField.NUMERIC));
        m_form.append(new TextField(m_lang.m_dic[Lang.INTERLINE], ""+m_store.getInteger(Store.INT_INTERLINE), 2, TextField.NUMERIC));
        m_form.append(new TextField(m_lang.m_dic[Lang.LINEHISTORY], ""+m_store.getInteger(Store.INT_LINEHISTORY), 2, TextField.NUMERIC));
        m_form.append(new TextField(m_lang.m_dic[Lang.LINEBUFFOR], ""+m_store.getInteger(Store.INT_LINEBUFFOR), 2, TextField.NUMERIC));
        m_form.append(new TextField(m_lang.m_dic[Lang.LINEBASE], ""+m_store.getInteger(Store.INT_LINEBASE), 4, TextField.NUMERIC));
        ChoiceGroup bkgChoice = new ChoiceGroup(m_lang.m_dic[Lang.BACKGROUNG], Choice.MULTIPLE);
        bkgChoice.append(m_lang.m_dic[Lang.GRADIENT] , null);
        bkgChoice.append(m_lang.m_dic[Lang.WALLPAPPER] , null);
        bkgChoice.append(m_lang.m_dic[Lang.PANEL_SHOW] , null);
        bkgChoice.append(m_lang.m_dic[Lang.ADVERTISMENT] , null);
        bkgChoice.append(m_lang.m_dic[Lang.CONV_DESC] , null);
        int bkgFlags = m_store.getInteger(Store.INT_BACKGROUND);
        if ((bkgFlags & FLAG_GRADIENT) == FLAG_GRADIENT)
            bkgChoice.setSelectedIndex(0, true);
        if ((bkgFlags & FLAG_BACKGROUND) == FLAG_BACKGROUND)
            bkgChoice.setSelectedIndex(1, true);
        if ((bkgFlags & FLAG_PANEL) == FLAG_PANEL)
            bkgChoice.setSelectedIndex(2, true);
        if ((bkgFlags & FLAG_ADVERTISMENT) == FLAG_ADVERTISMENT)
            bkgChoice.setSelectedIndex(3, true);
        if ((bkgFlags & FLAG_LATIN) == FLAG_LATIN)
            bkgChoice.setSelectedIndex(4, true);
        m_form.append(bkgChoice);
        m_activeForm = FORM_LIST_SETTINGSMORE;
        setView(m_form, MENU|OK);
    }
    public void cmdLanguage(){
        m_form = new Form(m_lang.m_dic[Lang.LANGUAGE]);
        String langs[] = m_lang.getLangs();
        ChoiceGroup langChoice = new ChoiceGroup(m_lang.m_dic[Lang.SIZE], Choice.EXCLUSIVE);
        langChoice.append(m_lang.m_dic[Lang.LANG_AUTO] , null);
        for (int i=0; i<langs.length; i++){
            langChoice.append(langs[i] , null);
        }
        langChoice.setSelectedIndex(m_store.getInteger(Store.INT_LANGS), true);
        m_form.append(langChoice);
        m_activeForm = FORM_LIST_LANGS;
        setView(m_form, MENU|OK);
    }
    public void cmdPlay(){
        if (MainMIDlet.DEBUG) System.err.println("cmdPlay()");
        m_engine = new Engine();
    }
    public void cmdInfo(){
        List menu = new List(m_lang.m_dic[Lang.INFO], List.IMPLICIT);
        menu.append(m_lang.m_dic[Lang.ABOUT], null);
        String advNames[] = m_store.getAdvNames();
        for (int i=0; i<advNames.length; i++){
            menu.append(advNames[i], null);
        }
        m_activeForm = FORM_LIST_INFO;
        setView(menu, MENU);
    }
    public void cmdHelp(){
        m_form = new Form(m_lang.m_dic[Lang.HELP]);
        m_form.append(m_lang.m_dic[Lang.HELP_DESC]);
        m_form.append("http://wap.mobikar.net/help.php");
        m_activeForm = FORM_LIST_LANGS;
        Platform platform = new Platform(this);
        if (platform.isSupported()){
            setView(m_form, MENU|GOTO);
        } else{
            setView(m_form, MENU);
        }
    }
    public void cmdAbout(){
        m_form = new Form(m_lang.m_dic[Lang.ABOUT]);
        if (HISTORY){
            // wstawki moje
            int bkgFlags = m_store.getInteger(Store.INT_BACKGROUND);
            m_form.append("bkgFlags:"+bkgFlags+"\n");
            m_form.append("FLAG_BACKGROUND:"+((bkgFlags & FLAG_BACKGROUND) == FLAG_BACKGROUND)+"\n");
            m_form.append("FLAG_PANEL:"+((bkgFlags & FLAG_PANEL) == FLAG_PANEL)+"\n");
            m_form.append("FLAG_ADVERTISMENT:"+((bkgFlags & FLAG_ADVERTISMENT) == FLAG_ADVERTISMENT)+"\n");
            m_form.append("history:\n");
            for (int i=0; i<history.size(); i++){
                m_form.append(""+history.elementAt(i)+"\n");
            }
            history.removeAllElements();
            m_form.append("-------\n");
        }
        m_form.append(m_lang.m_dic[Lang.ABOUT_DESC]+"\n");
        m_form.append("http://mobikar.net");
        m_form.append(new StringItem(m_lang.m_dic[Lang.VERSION]," "+getAppProperty("MIDlet-Version")));
        m_form.append(new StringItem(m_lang.m_dic[Lang.CREATORS], " Micha³ Kud³a"));
        m_activeForm = FORM_LIST_ABOUT;
        Platform platform = new Platform(this);
        if (platform.isSupported()){
            setView(m_form, MENU|GOTO);
        } else{
            setView(m_form, MENU);
        }
    }
    public void cmdAdv(int no){
        String name = "m1k0.com";
//        String text = "KomKom to komórkowy komunikator.\n"
//                +"Dziêki niemu i telefonowi mo¿esz byæ stale w kontakcie z przyció³mi, "
//                +" którzy korzystaj¹ z przeró¿nych komunikatorów tkj. WPKontakt, Gadu-Gadu, Tlen\n"
//                +"Wiêcej informacji na stronie domowej KomKom.PL\n";
//        String link = "http://komkom.pl";
        String text = "m1k0.com.\n"
                +"We also develop KomKom - cellular communicator"
                +"More information at web page http://m1k0.com\n";
        String link = "http://m1k0.com";
        m_form = new Form(name);
        m_form.append(m_lang.convert(text));
        m_form.append(link);
        try{
            Image imgAdv = Image.createImage("/adv.png");
            m_form.append(imgAdv);
        } catch (Exception e){
            if (MainMIDlet.DEBUG) System.err.println("cmdAdv "+e);
        }
        m_activeForm = FORM_LIST_ADV;
        Platform platform = new Platform(this);
        if (platform.isSupported()){
            setView(m_form, MENU|GOTO);
        } else{
            setView(m_form, MENU);
        }
    }
    public void cmdDownload(final int indexSong){
        m_form = new Form(m_lang.m_dic[Lang.DOWNLOAD]);
        m_form.append("");
        m_activeForm = FORM_LIST_DOWNLOAD;
        setView(m_form, MENU|OK);
        new Thread() {
            public void run() {
                StringItem strItemNotifier = (StringItem)m_form.get(0);
                try{
                    String songId = ((String[])m_songsServerList.elementAt(indexSong))[0];
                    String strResponse = getStanza("<song id='"+songId+"'/>", strItemNotifier);
                    download(strResponse, strItemNotifier);
                } catch (Exception e){
                    strItemNotifier.setText(e.getMessage());
                    if (MainMIDlet.DEBUG) e.printStackTrace();
                }
            }
        }.start();
    }
    public void cmdDownloadSongFromURL(final String url){
        m_form = new Form(m_lang.m_dic[Lang.INTERNET_SONG]);
        m_form.append("");
        m_activeForm = FORM_LIST_DOWNLOAD;
        setView(m_form, MENU);
        new Thread() {
            public void run() {
                StringItem strItemNotifier = (StringItem)m_form.get(0);
                try{
                    // pobranie opisu z url-a
                    strItemNotifier.setText("URL: "+url+" ... "+m_lang.m_dic[Lang.CONNECTING]);
                    StreamConnection conn = (StreamConnection)Connector.open(url, Connector.READ);
                    XmlReader reader = new XmlReader(new DataInputStream(conn.openInputStream()));
                    String strResponse = "";
                    int c = 0;
                    int count = 0;
                    while ((c = reader.read()) != -1){
                        strResponse += ((char)c);
                        if (MainMIDlet.DEBUG) System.err.print(""+(char)c);
                    }
                    reader.close();
                    reader = null;
                    conn.close();
                    conn = null;
                    // pobranie piosenki
                    download(strResponse, strItemNotifier);
                } catch (Exception e){
                    strItemNotifier.setText(m_lang.m_dic[Lang.PROBLEM]+e.toString());
                    if (MainMIDlet.DEBUG) e.printStackTrace();
                }
            }
        }.start();
    }
    private void download(final String stanza, final StringItem strItemNotifier){
        setView(m_form, MENU);
        String strResponse = stanza;
        String notice = "Problem";
        if (strResponse != null){
            int idx=strResponse.indexOf("<notice");
            if (idx != -1){
                idx=strResponse.indexOf("<describe", idx);
                if (idx != -1)
                    idx=strResponse.indexOf("<describe", idx);
                if (idx != -1){
                    idx=strResponse.indexOf(">", idx);
                    int idxEnd = strResponse.indexOf("</", idx);
                    notice = strResponse.substring(idx+1, idxEnd).trim();
                }
            } else{
                notice = null;
            }
            
            idx = strResponse.indexOf("name");
            if (idx != -1){
                String songName = getValFromTag(strResponse, "name", 0)
                + " - " + getValFromTag(strResponse, "artist", 0);
                idx = strResponse.indexOf("melody");
                String addrMidi = getValFromTag(strResponse, "addr", strResponse.indexOf("melody"));
                String addrMlyr = getValFromTag(strResponse, "addr", strResponse.indexOf("lyric"));
                StreamConnection conn = null;
                InputStream is = null;
                try{
                    strItemNotifier.setText(m_lang.m_dic[Lang.CONNECTING]);
                    conn = (StreamConnection)Connector.open(addrMidi, Connector.READ);
                    strItemNotifier.setText(m_lang.m_dic[Lang.RECEIVING]);
                    is = conn.openInputStream();
                    byte[] dataMidi = getBytes(is);
                    is.close();
                    strItemNotifier.setText(m_lang.m_dic[Lang.CONNECTING]);
                    conn = (StreamConnection)Connector.open(addrMlyr, Connector.READ);
                    strItemNotifier.setText(m_lang.m_dic[Lang.RECEIVING]);
                    is = conn.openInputStream();
                    byte[] dataMlyr = getBytes(is);
                    is.close();
                    strItemNotifier.setText(songName);
                    m_store.setSongRms(songName, dataMidi, dataMlyr);
                } catch (Exception e){
                    strItemNotifier.setText(m_lang.m_dic[Lang.PROBLEM]+e.toString());
                    if (MainMIDlet.DEBUG) e.printStackTrace();
                }
            }
        }
        try{
            Thread.sleep(1000);
        } catch (Exception e){
            if (MainMIDlet.DEBUG) e.printStackTrace();
        }
        cmdListSongsMine();
    }
    public void cmdServerSongs(){
        m_form = new Form(m_lang.m_dic[Lang.SERVER_SONGS]);
        m_form.append("");
        m_activeForm = FORM_LIST_SERVERSONGS;
        setView(m_form, MENU);
        new Thread() {
            public void run() {
                StringItem strItem = (StringItem)m_form.get(0);
                try{
                    String strResponse = getStanza("<list offset='0' limit='11'/>", strItem);
                    String notice = m_lang.m_dic[Lang.PROBLEM];
                    if (strResponse != null){
                        int idx=strResponse.indexOf("<notice");
                        if (idx != -1){
                            idx=strResponse.indexOf("<describe", idx);
                            if (idx != -1)
                                idx=strResponse.indexOf("<describe", idx);
                            if (idx != -1){
                                idx=strResponse.indexOf(">", idx);
                                int idxEnd = strResponse.indexOf("</", idx);
                                notice = strResponse.substring(idx+1, idxEnd).trim();
                            }
                        } else{
                            notice = null;
                        }
                        m_songsServerList = new Vector();
                        idx = strResponse.indexOf("<list");
                        if (idx != -1){
                            while ((idx = strResponse.indexOf("<item", idx+1)) != -1){
                                String id = getValFromTag(strResponse, "id", idx);
                                String name = getValFromTag(strResponse, "name", idx);
                                m_songsServerList.addElement(new String[]{id, name});
                            }
                        }
                        if (MainMIDlet.DEBUG){
                            System.err.println("m_songsServerList.size(): "+m_songsServerList.size());
                            for (int i=0; i<m_songsServerList.size(); i++){
                                String[] item = (String[])m_songsServerList.elementAt(i);
                                System.err.println("["+item[0]+"]: "+item[1]);
                            }
                        }
                        cmdListSongsServer();
                    }
                    if (notice != null)
                        strItem.setText(notice);
                    
                } catch (Exception e){
                    strItem.setText(e.getMessage());
                    if (MainMIDlet.DEBUG) e.printStackTrace();
                }
            }
        }.start();
        
    }
    public void cmdInternetSong(){
        m_form = new Form(m_lang.m_dic[Lang.INTERNET_SONG]);
        m_form.append(m_lang.m_dic[Lang.INTERNET_SONG_ABOUT]);
        m_form.append(new TextField(m_lang.m_dic[Lang.INTERNET_SONG_ENTER],
                m_store.getString(Store.STR_INTERNET_SONG), 250, TextField.URL));
        m_activeForm = FORM_INTERNET_SONG;
        setView(m_form, MENU|OK);
    }
    
    public static void check_Point(AlertType alertType){
//        alertType.playSound(Display.getDisplay(MainMIDlet.m_instance));
//        try{
//            Thread.sleep(1000);
//        } catch (Exception e){
//        }
//        try{
//            com.samsung.util.AudioClip clip =
//                new com.samsung.util.AudioClip(1, "/intro.mmf");
//            boolean isSupported = clip.isSupported();
//            clip.play(1, 5);
//            Thread.currentThread().sleep(3000);
//            clip.stop();
//        } catch (Exception e){
//        }
        
    }
    
    public String getValFromTag(String xmlData, String tag, int offset){
        String ret = null;
        int idx0 = xmlData.indexOf(tag, offset);
        if (idx0 != -1){
            int idx2 = idx0 + tag.length() + 2; // ='
            char apos = xmlData.charAt(idx2-1);
            int idx9 = xmlData.indexOf(apos, idx2);
            if (idx9 != -1)
                ret = xmlData.substring(idx2, idx9);
        }
        return ret;
    }
    byte[] getBytes(InputStream is){
        byte[] ret = null;
        try{
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int b = -1;
            while((b=is.read()) != -1){
                baos.write(b);
            }
            ret = baos.toByteArray();
        } catch (Exception e){
            if (MainMIDlet.DEBUG) System.err.println("getBytes "+e);
        }
        
        return ret;
    }
    String getStanza(String request, StringItem strItemNotifier){
        String ret = null;
        // oczytaæ z JADa nazwê providera
        // po³¹czyæ siê z http://mobiKAR.net/service
        String jad_provider = getAppProperty("mobiKAR-provider");
        String jad_browser = getAppProperty("mobiKAR-browser");
        String jad_login = getAppProperty("mobiKAR-login");
        String jad_password = getAppProperty("mobiKAR-password");
        String jad_key = getAppProperty("mobiKAR-key");
        String jad_url = getAppProperty("mobiKAR-service");
        if (jad_url == null)
            jad_url =  "http://mobiKAR.net/service/";
        StreamConnection conn = null;
        OutputStream os = null;
        XmlReader reader = null;
        try{
            if (MainMIDlet.DEBUG) System.err.println("Connecting to "+jad_url);
            strItemNotifier.setText(m_lang.m_dic[Lang.CONNECTING]);
            conn = (StreamConnection)Connector.open(jad_url, Connector.READ_WRITE);
            os = conn.openOutputStream();
            String strReq = "<?xml version=\"1.0\"?>";
            strReq +="<!DOCTYPE request SYSTEM 'http://mobikar.net/request.dtd'>\n";
            strReq +="<request>\n";
            strReq +="    <language name='pl'/>\n";
            strReq +="    <authorization login='"+jad_login+"' password='"+jad_password+"' />\n";
            strReq +="    <browser user-agent='"+jad_browser+"'/>\n";
            strReq +="    <property name='provider' value='"+jad_provider+"'/>\n";
            strReq +="    <property name='key' value='"+jad_key+"'/>\n";
            strReq +="    <property name='version' value='"+getAppProperty("MIDlet-Version")+"'/>\n";
            strReq +="    <property name='screen-width' value='"+m_width+"'/>\n";
            strReq +="    <property name='screen-height' value='"+m_height+"'/>\n";
            strReq +="    <get>\n";
            strReq += request;//"        <list offset='0' limit='11'/>\n";
            strReq +="    </get>\n";
            strReq +="</request>\n";
            os.write(strReq.getBytes());
            os.close();
            os = null;
            strItemNotifier.setText(m_lang.m_dic[Lang.RECEIVING]);
            reader = new XmlReader(new DataInputStream(conn.openInputStream()));
            String strResponse = "";
            int c = 0;
            int count = 0;
            while ((c = reader.read()) != -1){
                strResponse += ((char)c);
                if (MainMIDlet.DEBUG) System.err.print(""+(char)c);
            }
            reader.close();
            reader = null;
            conn.close();
            conn = null;
            ret = strResponse;
        } catch (Exception e){
            strItemNotifier.setText(e.getMessage());
            if (MainMIDlet.DEBUG) e.printStackTrace();
        } finally {
            try{
                if (os != null)
                    os.close();
                if (reader != null)
                    reader.close();
                if (conn != null)
                    conn.close();
            } catch (IOException ioe){
                if (MainMIDlet.DEBUG) ioe.printStackTrace();
            }
        }
        return ret;
    }
    private void checkPassword(){
        String password = new String(m_store.getString(Store.STR_PASSWORD));
        SHA1 s = new SHA1();
        s.init();
        s.updateASCII(password + m_jadSize);
        s.finish();
        String digout = s.digout().substring(0, 32);
        if (MainMIDlet.DEBUG) System.err.println("digout:"+digout);
        if (digout.equals(m_jadPassword) || !GUARD){
            cmdMenu();
        } else{
            cmdPassword();
        }
    }
    public void cmdInfoSong(Song song){
        int time_min = (int)(song.m_mlyrDuration / (1000*60));
        int time_sek = (int)((song.m_mlyrDuration % (1000*60)) / 1000);
        m_form = new Form(song.m_mlyrTitle);
        m_form.append(m_lang.m_dic[Lang.MLYR_TITLE]+song.m_mlyrTitle+"\n");
        m_form.append(m_lang.m_dic[Lang.MLYR_ARTIST]+song.m_mlyrArtist+"\n");
        m_form.append(m_lang.m_dic[Lang.MLYR_LYRICS]+song.m_mlyrLyrics+"\n");
        m_form.append(m_lang.m_dic[Lang.MLYR_MUSIC]+song.m_mlyrMusic+"\n");
        m_form.append(m_lang.m_dic[Lang.MLYR_CREATOR]+song.m_mlyrCreator+"\n");
        m_form.append(m_lang.m_dic[Lang.MLYR_VERSION]+song.m_mlyrVersion+"\n");
        m_form.append(m_lang.m_dic[Lang.MLYR_NOTE]+song.m_mlyrNote+"\n");
        m_form.append(m_lang.m_dic[Lang.MLYR_TIME]+time_min+":"+(time_sek<10?"0":"")+time_sek+"\n");
        m_form.append(m_lang.m_dic[Lang.MLYR_TEXT]+song.m_mlyrText.replace('/', '\n')+"\n");
        setView(m_form, MENU);
    }
    public void cmdInfoSongFromServer(){
        m_form = new Form(m_lang.m_dic[Lang.SERVER_SONGS]);
        m_form.append(m_lang.m_dic[Lang.SERVER_SONGS_ABOUT]);
        setView(m_form, MENU);
    }
    public void cmdInfoSongFromInternet(){
        m_form = new Form(m_lang.m_dic[Lang.INTERNET_SONG]);
        m_form.append(m_lang.m_dic[Lang.INTERNET_SONG_ABOUT]);
        setView(m_form, MENU);
    }
    
}
