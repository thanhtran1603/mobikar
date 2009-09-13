/*
 * Engine.java
 *
 * Created on 26 wrzesieñ 2004, 23:00
 */
import javax.microedition.lcdui.*;
import java.io.*;
import java.util.*;

// todo: linia aktywan - tam gdzie jest spiewane - poprawic

/**
 *
 * @author  MiKO
 */
public class Engine extends NativeScreen implements Runnable{
    final byte LOADING = 1;
    final byte CALCULATING = 2;
    final byte LOADED = 3;
    final byte SING = 4;
    final byte SINGING = 5;
    final byte STOP = 6;
    final byte STOPED = 7;
    
    final byte IDX_START = 0;
    final byte IDX_END = 1;
    final byte IDX_LINE = 2;
    final byte IDX_POS = 3; //16bit: start w px; 8bit: idx starowy w lini; 8bit: idx koncowy w lini
    
    byte m_state = LOADING;
    private boolean m_stop;
    Song m_song = null;
    MKPlayer m_player = null;
    
    String[] m_lyric = null;
    int[][] m_times = null;
    int m_idxLine;
    int m_idxLineLast;
    int m_idxTime;
    int m_idxTimeLast;
    int m_arcSyllable = 0;
    int m_arcSong = 0;
    
    int m_width = MainMIDlet.m_width;
    int m_height = MainMIDlet.m_height;
    
    byte m_border = 1;
    
    final int m_lineSpacing = MainMIDlet.m_instance.m_store.getInteger(Store.INT_INTERLINE);
    final int m_before = MainMIDlet.m_instance.m_store.getInteger(Store.INT_PREVIEW);
    final int m_background = MainMIDlet.m_instance.m_store.getInteger(Store.INT_BACKGROUND);
    final int m_lineHistory = MainMIDlet.m_instance.m_store.getInteger(Store.INT_LINEHISTORY);
    final int m_lineBuffor = MainMIDlet.m_instance.m_store.getInteger(Store.INT_LINEBUFFOR);
    final int m_lineBase = MainMIDlet.m_instance.m_store.getInteger(Store.INT_LINEBASE);
    final int m_colNew0 = MainMIDlet.m_instance.m_store.getInteger(Store.INT_COLOR_NEW0);
    final int m_colNew1 = MainMIDlet.m_instance.m_store.getInteger(Store.INT_COLOR_NEW1);
    final int m_colBackground = MainMIDlet.m_instance.m_store.getInteger(Store.INT_COLOR_BACKGROUND);
    final int m_colActive = MainMIDlet.m_instance.m_store.getInteger(Store.INT_COLOR_ACTIVE);
    final int m_colOld = MainMIDlet.m_instance.m_store.getInteger(Store.INT_COLOR_OLD);
    final int m_colOutline = MainMIDlet.m_instance.m_store.getInteger(Store.INT_COLOR_OUTLINE);
    final int m_colShadow = MainMIDlet.m_instance.m_store.getInteger(Store.INT_COLOR_SHADOW);
    int m_volume = MainMIDlet.m_instance.m_store.getInteger(Store.INT_VOLUME);
    
    // pozycja od której zaczyna siê obszar z tekstem
    int m_panLyrTop = 0;
    int m_panLyrLeft = m_border;
    int m_panLyrWidth = m_width - 2*m_border + 1;
    int m_panAdvHeight = 12;
    
    // przesuniecie tytu³u piosenki
    long m_timeTitle = 0;
    int m_banerPosY = 0;
    
    boolean m_isFreshScreen = false;
    boolean m_isPainting = false;
    
    Image m_imgBackgroung;
    Image m_imgPanel;
    Image m_imgBan;
    Image m_imgAdv;
    
    int m_introPos = Integer.MAX_VALUE;
    
    String m_lyrText;
    long[] m_lyrData;
    
    // dane ekranu tekstowego ;)
    // zawieraj¹ dane int przepo³owione na pozycjê na ekranie i index do danych
    // m_screenData[a][b] = pozycja_X << 16 | idx_do_danych;
    int[][] m_screenData = null;
    int m_screenRowActive = -1;
    // indeks atywnej sylaby
    int m_idxSylActive = -1;
    // indeks pierwszej niestarej(nieodœpiewanej) sylaby
    // jesli m_idxSylActive >= 0 to m_idxSylActive == m_idxSylNotOld
    int m_idxSylNotOld = -1;
    // indekst pierszej sylaby z nastepnego rzedu
    int m_idxDataLastSylInRow = Integer.MAX_VALUE;
    // kontrolka zmian sylab
    boolean m_isChangedSyl = false;
    // co ile ma przerysowywaæ?
    final int m_refresh = 30;
    
