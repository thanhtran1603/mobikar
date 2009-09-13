/*
 * MainMIDlet.java
 *
 * Created on 26 wrzesieñ 2004, 22:37
 */

import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

/**
 *
 * @author  MiKO
 * @version
 */
public class MainMIDlet extends MIDlet implements CommandListener,ItemStateListener{
    public static final boolean DEBUG = false;
    public static final boolean DOWNLOADABLE = false;
    public static MainMIDlet m_instance;
    Display m_display;
    Displayable m_screen;
    public Intro m_intro = null;
    
    public Store m_store = null;
    public Lang m_lang = null;
    Command m_commands[] = null;
    
    Engine m_engine = null;
    Form m_form = null;
    ColorChooser m_colorChooser = null;
    
    public final static int FLAG_FONT_OUTLINE = 1024;
    public final static int FLAG_FONT_SHADOW = 2048;
    public final static int FLAG_BACKGROUND = 1;
    public final static int FLAG_PANEL = 2;
    public final static int FLAG_ADVERTISMENT = 4;
    
    public void startApp() {
        m_instance = this;
        m_display = Display.getDisplay(this);
        if (MainMIDlet.DEBUG) System.err.println("A");
        m_intro = new Intro(m_display); // startuje automatycznie
        m_store = new Store();
        m_lang = new Lang(m_store.getInteger(Store.INT_LANGS));
        createCommands();
    }
    
    public void pauseApp() {
        if (m_store != null)
            m_store.write();
    }
    
    public void destroyApp(boolean unconditional) {
        notifyDestroyed();
    }
    
    public void startUI(){
        if (MainMIDlet.DEBUG) System.err.println("startUI()");
        m_display.setCurrent(null);
        m_intro = null;
        cmdMenu();
    }
    ////////////////////////////////////////////////////////////
    static final byte CMD_EXIT = 0;
    static final byte CMD_MENU = 1;
    static final byte CMD_OK = 2;
    static final byte CMD_RENAME = 3;
    static final byte CMD_DELETE = 4;
    static final byte CMD_DOWNLOAD = 5;
    static final byte CMD_PLAY = 6;
    static final byte CMD_FONTS = 7;
    static final byte CMD_STOP = 8;
    static final byte CMD_RESUME = 9;
    static final byte CMD_SING = 10;
    static final byte CMD_GOTO = 11;
    
    public static final short EXIT = 1 << CMD_EXIT;
    public static final short MENU = 1 << CMD_MENU;
    public static final short OK = 1 << CMD_OK;
    public static final short RENAME = 1 << CMD_RENAME;
    public static final short DELETE = 1 << CMD_DELETE;
    public static final short DOWNLOAD = 1 << CMD_DOWNLOAD;
    public static final short PLAY = 1 << CMD_PLAY;
    public static final short FONTS = 1 << CMD_FONTS;
    public static final short STOP = 1 << CMD_STOP;
    public static final short RESUME = 1 << CMD_RESUME;
    public static final short SING = 1 << CMD_SING;
    public static final short GOTO = 1 << CMD_GOTO;
    // uwazaj na przekroczenie zakresu
    
