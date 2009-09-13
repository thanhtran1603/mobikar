/*
 * Song.java
 *
 * Created on 27 wrzesieñ 2004, 23:01
 */

import javax.microedition.midlet.*;
import java.io.*;

/**
 *
 * @author  MiKO
 * @version
 */
public class Song {
    String m_name = null;
    long m_duration = 115500;
    InputStream m_MidiStream = null;
    int[] m_times = null;
    String[] m_syllables = null;
    public Song(String title, InputStream midi, InputStream liryc, long duration){
        if (MainMIDlet.DEBUG) System.err.println("Song("+title+", "+midi+", "+liryc+", "+m_duration+")");
        m_name = title;
        m_duration = duration;
        m_MidiStream = midi;
        
        DataInputStream dis = null;
        try{
            dis = new DataInputStream(liryc);
            int size = dis.readInt();
            m_times = new int[size];
            m_syllables = new String[size];
            for (int i=0; i<size; i++){
                m_times[i] = dis.readInt();
                m_syllables[i] = dis.readUTF();
            }
            dis.close();
        }
        catch (Exception e){
            if (MainMIDlet.DEBUG) System.err.println("Song(): "+e);
        }        
        if (MainMIDlet.DEBUG) System.err.println("Song() done");
    }
    
    public String getName(){
        return m_name;
    }
    public long getDuration(){
        return m_duration;
    }
    public InputStream getMIDIStream(){
        return m_MidiStream;
    }
    public int[] getTextTimes(){
        return m_times;
    }
    public String[] getSyllables(){
        return m_syllables;
    }
}