    Font m_font = null;
    int m_fontFlags = 0;
    
    String m_debugString = null;
    
    // Work on OffScereen
    Image m_offscreen = null;
    
    
    /** Creates a new instance of Engine */
    public Engine() {
        if (MainMIDlet.DEBUG) System.err.println("Engine()");
        m_offscreen = Image.createImage(MainMIDlet.m_width, MainMIDlet.m_height);
        MainMIDlet.m_instance.setView(this, MainMIDlet.MENU);
        MainMIDlet.check_Point(AlertType.INFO);
        m_stop = false;
        m_state = LOADING;
        m_isFreshScreen = false;
        m_isPainting = false;
        new Thread(this).start();
        // Ustawienie czcionki
        try{
            m_imgBackgroung = Image.createImage(MainMIDlet.m_width, MainMIDlet.m_height);
            Graphics g = m_imgBackgroung.getGraphics();
            g.setColor(MainMIDlet.m_instance.m_store.getInteger(Store.INT_COLOR_BACKGROUND));
            g.fillRect(0, 0, MainMIDlet.m_width, MainMIDlet.m_height);
            if ((m_background & MainMIDlet.FLAG_GRADIENT) == MainMIDlet.FLAG_GRADIENT){
                int sample_w = MainMIDlet.m_width;
                int sample_h = MainMIDlet.m_height;
                int sample_x = 0;
                int sample_y = 0;
                int color0 = MainMIDlet.m_instance.m_store.getInteger(Store.INT_COLOR_GRADIENT0);
                int color9 = MainMIDlet.m_instance.m_store.getInteger(Store.INT_COLOR_GRADIENT9);
                int red0 = (color0 >> 16) & 0xff;
                int green0 = (color0 >> 8) & 0xff;
                int blue0 = (color0) & 0xff;
                int red9 = (color9 >> 16) & 0xff;
                int green9 = (color9 >> 8) & 0xff;
                int blue9 = (color9) & 0xff;
                final int prec = 16;
                long deltaRed = ((red9 - red0) << prec) / (sample_h);
                long deltaGreen = ((green9 - green0) << prec) / (sample_h);
                long deltaBlue = ((blue9 - blue0) << prec) / (sample_h);
                for (int i=0; i<sample_h; i++){
                    int iRed = (int)(red0+((i*deltaRed)>>prec));
                    int iGreen = (int)(green0+((i*deltaGreen)>>prec));
                    int iBlue = (int)(blue0+((i*deltaBlue)>>prec));
                    if (MainMIDlet.DEBUG) System.err.println(" "+iRed+" "+iGreen+" "+iBlue+" ");
                    g.setColor(iRed>255?255:(iRed<0?0:iRed), iGreen>255?255:(iGreen<0?0:iGreen), iBlue>255?255:(iBlue<0?0:iBlue));
                    g.drawLine(sample_x, (int)sample_y+i, sample_x+sample_w, sample_y+i);
                }
            }
            if ((m_background & MainMIDlet.FLAG_BACKGROUND) == MainMIDlet.FLAG_BACKGROUND){
                g.drawImage(Image.createImage("/bkg.png"), MainMIDlet.m_width >> 1, MainMIDlet.m_height >> 1, g.HCENTER|g.VCENTER);
            }
            if ((m_background & MainMIDlet.FLAG_PANEL) == MainMIDlet.FLAG_PANEL)
                m_imgPanel = Image.createImage("/pan.png");
            if ((m_background & MainMIDlet.FLAG_ADVERTISMENT) == MainMIDlet.FLAG_ADVERTISMENT){
                m_imgBan = Image.createImage("/ban.png");
                m_imgAdv = Image.createImage("/adv.png");
            }
            
        } catch (Exception e){
            if (MainMIDlet.DEBUG) e.printStackTrace();
        }
        m_fontFlags = MainMIDlet.m_instance.m_store.getInteger(Store.INT_FONT);
        int face = 0;
        if ((m_fontFlags & Font.FACE_MONOSPACE) == Font.FACE_MONOSPACE)
            face = Font.FACE_MONOSPACE;
        else if ((m_fontFlags & Font.FACE_PROPORTIONAL) == Font.FACE_PROPORTIONAL)
            face = Font.FACE_PROPORTIONAL;
        int style = 0;
        if ((m_fontFlags & Font.STYLE_BOLD) == Font.STYLE_BOLD)
            style |= Font.STYLE_BOLD;
        if ((m_fontFlags & Font.STYLE_ITALIC) == Font.STYLE_ITALIC)
            style |= Font.STYLE_ITALIC;
        if ((m_fontFlags & Font.STYLE_UNDERLINED) == Font.STYLE_UNDERLINED)
            style |= Font.STYLE_UNDERLINED;
        int size = 0;
        if ((m_fontFlags & Font.SIZE_LARGE) == Font.SIZE_LARGE)
            size = Font.SIZE_LARGE;
        else if ((m_fontFlags & Font.SIZE_SMALL) == Font.SIZE_SMALL)
            size = Font.SIZE_SMALL;
        
        m_font = Font.getFont(face, style, size);
        // row_counts - liczba wierszy
        int row_counts = m_lineBuffor + 1 + m_lineHistory;
        
        // wyliczenie pozycji Top od której zaczyna siê tekst
        m_panLyrTop = m_height - m_lineBase - (m_font.getHeight() + m_lineSpacing) * row_counts;
        if (m_lineBase < 0){
            m_panLyrTop = -m_lineBase + m_lineSpacing;
        }
        // jeœli jest aktywny panel to uwzglêdniamy go
        if (m_imgPanel  != null ){
            if (m_lineBase < 0){
                m_panLyrTop += m_panAdvHeight;
            } else{
                m_panLyrTop -= m_panAdvHeight;
            }
        }
        
        
        // stworzenie tablicy tekstów
        m_screenData = new int[row_counts][];
        m_song = MainMIDlet.m_instance.m_store.getSongActual();
        m_timeTitle = System.currentTimeMillis();
        m_state = CALCULATING;
        m_isFreshScreen = false;
        m_lyrText = m_song.m_mlyrText;
        m_lyrData = m_song.m_mlyrData;
        // screen data
        m_screenRowActive = MainMIDlet.m_instance.m_store.getInteger(Store.INT_LINEHISTORY);
        // pocz¹tek piosenki
        m_idxSylNotOld = 0;
        scrollScereenData();
        MainMIDlet.m_instance.setView(this, MainMIDlet.MENU|MainMIDlet.SING);
        
    }
    public void destroy(){
        m_stop = true;
        stop();
    }
    public void cmdSing(){
        m_state = SING;
        m_isFreshScreen = false;
    }
    public void cmdStop(){
        m_state = STOP;
    }
    protected void keyPressed(int keyCode){
        int gameAction = getGameAction(keyCode);
        if (MainMIDlet.DEBUG) System.err.println("Engine.keyPressed("+keyCode+") getGameAction(("+keyCode+"):"+gameAction);
        if (gameAction == UP){
            if (m_player != null){
                m_volume = m_player.setVolume(m_player.getVolume()+10);
                MainMIDlet.m_instance.m_store.setInteger(Store.INT_VOLUME, m_volume);
                if (MainMIDlet.DEBUG) System.err.println("Engine.keyPressed() m_volume:"+m_volume);
            }
        } else if (gameAction == DOWN){
            if (m_player != null){
                m_volume = m_player.setVolume(m_player.getVolume()-10);
                MainMIDlet.m_instance.m_store.setInteger(Store.INT_VOLUME, m_volume);
                if (MainMIDlet.DEBUG) System.err.println("Engine.keyPressed() m_volume:"+m_volume);
            }
        }
        
    }
    private void update(){
        if (m_isPainting)
            return;
        m_isPainting = true;
        
        Graphics g = m_offscreen.getGraphics();
        
        boolean isFreshScreen = m_isFreshScreen;
        switch (m_state){
            case LOADING :{
                if ( ! isFreshScreen){
                    if (MainMIDlet.DEBUG) System.err.println("paint() LOADING");
                    g.setColor(m_colBackground);
                    g.fillRect(0, 0, m_width, m_height);
                }
                break;
            }
            case CALCULATING: {
                if ( ! isFreshScreen){
                    //g.setClip(0, 0, m_width, m_height);
                    //if (MainMIDlet.DEBUG) System.err.println("Clip:"+g.getClipX()+", "+g.getClipY()+", "+g.getClipWidth()+", "+g.getClipHeight());
                    drawBackgroung(g);
                }
                long period = System.currentTimeMillis() - m_timeTitle;
                if (period < 1000){
                    // wyswietlenie tytu³u
                    if ( ! isFreshScreen){
                        g.setFont(m_font);
                        //g.setClip(m_panLyrLeft, m_panLyrTop, m_panLyrWidth, m_font.getHeight());
                        //if (MainMIDlet.DEBUG) System.err.println("Clip:"+g.getClipX()+", "+g.getClipY()+", "+g.getClipWidth()+", "+g.getClipHeight());
                        String text = m_song.getName();
                        drawString(g, m_colNew0, text, m_panLyrLeft, m_panLyrTop, g.LEFT|g.TOP);
                        //g.setClip(0, 0, m_width, m_height);
                        //if (MainMIDlet.DEBUG) System.err.println("Clip:"+g.getClipX()+", "+g.getClipY()+", "+g.getClipWidth()+", "+g.getClipHeight());
                    }
                } else if (period < 2500){
                    // jeœli jest baner to niech sobie spadnie
                    if ((m_background & MainMIDlet.FLAG_ADVERTISMENT) == MainMIDlet.FLAG_ADVERTISMENT){
                        int fullHigh = m_font.getHeight() + m_imgAdv.getHeight();
                        int pos_y = m_panLyrTop + (int)(fullHigh * (period - 1000)) / 1000;
                        if (pos_y > m_panLyrTop + fullHigh)
                            pos_y = m_panLyrTop + fullHigh;
                        //                        g.setClip(0, 0, m_width, fullHigh);
                        drawBackgroung(g);
                        // malujemy tytu³
                        g.setFont(m_font);
                        String text = m_song.getName();
                        drawString(g, m_colNew0, text, m_panLyrLeft, m_panLyrTop, g.LEFT|g.TOP);
                        // malujemy baner
                        g.drawImage(m_imgAdv, 0, pos_y, g.LEFT|g.BOTTOM);
                    }
                } else{
                    // todo: nie staruje w³asciwei tekst w scrolu
                    int clipX = g.getClipX();
                    int clipY = g.getClipY();
                    int clipW = g.getClipWidth();
                    int clipH = g.getClipHeight();
                    g.setClip(m_panLyrLeft, m_panLyrTop, m_panLyrWidth, m_font.getHeight());
                    //if (MainMIDlet.DEBUG) System.err.println("Clip:"+g.getClipX()+", "+g.getClipY()+", "+g.getClipWidth()+", "+g.getClipHeight());
                    drawBackgroung(g);
                    g.translate(m_panLyrLeft, m_panLyrTop);
                    g.setFont(m_font);
                    String text = m_song.getName();
                    int textWidth = m_font.stringWidth(text);
                    int spacing = 50;
                    int titlePosX = -((int)((period - 2500)/50)%(textWidth+spacing));
                    drawString(g, m_colNew0, text, titlePosX, 0, g.LEFT|g.TOP);
                    if ((titlePosX + textWidth) < m_panLyrWidth){
                        drawString(g, m_colNew0, text, titlePosX + textWidth + spacing, 0, g.LEFT|g.TOP);
                    }
                    g.translate(-m_panLyrLeft, -m_panLyrTop);
                    g.setClip(clipX, clipY, clipW, clipH);
                }
                break;
            }
            case SINGING:{
                boolean isDrawed = false;
                if ( ! isFreshScreen || _controlMustBkgPainting){
                    isFreshScreen = false;
                    drawBackgroung(g);
                    drawLyric(g, isFreshScreen);
                    isDrawed = true;
                }
                drawPanel(g, isFreshScreen);
                if (m_isChangedSyl && isFreshScreen){
                    // gdy m_mustRepaint == true - jest juz malowany tekst
                    if ( ! isDrawed)
                        drawLyric(g, isFreshScreen);
                    m_isChangedSyl = false;
                }
            }
            
        }
//        if (m_debugString != null){
//            g.setColor(0xFF0000);
//            g.drawString(m_debugString, m_width, 0, g.RIGHT|g.TOP);
//        }
        if ( ! isFreshScreen)
            m_isFreshScreen = true;
        m_isPainting = false;
        
    }
    public synchronized void paint(Graphics g){
        if (m_isPainting)
            return;
        m_isPainting = true;
        g.drawImage(m_offscreen, 0, 0, g.LEFT|g.TOP);
        m_isPainting = false;
    }
    static int liczniczek = 0;
    private void drawBackgroung(Graphics g){
        if (m_imgBackgroung  != null ){
            if (m_lineBase < 0)
                g.drawImage(m_imgBackgroung , 0,0, g.TOP|g.LEFT);
            else
                g.drawImage(m_imgBackgroung , 0, m_height, g.BOTTOM|g.LEFT);
        } else{
            g.setColor(m_colBackground);
            g.fillRect(0, 0, m_width, m_height);
        }
        _controlMustBkgPainting = false;
    }
    private void drawPanel(Graphics g, boolean isFreshScreen){
        if (m_imgPanel  != null ){
            //malujemy panela
            int panel_y = 0;
            final int panel_h = 12;
            if (m_lineBase >= 0){
                panel_y = m_height-panel_h;
            }
            //if (MainMIDlet.DEBUG) System.err.println("Clip:"+g.getClipX()+", "+g.getClipY()+", "+g.getClipWidth()+", "+g.getClipHeight());
            g.drawImage(m_imgPanel , 0, panel_y, g.TOP|g.LEFT);
            g.setColor(0xffffff);
            g.fillArc(9, panel_y+1, 10, 10, 90, m_arcSong);
            g.setColor(0xff0000);
            g.fillArc(21, panel_y+1, 10, 10, 90, m_arcSyllable);
            // volume
            g.setColor(0x000000);
            g.fillRect(1, panel_y+1, 6, (100 - m_volume) / 10);
            if (m_imgBan  != null && ! isFreshScreen){
                g.drawImage(m_imgBan, 32, panel_y, g.TOP+g.LEFT);
            }
        }
        
    }
    private void drawLyric(Graphics g, boolean isFreshScreen){
        // wyliczyæ pole do clippingu
        if (_controlMustBkgPainting){
            drawBackgroung(g);
        }
        int lineHeight = m_font.getHeight() + m_lineSpacing;
        g.setFont(m_font);
        int posY = m_panLyrTop;
        for (int i=0; i<m_screenData.length; i++){
            if (m_screenData[i] == null)
                continue;
            for (int j=0; j<m_screenData[i].length; j++){
                if ( isFreshScreen && i != m_screenRowActive){
                    continue;
                }
                int pack = m_screenData[i][j];
                int idxData = pack & ~(-1 << 16);
                int charPosX = pack  >> 16;
                int sylIdx = unpackSylIdx(m_lyrData[idxData]);
                int sylLen = unpackSylLen(m_lyrData[idxData]);
                if (charPosX == 0 && "/".equals(m_lyrText.substring(sylIdx, sylIdx+1))){
                    sylIdx++;
                    sylLen--;
                    charPosX += m_font.charWidth('/');
                }
                int color = m_colOld;
                if (idxData >= m_idxSylNotOld){
                    color = m_colNew0;
                    if (unpackSinger(m_lyrData[idxData]) == 1)
                        color = m_colNew1;
                }
                if (idxData == m_idxSylActive && i == m_screenRowActive)
                    color = m_colActive;
                drawString(g, color, m_lyrText.substring(sylIdx, sylIdx+sylLen),
                        m_panLyrLeft + charPosX, posY, g.TOP|g.LEFT);
            }
            posY += lineHeight;
        }
        
    }
    private void drawString(Graphics g, int color, String str, int x, int y, int flags){
        if ((m_fontFlags & MainMIDlet.FLAG_FONT_SHADOW) == MainMIDlet.FLAG_FONT_SHADOW){
            g.setColor(m_colShadow);
            g.drawString(str, x+2, y+2, flags);
        }
        if ((m_fontFlags & MainMIDlet.FLAG_FONT_OUTLINE) == MainMIDlet.FLAG_FONT_OUTLINE){
            g.setColor(m_colOutline);
            g.drawString(str, x-1, y-1, flags);
            g.drawString(str, x-1, y+1, flags);
            g.drawString(str, x-1, y+1, flags);
            g.drawString(str, x+1, y+1, flags);
        }
        g.setColor(color);
        g.drawString(str, x, y, flags);
    }
    public void run() {
        //calculateLyric();
        while (! m_stop){
            try{
                Thread.sleep(m_refresh);
                try{
                    switch(m_state){
                        case SING:
                            sing();
                            break;
                        case STOP:
                            stop();
                            break;
                        case SINGING:{
                            long time = m_player.getTime() + m_before + m_refresh;
                            // sprawdzamy czy m_idxSylActive jest ci¹gle active
                            if (m_idxSylActive != -1){
                                long pack = m_lyrData[m_idxSylActive];
                                int sylActTimeStart = unpackTimeStart(pack);
                                int sylActTimeLong = unpackTimeLong(pack);
                                if (time > (sylActTimeStart + sylActTimeLong)){
                                    // gaœnie aktywna
                                    if (MainMIDlet.DEBUG) System.err.println("gaœnie aktywna "+time+" > ("+sylActTimeStart+" + "+sylActTimeLong+")");
                                    m_idxSylActive = -1;
                                    m_idxSylNotOld++;
                                    m_isChangedSyl = true;
                                    if (MainMIDlet.DEBUG) System.err.println("run() m_idxSylNotOld:"+m_idxSylNotOld);
                                    if (m_idxSylNotOld > m_idxDataLastSylInRow){
                                        scrollScereenData();
                                        _controlMustBkgPainting = true;
                                    }
                                }
                            }
                            m_arcSyllable = 0;
                            // sparwdzam, czy m_idxSylNotOld nie stala sie aktywna
                            if (m_idxSylNotOld < m_lyrData.length){
                                long pack = m_lyrData[m_idxSylNotOld];
                                int sylNotOldTimeStart = unpackTimeStart(pack);
                                int sylNotOldTimeLong = unpackTimeLong(pack);
                                // nowa sylaba - na potrzeby odliczania czasu do jej rozpoczêcia
                                int sylNewTimeStart = unpackTimeStart(pack);
                                if (m_idxSylNotOld < m_lyrData.length){
                                    if (MainMIDlet.DEBUG) System.err.println("time:"+time+", sylNotOldTimeStart: "+sylNotOldTimeStart+", sylNotOldTimeStart: "+sylNotOldTimeStart+", sylNotOldTimeLong: "+sylNotOldTimeLong+", (sylNotOldTimeStart+sylNotOldTimeLong): "+(sylNotOldTimeStart+sylNotOldTimeLong));
//                                    if (time >= sylNotOldTimeStart
//                                        && time < (sylNotOldTimeStart + sylNotOldTimeLong)){
//                                        // nietara sta³a siê aktywna
//                                        m_idxSylActive = m_idxSylNotOld;
//                                        m_isChangedSyl = true;
//                                        if (MainMIDlet.DEBUG) System.err.println("sylNewTimeStart+1:"+sylNewTimeStart);
//                                        if ((m_idxSylNotOld+1) >= m_lyrData.length){
//                                            sylNewTimeStart = -1;
//                                        } else{
//                                            sylNewTimeStart = unpackTimeStart(m_lyrData[m_idxSylNotOld+1]);
//                                        }
//                                    }
                                    if (time >= sylNotOldTimeStart){
                                        // czas na now¹ sylabê
                                        m_idxSylActive = m_idxSylNotOld;
                                        m_isChangedSyl = true;
                                        if ( time < (sylNotOldTimeStart + sylNotOldTimeLong)){
                                            // niestara sta³a siê aktywna - nie zd¹¿y³a siê zestarzeæ
                                            if ((m_idxSylNotOld+1) >= m_lyrData.length){
                                                sylNewTimeStart = -1;
                                            } else{
                                                sylNewTimeStart = unpackTimeStart(m_lyrData[m_idxSylNotOld+1]);
                                            }
                                            if (MainMIDlet.DEBUG) System.err.println("Jest sylNewTimeStart+1:"+sylNewTimeStart);
                                        } else{
                                            // sylaba sta³a siê aktywna i zazaz potem nieaktywna
                                            
                                        }
                                    }
                                    
                                }
                                int okres;
                                if (m_idxSylNotOld == 0){
                                    okres = sylNotOldTimeStart;
                                } else{
                                    okres = sylNotOldTimeStart - unpackTimeStart(m_lyrData[m_idxSylNotOld - 1]);
                                }
                                if (sylNewTimeStart!=-1)
                                    m_arcSyllable = (int)((360 * (sylNewTimeStart - time)) / okres);
                                else
                                    m_arcSyllable = 0;
                            }
                            // wyliczamy zegary
                            m_arcSong = (int)(360 - (360 * time) / m_song.m_mlyrDuration);
                            if (time > m_song.m_mlyrDuration)
                                stop();
                            update();
                            repaint();
                            break;
                        }
                        default:
                            update();
                            repaint();
                            break;
                    }
                } catch (Exception e){
                    if (MainMIDlet.DEBUG) System.err.println(e.toString());
                }
                update();
                repaint();
            } catch (Exception e){
                if (MainMIDlet.DEBUG) System.err.println(e.toString());
            }
        }
        MainMIDlet.m_instance.startUI();
    }
    public void stop(){
        if (MainMIDlet.DEBUG) System.err.println("Engine.stop()");
        MainMIDlet.m_instance.setView(this, MainMIDlet.MENU);
        if (m_player != null && m_player.stop()){
            MainMIDlet.m_instance.setView(this, MainMIDlet.MENU|MainMIDlet.SING);
            m_player = null;
        }
        // pocz¹tek piosenki
        m_idxSylNotOld = 0;
        scrollScereenData();
        m_isFreshScreen = false;
        m_introPos = Integer.MAX_VALUE;
        m_state = STOPED;
    }
    public void sing(){
        if (MainMIDlet.DEBUG) System.err.println("Engine.sing()");
        if (MainMIDlet.DEBUG) System.err.println("Engine.sing() MainMIDlet.m_instance "+MainMIDlet.m_instance);
        if (MainMIDlet.DEBUG) System.err.println("Engine.sing() MainMIDlet.MENU: "+MainMIDlet.MENU);
        MainMIDlet.m_instance.setView(this, MainMIDlet.MENU);
        if (MainMIDlet.DEBUG) System.err.println("Engine.sing() A");
        launchPlayer();
        if (MainMIDlet.DEBUG) System.err.println("Engine.sing() B");
        MainMIDlet.m_instance.setView(this, MainMIDlet.MENU|MainMIDlet.STOP);
        if (MainMIDlet.DEBUG) System.err.println("Engine.sing() C");
        m_state = SINGING;
        // to dla motki
        m_idxLine = 0;
        m_idxLineLast = -1;
        m_idxTime = 0;
        m_idxTimeLast = -1;
        m_isFreshScreen = false;
        if (MainMIDlet.DEBUG) System.err.println("Engine.sing() ... done");
    }
    void launchPlayer(){
        stop();
        if (m_song.m_MidiLocation != null)
            m_player = new MKPlayer(this, m_song.m_MidiLocation);
        else
            m_player = new MKPlayer(this, m_song.m_MidiStream);
        if (m_volume < 0)
            m_volume = 100;
        m_player.start(m_volume);
        m_volume = m_player.setVolume(m_volume);
        if (m_volume < 0)
            m_volume = 100;
        m_state = LOADED;
    }
    public void playerEvent(byte event){
        if (event == m_player.EVENT_STARTED){
            if (MainMIDlet.DEBUG) System.out.println("playerEvent started");
        } else if (event == m_player.EVENT_STOPED){
            if (MainMIDlet.DEBUG) System.out.println("playerEvent stoped");
            MainMIDlet.m_instance.setView(this, MainMIDlet.MENU|MainMIDlet.SING);
        }
        m_isFreshScreen = false;
    }
    
