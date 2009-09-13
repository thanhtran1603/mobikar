/*
 * Engine.java
 *
 * Created on 26 wrzesieñ 2004, 23:00
 */
import javax.microedition.lcdui.*;
import java.io.*;
import java.util.*;
/**
 *
 * @author  MiKO
 */
public class Engine extends Canvas implements Runnable{
    
    final byte LOADING = 0;
    final byte LOADED = 1;
    final byte SING = 2;
    final byte SINGING = 3;
    final byte STOP = 4;
    final byte STOPED = 5;
    
    final byte IDX_START = 0;
    final byte IDX_END = 1;
    final byte IDX_LINE = 2;
    final byte IDX_POS = 3; //16bit: start w px; 8bit: idx starowy w lini; 8bit: idx koncowy w lini
    
    byte m_state = LOADING;
    private boolean m_stop;
    Song m_song = null;
    MidiPlayer m_player = null;
    
    
    
    String[] m_lyric = null;
    int[][] m_times = null;
    int m_idxLine;
    int m_idxLineLast;
    int m_idxTime;
    int m_idxTimeLast;
    int m_arcSyllable = 0;
    int m_arcSong = 0;
    
    byte m_border = 1;
    
    long m_timeStart;
    int m_lineSpacing = MainMIDlet.m_instance.m_store.getInteger(Store.INT_INTERLINE);
    int m_before = MainMIDlet.m_instance.m_store.getInteger(Store.INT_PREVIEW);
    int m_background = MainMIDlet.m_instance.m_store.getInteger(Store.INT_BACKGROUND);
    int m_lineHistory = MainMIDlet.m_instance.m_store.getInteger(Store.INT_LINEHISTORY);
    int m_lineBuffor = MainMIDlet.m_instance.m_store.getInteger(Store.INT_LINEBUFFOR);
    int m_lineBase = MainMIDlet.m_instance.m_store.getInteger(Store.INT_LINEBASE);
    int m_volume = MainMIDlet.m_instance.m_store.getInteger(Store.INT_VOLUME);
    
    boolean m_mustRepaint = true;
    
    Image m_imgBackgroung;
    Image m_imgPanel;
    Image m_imgBan;
    Image m_imgAdv;
    
    int m_introPos = Integer.MAX_VALUE;
    
