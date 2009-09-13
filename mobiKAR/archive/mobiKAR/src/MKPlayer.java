/*
 * MidiManager.java
 *
 * Created on 11 listopad 2004, 11:08
 */
//#if DefaultConfiguration || MMAPI
import javax.microedition.media.*;
import javax.microedition.media.control.*;
//#endif
//#if SIES55
//# import com.siemens.mp.media.*;
//# import com.siemens.mp.media.control.*;
//#endif
import java.io.*;
import java.util.*;

/**
 *
 * @author  MiKO
 */
//#if API_JSR135
public class MKPlayer implements PlayerListener {
//#else
//# public class MKPlayer {
//#endif
    public static final byte EVENT_STARTED = 0;
    public static final byte EVENT_STOPED = 1;
    
    private final boolean IS_INTERNAL_TIMING = false;
    
    // to dla metody m_engine.playerEvent()
    Engine m_engine = null;
    
    // wejscie dla danych muzyki
    InputStream m_dataStream = null;
    String m_fileName = null;
    long m_time = -1;
    long m_start = -1;
    
    //#if SND_MOT
//#     com.motorola.game.BackgroundMusic m_player = null;
    //#endif
    
    // player
    //#if SND_SAM
//--     com.samsung.util.AudioClip m_player = null;
    //#endif
    