    void scrollScereenData(){
        if (m_idxSylNotOld > 0){
            // normalna przejœcie linia w dó³
            int idxDataLast = 0;
            for (int i=1 ;i<m_screenData.length; i++){
                m_screenData[i-1] = m_screenData[i];
                if (m_screenData[i] != null){
                    if (m_screenData[i].length > 0){
                        idxDataLast = m_screenData[i][m_screenData[i].length-1] & ~(-1 << 16);
                    }
                }
            }
            // jesli jest tylko jedna linia to nic nie zostanie przewiniete
            // a co za tym idzie - nie zostanie odczytana ostatnia pozycja z wiersza idxDataLast
            if (m_screenData.length == 1){
                idxDataLast = m_screenData[0][m_screenData[0].length-1] & ~(-1 << 16);
            }
            fillScreenRow(m_screenData.length-1, idxDataLast + 1);
        } else if (m_idxSylNotOld == 0){
            // pocz¹tkowa inicjacja
            for (int i=0 ;i<m_screenRowActive; i++){
                m_screenData[i] = new int[0];
            }
            int idxDataLast = 0;
            for (int i=m_screenRowActive ;i<m_screenData.length; i++){
                idxDataLast = fillScreenRow(i, idxDataLast);
            }
        }
        try{
            // TODO: Tu skoñczy³em
            m_idxDataLastSylInRow = m_screenData[m_screenRowActive][m_screenData[m_screenRowActive].length-1] & ~(-1 << 16);
        } catch(Exception e){
            if (MainMIDlet.DEBUG) e.printStackTrace();
            if (MainMIDlet.DEBUG) System.err.println("m_screenData.length:"+m_screenData.length+", m_screenRowActive:"+m_screenRowActive);
        }
        if (m_idxDataLastSylInRow  >= m_lyrData.length - 1){
            // co by dalej juz nie przewijac
            m_idxDataLastSylInRow = Integer.MAX_VALUE;
        }
        if (MainMIDlet.DEBUG) System.err.println("m_idxDataLastSylInRow:"+m_idxDataLastSylInRow);
        if (MainMIDlet.DEBUG) showScreenData();
        m_isFreshScreen = false;
    }
    public static boolean _controlMustBkgPainting = false;
    private int fillScreenRow(int idxRow, int idxData){
        if (idxRow == m_screenData.length-1)
            if (MainMIDlet.DEBUG) System.err.println("fillScreenRow("+idxRow+", "+idxData+")");
        int lineWidth = m_width - 2 * m_border;
        String line = "";
        Vector data = new Vector();
        int i=0;
        do{
            if (idxData >= m_lyrData.length){
                break;
            }
            int sylIdx = unpackSylIdx(m_lyrData[idxData]);
            int sylLen = unpackSylLen(m_lyrData[idxData]);
            String sylaba = m_lyrText.substring(sylIdx, sylIdx+sylLen);
            if (sylaba.startsWith("/")){
                if (MainMIDlet.DEBUG) System.err.println("sylaba.startsWith(/) line.length():"+line.length());
                if (line.length() > 0){
                    break;
                } else{
                    //nic nie robimy, ale tenznak musi byc wylapany przy malowaniu
//                    long pack = m_lyrData[idxData];
//                    pack += 1 << (64 - 24 - 16 - 16);
//                    pack -= 1 << (64 - 24 - 16 - 16 - 6);
//                    m_lyrData[idxData] = pack;
//                    sylIdx = unpackSylIdx(m_lyrData[idxData]);
//                    sylLen = unpackSylLen(m_lyrData[idxData]);
//                    sylaba = m_lyrText.substring(sylIdx, sylIdx+sylLen);
                }
            }
            int charPosX = m_font.stringWidth(line);
            line += sylaba;
            int charEndX = m_font.stringWidth(line);
            if (charEndX > lineWidth || idxData >= m_lyrData.length){
                // konczymy petle whilw
                break;
            } else{
                int pack = charPosX << 16 | idxData;
                data.addElement(new Integer(pack));
                idxData++;
            }
        } while (true);
        m_screenData[idxRow] = new int[data.size()];
        for(int j=0; j<data.size(); j++){
            m_screenData[idxRow][j] = ((Integer)data.elementAt(j)).intValue();
        }
        return idxData;
        
    }
    private void showScreenData(){
        for (int i=0; i<m_screenData.length; i++){
            if (MainMIDlet.DEBUG) System.err.print("[");
            int[] row = m_screenData[i];
            if (row != null){
                String line = "";
                for (int j=0; j<row.length; j++){
                    int pack = row[j];
                    int idxData = pack & ~(-1 << 16);
                    int charPosX = pack  >> 16;
                    int sylIdx = unpackSylIdx(m_lyrData[idxData]);
                    int sylLen = unpackSylLen(m_lyrData[idxData]);
                    line += ""+idxData+":"+charPosX+" "+m_lyrText.substring(sylIdx, sylIdx+sylLen) + ",";
                }
                if (line.length() > 0){
                    line = line.substring(0, line.length()-1);
                    if (MainMIDlet.DEBUG) System.err.print(line);
                }
            }
            
            if (MainMIDlet.DEBUG) System.err.println("]");
        }
    }
    private final int unpackSylIdx(long pack){
        return (int)((pack >> (64 - 24 - 16 - 16)) & ~(-1 << 16));
    }
    private final int unpackSylLen(long pack){
        return (int)((pack >> (64 - 24 - 16 - 16 - 6)) & ~(-1 << 6));
    }
    private final int unpackSinger(long pack){
        return (int)((pack >> (64 - 24 - 16 - 16 - 6 - 1)) & ~(-1 << 1));
    }
    private final int unpackTimeStart(long pack){
        return (int)((pack >> (64 - 24)) & ~(-1 << 24));
    }
    private final int unpackTimeLong(long pack){
        return (int)((pack >> (64 - 24 - 16)) & ~(-1 << 16));
    }
}