    /** Creates a new instance of Engine */
    public Engine() {
        m_stop = false;
        m_song = MainMIDlet.m_instance.m_store.getActualSong();
        m_lyric = new String[]{
            "Autor Zas³u¿ony",
            "Pieœñ ludowa",
        };
        m_times = new int[][]{
            {0, 1000, 0, 0<<16 | 0<<8 | 5},
            {0, 1000, 1, 0<<16 | 0<<8 | 5},
        };
        m_idxLine = 0;
        m_idxLineLast = -1;
        m_idxTime = 0;
        m_idxTimeLast = -1;
        try{
            m_imgBackgroung = Image.createImage("/bkg.png");
            m_imgPanel = Image.createImage("/pan.png");
            m_imgBan = Image.createImage("/ban.png");
            m_imgAdv = Image.createImage("/adv.png");
            
        } catch (Exception e){
            e.printStackTrace();
        }
        new Thread(this).start();
    }
    public void destroy(){
        m_stop = true;
        stop();
    }
    public void cmdSing(){
        m_state = SING;
    }
    public void cmdStop(){
        m_state = STOP;
    }
    protected void keyPressed(int keyCode){
        int gameAction = getGameAction(keyCode);
        if (MainMIDlet.DEBUG) System.err.println("Engine.keyPressed("+keyCode+") getGameAction(("+keyCode+"):"+gameAction);
        if (gameAction == UP){
            m_volume = m_player.setVolume(m_player.getVolume()+10);
            MainMIDlet.m_instance.m_store.setInteger(Store.INT_VOLUME, m_volume);
            if (MainMIDlet.DEBUG) System.err.println("Engine.keyPressed() m_volume:"+m_volume);
        }
        else if (gameAction == DOWN){
            m_volume = m_player.setVolume(m_player.getVolume()-10);
            MainMIDlet.m_instance.m_store.setInteger(Store.INT_VOLUME, m_volume);
            if (MainMIDlet.DEBUG) System.err.println("Engine.keyPressed() m_volume:"+m_volume);
        }
        
    }
    public void paint(Graphics g){
        int w = g.getClipWidth();
        int h = g.getClipHeight();
        int foreground = MainMIDlet.m_instance.m_store.getInteger(Store.INT_COLOR_FOREGROUND);
        int background = MainMIDlet.m_instance.m_store.getInteger(Store.INT_COLOR_BACKGROUND);
        int active = MainMIDlet.m_instance.m_store.getInteger(Store.INT_COLOR_ACTIVE);
        int after = MainMIDlet.m_instance.m_store.getInteger(Store.INT_COLOR_READ);
        int outline = MainMIDlet.m_instance.m_store.getInteger(Store.INT_COLOR_OUTLINE);
        int shadow = MainMIDlet.m_instance.m_store.getInteger(Store.INT_COLOR_SHADOW);
        int fontFlags = MainMIDlet.m_instance.m_store.getInteger(Store.INT_FONT);
        int face = 0;
        if ((fontFlags & Font.FACE_MONOSPACE) == Font.FACE_MONOSPACE)
            face = Font.FACE_MONOSPACE;
        else if ((fontFlags & Font.FACE_PROPORTIONAL) == Font.FACE_PROPORTIONAL)
            face = Font.FACE_PROPORTIONAL;
        int style = 0;
        if ((fontFlags & Font.STYLE_BOLD) == Font.STYLE_BOLD)
            style |= Font.STYLE_BOLD;
        if ((fontFlags & Font.STYLE_ITALIC) == Font.STYLE_ITALIC)
            style |= Font.STYLE_ITALIC;
        if ((fontFlags & Font.STYLE_UNDERLINED) == Font.STYLE_UNDERLINED)
            style |= Font.STYLE_UNDERLINED;
        int size = 0;
        if ((fontFlags & Font.SIZE_LARGE) == Font.SIZE_LARGE)
            size = Font.SIZE_LARGE;
        else if ((fontFlags & Font.SIZE_SMALL) == Font.SIZE_SMALL)
            size = Font.SIZE_SMALL;
        
        Font f = Font.getFont(face, style, size);
        g.setFont(f);
        
        if (m_state != SINGING){
            if (m_introPos == 0)
                return;
            if (m_imgBackgroung  != null && ((m_background & MainMIDlet.FLAG_BACKGROUND) == MainMIDlet.FLAG_BACKGROUND)){
                if (m_lineBase < 0)
                    g.drawImage(m_imgBackgroung , 0,0, g.TOP|g.LEFT);
                else
                    g.drawImage(m_imgBackgroung , 0,h, g.BOTTOM|g.LEFT);
            }
            else{
                g.setColor(background);
                g.fillRect(0, 0, w, h);
            }
            if (m_introPos == Integer.MAX_VALUE){
                m_introPos = h;
            }
            g.setColor(foreground);
            g.drawString(m_song.getName(), 1, m_introPos + 2, g.TOP|g.LEFT);
            if (m_imgAdv  != null && ((m_background & MainMIDlet.FLAG_ADVERTISMENT) == MainMIDlet.FLAG_ADVERTISMENT)){
                g.drawImage(m_imgAdv, 1, m_introPos +f.getHeight() + 4 , g.TOP|g.LEFT);
            }
            m_introPos-= 10;
            if (m_introPos < 0)
                m_introPos = 0;
        }
        else{
            
            int lineBase = h - m_lineBase - (f.getHeight() + m_lineSpacing) * m_lineBuffor;
            if (m_lineBase < 0){
                lineBase = -m_lineBase + (f.getHeight() + m_lineSpacing) * (m_lineHistory+1);
            }
            
            if (m_idxLineLast != m_idxLine || m_mustRepaint){
                // malujemy t³o
                if (m_imgBackgroung  != null && ((m_background & MainMIDlet.FLAG_BACKGROUND) == MainMIDlet.FLAG_BACKGROUND)){
                    if (m_lineBase < 0)
                        g.drawImage(m_imgBackgroung , 0,0, g.TOP|g.LEFT);
                    else
                        g.drawImage(m_imgBackgroung , 0,h, g.BOTTOM|g.LEFT);
                }
                else{
                    g.setColor(background);
                    g.fillRect(0, 0, w, h);
                }
                m_idxLineLast = m_idxLine;
            }
            if (m_imgPanel  != null && ((m_background & MainMIDlet.FLAG_PANEL) == MainMIDlet.FLAG_PANEL)){
                //malujemy panela
                int panel_y = 0;
                final int panel_h = 12;
                if (m_lineBase < 0){
                    lineBase += panel_h;
                }
                else{
                    lineBase -= panel_h;
                    panel_y = h-panel_h;
                }
                g.drawImage(m_imgPanel , 0, panel_y, g.TOP|g.LEFT);
                g.setColor(0xffffff);
                g.fillArc(9, panel_y+1, 10, 10, 90, m_arcSong);
                g.setColor(0xff0000);
                g.fillArc(21, panel_y+1, 10, 10, 90, m_arcSyllable);
                // volume
                g.setColor(0x000000);
                g.fillRect(1, panel_y+1, 6, (100 - m_volume) / 10);
                if (m_imgBan  != null && ((m_background & MainMIDlet.FLAG_ADVERTISMENT) == MainMIDlet.FLAG_ADVERTISMENT)){
                    g.drawImage(m_imgBan, 32, panel_y, g.TOP+g.LEFT);
                }
            }
            
            if (!(m_idxTime != -1 || m_mustRepaint || m_idxTime != m_idxTimeLast))
                return;
            
            int y = lineBase;
            // malowanie aktywnej lini
            drawString(g, after,  m_lyric[m_idxLine], m_border, y, g.LEFT | g.BASELINE);
            g.setColor(foreground);
            // nadmalowywanie starego tekstu
            // m_idxTimeLast jest równe m_idxTime jeœli m_idxTime nie jest równe -1
            int pos = 0;
            if (m_idxTimeLast != -1)
                pos = m_times[m_idxTimeLast][IDX_POS];
            int offset = (pos & 0xFF00) >> 8;
            int len = m_lyric[m_idxLine].length() - offset;
            int x = (pos >> 16) + m_border;
            //if (MainMIDlet.DEBUG) System.err.println("Paint len:"+len+", offset:"+offset+"["+m_idxTimeLast+"|"+m_idxTime+"]");
            if (len > 0){
                g.drawSubstring(m_lyric[m_idxLine], offset, len, x, y, g.LEFT | g.BASELINE);
            }
            // linie nowe
            for (int linia = m_idxLine+1; linia <  m_lyric.length && ((linia - m_idxLine) <= m_lineBuffor); linia++){
                drawString(g, foreground,  m_lyric[linia], m_border, lineBase+(linia - m_idxLine)*(f.getHeight() + m_lineSpacing), g.LEFT | g.BASELINE);
            }
            int y1 = y;
            g.setColor(after);
            for (int linia = m_idxLine-1; linia >= 0 && y1 > 0 && ((m_idxLine - linia -1) < m_lineHistory); linia--){
                y1 -= f.getHeight() + m_lineSpacing;
                drawString(g, after,  m_lyric[linia], m_border, y1, g.LEFT | g.BASELINE);
            }
            if (m_idxTime != -1){
                len = pos & 0xFF;
                g.setColor(active);
                g.drawSubstring(m_lyric[m_idxLine], offset, len, x, y, g.LEFT | g.BASELINE);
                //if (MainMIDlet.DEBUG) System.err.println("Paint "+m_lyric[m_idxLine]+", "+offset+", "+len+", "+x);
            }
        }
        m_mustRepaint = false;
    }
    private void drawString(Graphics g, int color, String str, int x, int y, int flags){
        int outline = MainMIDlet.m_instance.m_store.getInteger(Store.INT_COLOR_OUTLINE);
        int shadow = MainMIDlet.m_instance.m_store.getInteger(Store.INT_COLOR_SHADOW);
        int fontFlags = MainMIDlet.m_instance.m_store.getInteger(Store.INT_FONT);
        if ((fontFlags & MainMIDlet.FLAG_FONT_SHADOW) == MainMIDlet.FLAG_FONT_SHADOW){
            g.setColor(shadow);
            g.drawString(str, x+2, y+2, flags);
        }
        if ((fontFlags & MainMIDlet.FLAG_FONT_OUTLINE) == MainMIDlet.FLAG_FONT_OUTLINE){
            g.setColor(outline);
            g.drawString(str, x-1, y-1, flags);
            g.drawString(str, x-1, y+1, flags);
            g.drawString(str, x-1, y+1, flags);
            g.drawString(str, x+1, y+1, flags);
        }
        g.setColor(color);
        g.drawString(str, x, y, flags);
    }
    public void run() {
        // TODO: to powinno byc w osobnym watku
        calculateLyric();
        while (! m_stop){
            try{
                Thread.sleep(50);
                try{
                    switch(m_state){
                        case SING:
                            sing();
                            break;
                        case STOP:
                            stop();
                            break;
                        case SINGING:{
                            long currentTimeMillis = System.currentTimeMillis();
                            int linia = m_idxLine;
                            long time = currentTimeMillis - m_timeStart + m_before;
                            //if (MainMIDlet.DEBUG) System.err.println("getMediaTime():"+m_player.getMediaTime()+" : "+time);
                            int idxTimeNext = -1;
                            int idxTime = m_idxTime;
                            m_idxTime = -1;
                            if (idxTime == -1)
                                idxTime = 0;
                            while(idxTime < m_times.length){
                                //if (MainMIDlet.DEBUG) System.err.println("m_times["+idxTime+"][IDX_END]:"+m_times[idxTime][IDX_END]+" < ["+time+"] < "+m_times[idxTime][IDX_START]);
                                if (m_times[idxTime][IDX_START] > time){
                                    idxTimeNext = idxTime;
                                    break;
                                }
                                if (m_times[idxTime][IDX_START] <= time && time <= m_times[idxTime][IDX_END]){
                                    m_idxTime = idxTime;
                                    m_idxLine = m_times[idxTime][IDX_LINE];
                                    // sparwdzako
                                    int pos = m_times[m_idxTime][IDX_POS];
                                    int offset = (pos & 0xFF00) >> 8;
                                    int len = pos & 0xFF;
                                    int x = (pos >> 16) + m_border;
                                    //if (MainMIDlet.DEBUG) System.err.println(m_lyric[m_idxLine]+", "+offset+", "+len+", "+x);
                                    m_idxTimeLast = m_idxTime;
                                    if (m_idxTime+1 < m_times.length)
                                        idxTimeNext = idxTime + 1;
                                    break;
                                }
                                idxTime++;
                            }
                            //if (MainMIDlet.DEBUG) System.err.println("m_idxTime:"+m_idxTime);
                            m_arcSong = (int)(360 - (360 * (currentTimeMillis - m_timeStart)) / m_song.getDuration());
                            m_arcSyllable = 0;
                            if (idxTimeNext >= 0){
                                int okres;
                                if (m_idxTime == -1){
                                    if (m_idxTimeLast != -1 ){
                                        okres = m_times[idxTimeNext][IDX_START] - m_times[m_idxTimeLast][IDX_START];
                                    }
                                    else{
                                        okres = m_times[idxTimeNext][IDX_START];
                                    }
                                }
                                else{
                                    okres = m_times[idxTimeNext][IDX_START] - m_times[m_idxTime][IDX_START];
                                }
                                m_arcSyllable = (int)((360 * (m_times[idxTimeNext][IDX_START] - time)) / okres);
                                if (MainMIDlet.DEBUG) System.err.println(time+" "+okres+" "+m_times[idxTimeNext][IDX_START]+ " " +m_arcSyllable );
                            }
                            repaint();
                            break;
                        }
                        default:
                            repaint();
                            break;
                    }
                } catch (Exception e){
                    if (MainMIDlet.DEBUG) System.err.println(e.toString());
                }
                repaint();
            } catch (Exception e){
                if (MainMIDlet.DEBUG) System.err.println(e.toString());
            }
        }
        MainMIDlet.m_instance.startUI();
    }
    public void stop(){
        if (MainMIDlet.DEBUG) System.err.println("Engine.stop()");
        if (m_player != null && m_player.stop()){
            MainMIDlet.m_instance.setView(this, MainMIDlet.MENU|MainMIDlet.SING);
            m_player = null;
        }
        m_mustRepaint = true;
        m_introPos = Integer.MAX_VALUE;
        m_state = STOPED;
    }
    public void sing(){
        if (MainMIDlet.DEBUG) System.err.println("Engine.sing()");
        m_timeStart = System.currentTimeMillis();
        launchPlayer();
        m_state = SINGING;
        MainMIDlet.m_instance.setView(this, MainMIDlet.MENU|MainMIDlet.STOP);
        // to dla motki
        m_idxLine = 0;
        m_idxLineLast = -1;
        m_idxTime = 0;
        m_idxTimeLast = -1;
        m_mustRepaint = true;
    }
    void launchPlayer(){
        stop();
        m_player = new MidiPlayer(this, MainMIDlet.m_instance.m_store.getActualSong().getMIDIStream());
        m_volume = m_player.setVolume(m_volume);
        m_player.start();
        m_state = LOADED;
    }
    void calculateLyric(){
        int maxSylables = m_song.getSyllables().length;
        int fontFlags = MainMIDlet.m_instance.m_store.getInteger(Store.INT_FONT);
        int face = 0;
        if ((fontFlags & Font.FACE_MONOSPACE) == Font.FACE_MONOSPACE)
            face = Font.FACE_MONOSPACE;
        else if ((fontFlags & Font.FACE_PROPORTIONAL) == Font.FACE_PROPORTIONAL)
            face = Font.FACE_PROPORTIONAL;
        int style = 0;
        if ((fontFlags & Font.STYLE_BOLD) == Font.STYLE_BOLD)
            style |= Font.STYLE_BOLD;
        if ((fontFlags & Font.STYLE_ITALIC) == Font.STYLE_ITALIC)
            style |= Font.STYLE_ITALIC;
        if ((fontFlags & Font.STYLE_UNDERLINED) == Font.STYLE_UNDERLINED)
            style |= Font.STYLE_UNDERLINED;
        int size = 0;
        if ((fontFlags & Font.SIZE_LARGE) == Font.SIZE_LARGE)
            size = Font.SIZE_LARGE;
        else if ((fontFlags & Font.SIZE_SMALL) == Font.SIZE_SMALL)
            size = Font.SIZE_SMALL;
        Font f = Font.getFont(face, style, size);
        int[][] times = new int[maxSylables][4];
        Vector lyrics = new Vector();
        int lineWidth = getWidth() - 2 * m_border;
        String line = m_song.getSyllables()[0];
        times[0][IDX_START] = m_song.getTextTimes()[0];
        times[0][IDX_END] = times[0][IDX_START] + 500;
        times[0][IDX_LINE] = 0;
        times[0][IDX_POS] = 0<<16 | 0<<8 | line.length();
        int idxTimes = 1;
        int idxSong = 1;
        while (idxSong < times.length){
            if (m_song.getTextTimes()[idxSong] != times[idxTimes-1][IDX_START]){
                if (f.stringWidth(line + m_song.getSyllables()[idxSong]) > lineWidth){
                    lyrics.addElement(line);
                    line = m_song.getSyllables()[idxSong];
                    times[idxTimes][IDX_LINE] = lyrics.size();
                    times[idxTimes][IDX_POS] = 0<<16 | 0<<8 | line.length();
                }
                else{
                    int s = f.stringWidth(line)<<16;
                    times[idxTimes][IDX_POS] = (f.stringWidth(line)<<16) | (line.length()<<8) | m_song.getSyllables()[idxSong].length();
                    s = times[idxTimes][IDX_POS];
                    line += m_song.getSyllables()[idxSong];
                    times[idxTimes][IDX_LINE] = lyrics.size();
                }
                times[idxTimes][IDX_START] = m_song.getTextTimes()[idxSong];
                times[idxTimes][IDX_END] = times[idxTimes][IDX_START] + 500;
                times[idxTimes-1][IDX_END] = times[idxTimes][IDX_START];
                // sparwdzako
                int pos = times[idxTimes][IDX_POS];
                int offset = (pos & 0xFF00) >> 8;
                int len = pos & 0xFF;
                int x = (pos >> 16) + m_border;
                if (MainMIDlet.DEBUG) System.out.println(times[idxTimes][IDX_START]+":"+times[idxTimes][IDX_END]+" "+x+","+offset+","+len+" "+line);
                idxTimes++;
            }
            idxSong++;
        }
        if (line != null)
            lyrics.addElement(line);
        String[] lyric = new String[lyrics.size()];
        for (int i=0; i<lyric.length; i++){
            lyric[i] = (String)lyrics.elementAt(i);
        }
        synchronized(this){
            m_lyric =lyric;
            m_times = times;
        }
        m_idxLineLast = -1;
        m_mustRepaint = true;
        MainMIDlet.m_instance.setView(this, MainMIDlet.MENU|MainMIDlet.SING);
    }
    
    public void playerEvent(byte event){
        if (event == MidiPlayer.EVENT_STARTED){
            m_timeStart = System.currentTimeMillis();
            if (MainMIDlet.DEBUG) System.out.println("playerEvent started");
        }
        else if (event == MidiPlayer.EVENT_STOPED){
            if (MainMIDlet.DEBUG) System.out.println("playerEvent stoped");
            MainMIDlet.m_instance.setView(this, MainMIDlet.MENU|MainMIDlet.SING);
        }
        m_mustRepaint = true;
    }
}