    void createCommands(){
        m_commands = new Command[]{
            new Command(m_lang.m_dic[Lang.QUIT],   Command.EXIT, 99),
            new Command(m_lang.m_dic[Lang.MENU],   Command.BACK, 99),
            new Command(m_lang.m_dic[Lang.OK],     Command.OK, 7),
            new Command(m_lang.m_dic[Lang.RENAME],     Command.ITEM, 1),
            new Command(m_lang.m_dic[Lang.DELETE],     Command.ITEM, 2),
            new Command(m_lang.m_dic[Lang.DOWNLOAD],     Command.ITEM, 3),
            new Command(m_lang.m_dic[Lang.PLAY],     Command.SCREEN, 0),
            new Command(m_lang.m_dic[Lang.FONT],     Command.SCREEN, 0),
            new Command(m_lang.m_dic[Lang.STOP],     Command.SCREEN, 0),
            new Command(m_lang.m_dic[Lang.RESUME],     Command.SCREEN, 0),
            new Command(m_lang.m_dic[Lang.SING],     Command.SCREEN, 0),
            new Command(m_lang.m_dic[Lang.GOTO_URL],     Command.SCREEN, 0),
        };
    }
    
    
    public void setView(Displayable displayable, long flagsOfView){
        if (m_screen != null)
            for (int i=0; i<m_commands.length; i++)
                m_screen.removeCommand(m_commands[i]);
        if (this.m_screen != displayable){
            this.m_screen = displayable;
            m_screen.setCommandListener(this);
        }
        m_screen.setCommandListener(this);
        for (int i=0; i<m_commands.length; i++){
            int maska = 1 << i;
            if ((flagsOfView & maska) == maska){
                m_screen.addCommand(m_commands[i]);
            }
        }
        m_display.setCurrent(m_screen);
    }
    public void commandAction(Command c, Displayable s) {
        if (c == m_commands[CMD_EXIT]) {
            cmdExit();
        }
        else if (c == m_commands[CMD_MENU]) {
            if (m_engine != null)
                m_engine.cmdStop();
            cmdMenu();
        }
        else if (c == m_commands[CMD_RENAME]) {
            if (MainMIDlet.DEBUG) System.err.println("m_commands[CMD_RENAME]: "+List.SELECT_COMMAND);
            if (MainMIDlet.DEBUG) System.err.println("List.SELECT_COMMAND: "+List.SELECT_COMMAND);
            if (MainMIDlet.DEBUG) System.err.println("((List)s).getSelectedIndex(): "+(((List)s).getSelectedIndex()));
            cmdRename();
        }
        else if (c == m_commands[CMD_FONTS]) {
            cmdFonts();
        }
        else if (c == m_commands[CMD_SING]) {
            if (m_engine != null)
                m_engine.cmdSing();
        }
        else if (c == m_commands[CMD_STOP]) {
            if (m_engine != null)
                m_engine.cmdStop();
        }
        else if (c == m_commands[CMD_GOTO]) {
            String url = ((StringItem)m_form.get(1)).getText();
            Platform platform = new Platform(this);
            cmdMenu();
            boolean success = platform.platformRequest(url);
        }
        else if (c == m_commands[CMD_OK] || c == List.SELECT_COMMAND) {
            //m_ui.cmdPlay();
            switch (m_activeForm){
                case FORM_LIST_MENU:{
                    switch(((List)s).getSelectedIndex()){
                        case 0:{
                            cmdPlay();
                            break;
                        }
                        case 1:{
                            cmdListSongs();
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
                    break;
                }
                case FORM_LIST_SONGS:{
                    int idx = ((List)s).getSelectedIndex();
                    if (DOWNLOADABLE){
                        if (idx == 0){
                            cmdDownload();
                            return;
                        }
                        else if (idx == 1){
                            cmdDelete();
                            return;
                        }
                        else{
                            idx -= 2;
                        }
                    }
                    if (idx >= 0){
                        m_store.setInteger(Store.INT_SONG, idx);
                        cmdPlay();
                    }
                    break;
                }
                case FORM_LIST_SETTINGS:{
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
                    }
                    break;
                }
                case FORM_FONTS:{
                    cmdSettings();
                    break;
                }
                case FORM_LIST_COLORS:{
                    if (m_colorChooser != null){
                        //palette = { background, after, active, foreground, outline, shadow };
                        m_store.setInteger(Store.INT_COLOR_BACKGROUND, m_colorChooser.palette[0]);
                        m_store.setInteger(Store.INT_COLOR_READ, m_colorChooser.palette[1]);
                        m_store.setInteger(Store.INT_COLOR_ACTIVE, m_colorChooser.palette[2]);
                        m_store.setInteger(Store.INT_COLOR_FOREGROUND, m_colorChooser.palette[3]);
                        m_store.setInteger(Store.INT_COLOR_OUTLINE, m_colorChooser.palette[4]);
                        m_store.setInteger(Store.INT_COLOR_SHADOW, m_colorChooser.palette[5]);
                        m_colorChooser = null;
                    }
                    cmdSettings();
                    break;
                }
                case FORM_LIST_SETTINGSMORE:{
                    try{
                        m_store.setInteger(Store.INT_VOLUME, Integer.parseInt(((TextField)m_form.get(0)).getString()));
                        m_store.setInteger(Store.INT_PREVIEW, Integer.parseInt(((TextField)m_form.get(1)).getString()));
                        m_store.setInteger(Store.INT_INTERLINE, Integer.parseInt(((TextField)m_form.get(2)).getString()));
                        m_store.setInteger(Store.INT_LINEHISTORY, Integer.parseInt(((TextField)m_form.get(3)).getString()));
                        m_store.setInteger(Store.INT_LINEBUFFOR, Integer.parseInt(((TextField)m_form.get(4)).getString()));
                        m_store.setInteger(Store.INT_LINEBASE, Integer.parseInt(((TextField)m_form.get(5)).getString()));
                    } catch (Exception e){
                        if (MainMIDlet.DEBUG) System.err.println(e.toString());
                    }
                    cmdSettings();
                    break;
                }
                case FORM_LIST_LANGS:{
                    m_lang = new Lang(m_store.getInteger(Store.INT_LANGS));
                    createCommands();
                    cmdSettings();
                    break;
                }
                case FORM_LIST_INFO:{
                    int idx = ((List)s).getSelectedIndex();
                    if (idx == 0){
                        cmdAbout();
                    }
                    else{
                        cmdAdv(idx-1);
                        // tu trza wstawiæ cosik z reklam
                    }
                    break;
                }
                
                
            }
        }
    }
    public void itemStateChanged(Item item) {
        switch (m_activeForm){
            case FORM_FONTS:{
                int font = m_store.getInteger(Store.INT_FONT);
                ChoiceGroup faceChoice = (ChoiceGroup)m_form.get(0);
                ChoiceGroup styleChoice = (ChoiceGroup)m_form.get(1);
                ChoiceGroup sizeChoice = (ChoiceGroup)m_form.get(2);
                if (item == faceChoice) {
                    font &= ~(Font.FACE_MONOSPACE|Font.FACE_PROPORTIONAL|Font.FACE_SYSTEM);
                    int f = faceChoice.getSelectedIndex();
                    switch (f) {
                        case 0: font |=  Font.FACE_SYSTEM; break;
                        case 1: font |= Font.FACE_MONOSPACE; break;
                        case 2: font |=  Font.FACE_PROPORTIONAL; break;
                    }
                }
                else if (item == styleChoice) {
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
                }
                else if (item == sizeChoice) {
                    font &= ~(Font.SIZE_LARGE|Font.SIZE_MEDIUM|Font.SIZE_SMALL);
                    int s = sizeChoice.getSelectedIndex();
                    switch (s) {
                        case 0: font |=  Font.SIZE_SMALL; break;
                        case 1: font |=  Font.SIZE_MEDIUM; break;
                        case 2: font |=  Font.SIZE_LARGE; break;
                    }
                }
                m_store.setInteger(Store.INT_FONT, font);
                break;
            }
            case FORM_LIST_SETTINGSMORE:{
                ChoiceGroup bkgChoice = (ChoiceGroup)m_form.get(6);
                int bkg = 0;
                if (bkgChoice.isSelected(0))
                    bkg |=  FLAG_BACKGROUND;
                if (bkgChoice.isSelected(1))
                    bkg |=  FLAG_PANEL;
                if (bkgChoice.isSelected(2))
                    bkg |=  FLAG_ADVERTISMENT;
                m_store.setInteger(Store.INT_BACKGROUND, bkg);
                break;
            }
            case FORM_LIST_LANGS:{
                ChoiceGroup langChoice = (ChoiceGroup)m_form.get(0);
                m_store.setInteger(Store.INT_LANGS, langChoice.getSelectedIndex());
                break;
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
    
    
    public int m_activeForm = -1;
    
    public void cmdMenu(){
        if (MainMIDlet.DEBUG) System.err.println("cmdMenu()");
        if (m_engine != null){
            m_engine.destroy();
            m_engine = null;
        }
        List menu = new List(m_lang.m_dic[Lang.MENU], List.IMPLICIT);
        String songName = m_store.getSongNames()[m_store.getInteger(Store.INT_SONG)];
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
    public void cmdListSongs(){
        List list = new List(m_lang.m_dic[Lang.MY_SONGS], List.IMPLICIT);
        if (DOWNLOADABLE){
            list.append("("+m_lang.m_dic[Lang.DOWNLOAD]+")", null);
            list.append("("+m_lang.m_dic[Lang.DELETE]+")", null);
        }
        String songs[] = m_store.getSongNames();
        int actualSong = m_store.getInteger(Store.INT_SONG);
        for (int i=0; i<songs.length; i++){
            list.append((i+1)+(i==actualSong?"!":".")+" "+songs[i], null);
        }
        m_activeForm = FORM_LIST_SONGS;
        setView(list, MENU|OK);
    }
    public void cmdRename(){
        
    }
    public void cmdSettings(){
        List menu = new List(m_lang.m_dic[Lang.SETTINGS], List.IMPLICIT);
        menu.append(m_lang.m_dic[Lang.FONT], null);
        menu.append(m_lang.m_dic[Lang.COLORS], null);
        menu.append(m_lang.m_dic[Lang.SETTINGS_MORE], null);
        menu.append(m_lang.m_dic[Lang.LANGUAGE], null);
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
        //  TODO: trza to oprogramowaæ
        //        m_sizeChoice.setSelectedFlags(
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
        
        m_form.setItemStateListener(this);
        m_activeForm = FORM_FONTS;
        setView(m_form, MENU|OK);
    }
    public void cmdColors(){
        int foreground = m_store.getInteger(Store.INT_COLOR_FOREGROUND);
        int background = m_store.getInteger(Store.INT_COLOR_BACKGROUND);
        int active = m_store.getInteger(Store.INT_COLOR_ACTIVE);
        int after = m_store.getInteger(Store.INT_COLOR_READ);
        int outline = m_store.getInteger(Store.INT_COLOR_OUTLINE);
        int shadow = m_store.getInteger(Store.INT_COLOR_SHADOW);
        int[] palette = { background, after, active, foreground, outline, shadow };
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
        bkgChoice.append(m_lang.m_dic[Lang.WALLPAPPER] , null);
        bkgChoice.append(m_lang.m_dic[Lang.PANEL_SHOW] , null);
        bkgChoice.append(m_lang.m_dic[Lang.ADVERTISMENT] , null);
        int bkgFlags = m_store.getInteger(Store.INT_BACKGROUND);
        if ((bkgFlags & FLAG_BACKGROUND) == FLAG_BACKGROUND)
            bkgChoice.setSelectedIndex(0, true);
        if ((bkgFlags & FLAG_PANEL) == FLAG_PANEL)
            bkgChoice.setSelectedIndex(1, true);
        if ((bkgFlags & FLAG_ADVERTISMENT) == FLAG_ADVERTISMENT)
            bkgChoice.setSelectedIndex(2, true);
        m_form.append(bkgChoice);
        m_form.setItemStateListener(this);
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
        m_form.setItemStateListener(this);
        setView(m_form, MENU|OK);
    }
    public void cmdPlay(){
        if (MainMIDlet.DEBUG) System.err.println("cmdPlay()");
        m_engine = new Engine();
        setView(m_engine, MENU);
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
        m_activeForm = FORM_LIST_LANGS;
        setView(m_form, MENU);
    }
    public void cmdAbout(){
        m_form = new Form(m_lang.m_dic[Lang.ABOUT]);
        m_form.append(m_lang.m_dic[Lang.ABOUT_DESC]+"\n");
        m_form.append("http://komkom.pl/mobiKAR/");
        m_form.append(new StringItem(m_lang.m_dic[Lang.VERSION]," "+getAppProperty("MIDlet-Version")));
        m_form.append(new StringItem(m_lang.m_dic[Lang.CREATORS], " Micha³ Kud³a, Bart³omiej Budnik"));
        m_activeForm = FORM_LIST_ABOUT;
        Platform platform = new Platform(this);
        if (platform.isSupported()){
            setView(m_form, MENU|GOTO);
        }
        else{
            setView(m_form, MENU);
        }
    }
    public void cmdAdv(int no){
        String name = "KomKom";
        String text = "KomKom to komórkowy komunikator.\n"
        +"Dziêki niemu i telefonowi mo¿esz byæ stale w kontakcie z przyció³mi, "
        +" którzy korzystaj¹ z przeró¿nych komunikatorów tkj. WPKontakt, Gadu-Gadu, Tlen\n"
        +"Wiêcej informacji na stronie domowej KomKom.PL\n";
        String link = "http://komkom.pl";
        m_form = new Form(name);
        m_form.append(text);
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
        }
        else{
            setView(m_form, MENU);
        }
    }
    public void cmdDownload(){
        m_form = new Form(m_lang.m_dic[Lang.DOWNLOAD]);
        m_form.append(m_lang.m_dic[Lang.DOWNLOAD_DESC]);
        m_form.append(new TextField("URL:", "http://", 255, TextField.ANY));
        m_activeForm = FORM_LIST_DOWNLOAD;
        setView(m_form, MENU|OK);
    }
    public void cmdDelete(){
        m_form = new Form(m_lang.m_dic[Lang.DELETE]);
        m_form.append(m_lang.m_dic[Lang.DELETE_DESC]);
        m_activeForm = FORM_LIST_DELETE;
        setView(m_form, MENU|OK);
    }
    
    
}
