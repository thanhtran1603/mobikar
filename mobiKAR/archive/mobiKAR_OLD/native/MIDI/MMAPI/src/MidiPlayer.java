/*
 * MidiManager.java
 *
 * Created on 11 listopad 2004, 11:08
 */
import javax.microedition.media.*;
import javax.microedition.media.control.*;
import java.io.*;
import java.util.*;

/**
 *
 * @author  MiKO
 */
public class MidiPlayer implements PlayerListener{
    public static final byte EVENT_STARTED = 0;
    public static final byte EVENT_STOPED = 1;
    Engine m_engine = null;
    Player m_player = null;
    VolumeControl m_control = null;
    /** Creates a new instance of MidiManager */
    public MidiPlayer(Engine engine, InputStream midiSrtream) {
        m_engine = engine;
        load(midiSrtream);
    }
    private void load(InputStream midiSrtream){
        stop();
        try {
            m_player = Manager.createPlayer(midiSrtream, "audio/midi");
        }
        catch (Exception e) {
            if (MainMIDlet.DEBUG) System.out.println("FAILED: exception for createPlayer: " + e.toString());
        }
        // Obtain the information required to acquire the media resources
        try {
            m_player.realize();
        }
        catch (Exception e) {
            if (MainMIDlet.DEBUG) System.out.println("FAILED: exception for realize: " + e.toString());
        }
        // Acquire exclusive resources, fill buffers with media data
        try {
            //m_player.prefetch();
        }
        catch (Exception e) {
            if (MainMIDlet.DEBUG) System.out.println("FAILED: exception for prefetch: " +e.toString());
        }
        obtainVolumeControl();
        m_player.addPlayerListener(this);
    }
    public void start(){
        if (m_player != null){
            try {
                m_player.start();
            }
            catch (MediaException e) {
                if (MainMIDlet.DEBUG) System.out.println("FAILED: exception for start: " +e.toString());
            }
        }
    }
    public boolean stop(){
        if (MainMIDlet.DEBUG) System.err.println("MidiManager.stop()");
        boolean ret = false;
        if (m_player != null){
            try {
                m_player.removePlayerListener(this);
                m_player.stop();
                m_player.close();
                m_player = null;
                System.gc();
                ret = true;
            }
            catch (MediaException e) {
                if (MainMIDlet.DEBUG) System.out.println("FAILED: exception for stop: " +e.toString());
            }
        }
        return ret;
    }
    public void playerUpdate(javax.microedition.media.Player player, String event, Object eventData) {
        if (MainMIDlet.DEBUG) System.out.println("playerUpdate("+player+", "+event+", "+eventData+")");
        //if (MainMIDlet.DEBUG) System.out.println("player.getMediaTime():"+player.getMediaTime());
        if (event.equals(PlayerListener.STARTED)){
            if (MainMIDlet.DEBUG) System.out.println("player.getDuration():"+player.getDuration());
            m_engine.playerEvent(EVENT_STARTED);
        }
        else  if (event.equals(PlayerListener.END_OF_MEDIA)){
            if (MainMIDlet.DEBUG) System.out.println("player.getDuration():"+player.getDuration());
            m_engine.playerEvent(EVENT_STOPED);
        }
    }
    private void obtainVolumeControl(){
        if (m_player != null){
            m_control = (VolumeControl)m_player.getControl("VolumeControl");
        }
    }
    public int getVolume() {
        int ret = -1;
        obtainVolumeControl();
        if (m_control != null) {
            ret = m_control.getLevel();
        }
        return ret;
    }
    public int setVolume(int volume) {
        int ret = -1;
        if (m_control != null) {
            m_control.setLevel(volume);
            ret = m_control.getLevel();
        }
        return ret;
    }
}
