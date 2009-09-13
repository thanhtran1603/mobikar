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
    String m_name;
    InputStream m_MidiStream = null;
    String m_MidiLocation = null;
    long m_mlyrDuration;
    String m_mlyrTitle = "";
    String m_mlyrMusic = "";
    String m_mlyrLyrics = "";
    String m_mlyrArtist = "";
    String m_mlyrCreator = "";
    String m_mlyrVersion = "";
    String m_mlyrNote = "";
    String m_mlyrText = null;
    long[] m_mlyrData = null;
    public Song(String name, InputStream liryc, InputStream midi){
        m_name = name;
        m_MidiStream = midi;
        loadLiryc(liryc);
    }
    public Song(String name, InputStream liryc, String location){
        m_name = name;
        m_MidiLocation = location;
        loadLiryc(liryc);
    }
    public void loadLiryc(InputStream liryc){
        if (MainMIDlet.DEBUG) System.err.println("loadLiryc("+liryc+")");
        
        final int TAG_MLYR = ('m'<<24)|('L'<<16)|('Y'<<8)|('R');
        final int TAG_TITLE = ('T'<<24)|('I'<<16)|('T'<<8)|('L');
        final int TAG_MUSIC = ('M'<<24)|('U'<<16)|('S'<<8)|('I');
        final int TAG_LYRICS = ('L'<<24)|('Y'<<16)|('R'<<8)|('I');
        final int TAG_ARTIST = ('A'<<24)|('R'<<16)|('T'<<8)|('I');
        final int TAG_CREATOR = ('C'<<24)|('R'<<16)|('E'<<8)|('A');
        final int TAG_VERSION = ('V'<<24)|('E'<<16)|('R'<<8)|('$');
        final int TAG_NOTE = ('N'<<24)|('O'<<16)|('T'<<8)|('E');
        final int TAG_MILLIS = ('M'<<24)|('S'<<16)|('E'<<8)|('K');
        final int TAG_TEXT = ('T'<<24)|('E'<<16)|('X'<<8)|('T');
        final int TAG_IDXS = ('I'<<24)|('D'<<16)|('X'<<8)|('S');
        
        DataInputStream dis = null;
        try{
            dis = new DataInputStream(liryc);
            int tag = dis.readInt();
            int size = dis.readInt();
            if (tag != TAG_MLYR){
                System.out.println("To niejest format mLYR");
                return;
            }
            do{
                tag = dis.readInt();
                size = dis.readInt();
                if (tag == TAG_TITLE){
                    m_mlyrTitle = MainMIDlet.m_instance.m_lang.convert(dis.readUTF());
                } else if (tag == TAG_MUSIC){
                    m_mlyrMusic = MainMIDlet.m_instance.m_lang.convert(dis.readUTF());
                } else if (tag == TAG_LYRICS){
                    m_mlyrLyrics = MainMIDlet.m_instance.m_lang.convert(dis.readUTF());
                } else if (tag == TAG_ARTIST){
                    m_mlyrArtist = MainMIDlet.m_instance.m_lang.convert(dis.readUTF());
                } else if (tag == TAG_CREATOR){
                    m_mlyrCreator = MainMIDlet.m_instance.m_lang.convert(dis.readUTF());
                } else if (tag == TAG_VERSION){
                    m_mlyrVersion = dis.readUTF();
                } else if (tag == TAG_NOTE){
                    m_mlyrNote = MainMIDlet.m_instance.m_lang.convert(dis.readUTF());
                } else if (tag == TAG_MILLIS){
                    m_mlyrDuration = dis.readLong();
                } else if (tag == TAG_TEXT){
                    m_mlyrText = MainMIDlet.m_instance.m_lang.convert(dis.readUTF());
                } else if (tag == TAG_IDXS){
                    int idxSize = dis.readInt();
                    m_mlyrData = new long[idxSize];
                    for(int i=0; i<m_mlyrData.length; i++)
                        m_mlyrData[i] = dis.readLong();
                    break;
                } else{
                    dis.skip(size);
                }
            } while(true);
            dis.close();
        } catch (Exception e){
            if (MainMIDlet.DEBUG) System.err.println("Song(): "+e);
        }
        if (MainMIDlet.DEBUG) System.err.println("Song() done");
    }
    
    public String getName(){
        return m_name;
    }
}
