/*
 * MidiManager.java
 *
 * Created on 11 listopad 2004, 11:08
 */
/*#MMAPI#*///<editor-fold>
//--import javax.microedition.media.*;
//--import javax.microedition.media.control.*;
/*$MMAPI$*///</editor-fold>
/*#SIE_S55#*///<editor-fold>
//--import com.siemens.mp.media.*;
//--import com.siemens.mp.media.control.*;
/*$SIE_S55$*///</editor-fold>
import java.io.*;
import java.util.*;

/**
 *
 * @author  MiKO
 */
/*#API_JSR135#*///<editor-fold>
//--public class MKPlayer implements PlayerListener{
/*$API_JSR135$*///</editor-fold>
/*#!API_JSR135#*///<editor-fold>
public class MKPlayer{
/*$!API_JSR135$*///</editor-fold>
    final boolean DEBUG = false;
    public static final byte EVENT_STARTED = 0;
    public static final byte EVENT_STOPED = 1;
    
    MKPlayerListener m_listener = null;
    
    // wejscie dla danych muzyki
    InputStream m_dataStream = null;
    String m_fileName = null;
    
    // player
    /*#API_JSR135#*///<editor-fold>
//--    Player m_player = null;
//--    VolumeControl m_control = null;
    /*$API_JSR135$*///</editor-fold>
    /** Creates a new instance of MidiManager */
    public MKPlayer(MKPlayerListener listener, InputStream dataStream) {
        m_listener = listener;
        m_dataStream = dataStream;
        load();
    }
    public MKPlayer(MKPlayerListener listener, String fileName) {
        m_listener = listener;
        m_fileName = fileName;
        load();
    }
    private void load(){
        stop();
        try {
            if (m_dataStream != null){
/*#API_JSR135#*///<editor-fold>
//--                m_player = Manager.createPlayer(m_dataStream, "audio/midi");
/*$API_JSR135$*///</editor-fold>
            } else{
/*#API_JSR135#*///<editor-fold>
//--                m_player = Manager.createPlayer(m_fileName);
/*$API_JSR135$*///</editor-fold>
            }
/*#API_JSR135#*///<editor-fold>
//--            m_player.realize();
/*$API_JSR135$*///</editor-fold>
        } catch (Exception e) {
            if (DEBUG) System.out.println("FAILED: exception for prefetch: " +e.toString());
        }
        obtainVolumeControl();
//        m_player.addPlayerListener(this);
    }
    public void start(){
/*#API_JSR135#*///<editor-fold>
//--        if (m_player != null){
//--            try {
//--                m_player.start();
//--            } catch (MediaException e) {
//--                if (DEBUG) System.out.println("FAILED: exception for start: " +e.toString());
//--            }
//--        }
/*$API_JSR135$*///</editor-fold>
    }
    public boolean stop(){
        if (DEBUG) System.err.println("MidiManager.stop()");
        boolean ret = false;
/*#API_JSR135#*///<editor-fold>
//--        if (m_player != null){
//--            try {
//--                m_player.removePlayerListener(this);
//--                m_player.stop();
//--                m_player.close();
//--                m_player = null;
//--                System.gc();
//--                ret = true;
//--            } catch (MediaException e) {
//--                if (DEBUG) System.out.println("FAILED: exception for stop: " +e.toString());
//--            }
//--        }
/*$API_JSR135$*///</editor-fold>
        return ret;
    }
/*#API_JSR135#*///<editor-fold>
//--    public void playerUpdate(Player player, String event, Object eventData) {
//--        if (DEBUG) System.out.println("playerUpdate("+player+", "+event+", "+eventData+")");
//--        //if (DEBUG) System.out.println("player.getMediaTime():"+player.getMediaTime());
//--        if (event.equals(PlayerListener.STARTED)){
//--            if (DEBUG) System.out.println("player.getDuration():"+player.getDuration());
//--            m_listener.playerEvent(EVENT_STARTED);
//--        } else  if (event.equals(PlayerListener.END_OF_MEDIA)){
//--            if (DEBUG) System.out.println("player.getDuration():"+player.getDuration());
//--            m_listener.playerEvent(EVENT_STOPED);
//--        }
//--    }
/*$API_JSR135$*///</editor-fold>

    private void obtainVolumeControl(){
/*#API_JSR135#*///<editor-fold>
//--        if (m_player != null){
//--            m_control = (VolumeControl)m_player.getControl("VolumeControl");
//--        }
/*$API_JSR135$*///</editor-fold>
    }
    public int getVolume() {
        int ret = -1;
        obtainVolumeControl();
/*#API_JSR135#*///<editor-fold>
//--        if (m_control != null) {
//--            ret = m_control.getLevel();
//--        }
/*$API_JSR135$*///</editor-fold>
        return ret;
    }
    public int setVolume(int volume) {
        int ret = -1;
/*#API_JSR135#*///<editor-fold>
//--        if (m_control != null) {
//--            m_control.setLevel(volume);
//--            ret = m_control.getLevel();
//--        }
/*$API_JSR135$*///</editor-fold>
        return ret;
    }
}