    //#if API_JSR135
    Player m_player = null;
    VolumeControl m_control = null;
    //#endif
    /** Creates a new instance of MidiManager */
    public MKPlayer(Engine engine, InputStream dataStream) {
        m_engine = engine;
        m_dataStream = dataStream;
        load();
    }
    public MKPlayer(Engine engine, String fileName) {
        m_engine = engine;
        m_fileName = fileName;
        load();
    }
    private void load(){
        if (MainMIDlet.DEBUG) System.out.println("MKPlayer.load()");
        stop();
        try {
            if (m_dataStream != null){
                //#if SND_SAM
//--                // TODO: obs³uga plików zapisanych w RMSach
//--                throw new IOException("Jeszcze tego nie oprogramowa³em");
                //#endif
                
                //#if SND_MOT
//#                 throw new IOException("In Motorola mode is not allowd play with stream");
                //#endif
                //#if API_JSR135
                try{
                    //#if SIES55
//#                     m_player = Manager.createPlayer(m_dataStream, "audio/x-mid");
                    //#else
                    m_player = Manager.createPlayer(m_dataStream, "audio/midi");
                    //#endif
                } catch(Exception me){
                    if (MainMIDlet.DEBUG) System.out.println("Manager.createPlayer(m_dataStream, audio/midi):" +me.toString());
                    m_player = Manager.createPlayer(m_dataStream, "audio/x-mid");
                }
                //#endif
            } else{
                //#if SND_SAM
//--                m_player = new com.samsung.util.AudioClip(3, m_fileName);
                //#endif
                //#if SND_MOT
//#                 if (MainMIDlet.HISTORY) MainMIDlet.m_instance.history.addElement("createBackgroundMusic( "+m_fileName+")");
//#                 m_player = com.motorola.game.BackgroundMusic.createBackgroundMusic(m_fileName);
//#                 if (MainMIDlet.HISTORY) MainMIDlet.m_instance.history.addElement("m_player: "+m_player);
                //#endif
                //#if API_JSR135
                m_dataStream = this.getClass().getResourceAsStream(m_fileName);
                try{
                    //#if SIES55
//#                     m_player = Manager.createPlayer(m_dataStream, "audio/x-mid");
                    //#else
                    m_player = Manager.createPlayer(m_dataStream, "audio/midi");
                    //#endif
                } catch(Exception me){
                    if (MainMIDlet.DEBUG) System.out.println("Manager.createPlayer(m_dataStream, audio/midi):" +me.toString());
                    m_player = Manager.createPlayer(m_dataStream, "audio/x-mid");
                }
                //#endif
            }
            //#if API_JSR135
            //m_player.prefetch();
            //#endif
        } catch (Exception e) {
            if (MainMIDlet.DEBUG) System.out.println("MKPlayer.load(): " +e.toString());
            if (MainMIDlet.HISTORY) MainMIDlet.m_instance.history.addElement("load() e: "+e.toString());
        }
        //obtainVolumeControl();
    }
    public void start(int volume){
        //#if SND_SAM
//--        int sam_volume = 0;
//--        if (volume > 0)
//--            sam_volume = volume / 20;
//--        m_player.play(1, sam_volume);
        //#endif
        //#if SND_MOT
//#         m_engine.playBackgroundMusic(m_player, false);
        //#endif
        //#if API_JSR135
        if (m_player != null){
            try {
                m_player.addPlayerListener(this);
                m_player.start();
                setVolume(volume);
            } catch (MediaException e) {
                if (MainMIDlet.DEBUG) System.out.println("MKPlayer.start(): " +e.toString());
            }
        }
        //#endif
        m_start = System.currentTimeMillis();
    }
    public boolean stop(){
        if (MainMIDlet.DEBUG) System.err.println("MidiManager.stop()");
        boolean ret = false;
        //#if SND_SAM
//--        if (m_player != null){
//--            try {
//--                m_player.stop();
//--                m_player = null;
//--                System.gc();
//--                ret = true;
//--            } catch (Exception e) {
//--                if (MainMIDlet.DEBUG) System.out.println("FAILED: exception for stop: " +e.toString());
//--            }
//--        }
        //#endif
        //#if SND_MOT
//#         m_engine.playBackgroundMusic(null, false);
        //#endif
        
        //#if API_JSR135
        if (m_player != null){
            try {
                m_player.removePlayerListener(this);
                //m_player.stop();
                m_player.close();
                m_player = null;
                System.gc();
                ret = true;
            } catch (Exception e) {
                if (MainMIDlet.DEBUG) System.out.println("FAILED: exception for stop: " +e.toString());
            }
        }
        //#endif
        m_start = -1;
        return ret;
    }
    //#if API_JSR135
    public void playerUpdate(Player player, String event, Object eventData) {
        if (MainMIDlet.DEBUG) System.out.println("playerUpdate("+player+", "+event+", "+eventData+")");
        //if (MainMIDlet.DEBUG) System.out.println("player.getMediaTime():"+player.getMediaTime());
        if (event.equals(PlayerListener.STARTED)){
            m_start = System.currentTimeMillis();
            if (MainMIDlet.DEBUG) System.out.println("player.getDuration():"+player.getDuration());
            m_engine.playerEvent(EVENT_STARTED);
        } else  if (event.equals(PlayerListener.END_OF_MEDIA)){
            if (MainMIDlet.DEBUG) System.out.println("player.getDuration():"+player.getDuration());
            m_engine.playerEvent(EVENT_STOPED);
        }
    }
    //#endif
    
    private void obtainVolumeControl(){
        //#if API_JSR135
        if (m_player != null){
            m_control = (VolumeControl)m_player.getControl("VolumeControl");
        }
        //#endif
    }
    public int getVolume() {
        int ret = 100;
        obtainVolumeControl();
        //#if API_JSR135
        if (m_control != null && m_player != null) {
            ret = m_control.getLevel();
        }
        //#endif
        return ret;
    }
    public int setVolume(int volume) {
        int ret = -1;
        obtainVolumeControl();
        //#if API_JSR135
        if (m_control != null && m_player != null) {
            m_control.setLevel(volume);
            ret = m_control.getLevel();
        }
        //#endif
        return ret;
    }
    public long getTime(){
        long ret = -1; //TIME_UNKNOWN = -1
        if (IS_INTERNAL_TIMING){
            //#if API_JSR135
            if (m_player != null){
                ret = m_player.getMediaTime();
                if (ret > 0)
                    ret /= 1000;
            }
            //#endif
        }
        if (ret <= 0 && m_start != -1)
            ret = System.currentTimeMillis() - m_start;
        return ret;
    }
}
