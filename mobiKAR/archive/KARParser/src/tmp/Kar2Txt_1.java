package tmp;
/*
 * Main.java
 *
 * Created on 26 luty 2005, 16:22
 */

import java.io.*;
import java.text.MessageFormat;
import javax.sound.midi.*;
import java.util.*;

/**
 *
 * @author a
 */
public class Kar2Txt_1 implements MetaEventListener{
    
    public static final int SYLABE_TIME_MIN = 50;
    public static final int SYLABE_TIME_MAX = 1000;
    public static final int SYLABE_GAP_MIN = 100;
    
    double m_multiple = 0;
    static boolean isPlaying = true;
    static boolean isVerbosing = false;
    long m_startTime = 0;
    Sequence m_sequence;
    MidiEvent m_currentEvent = null;
    List<Object[]> m_lyric = new LinkedList();
    
    public void meta(MetaMessage meta){
        if (meta.getType() != 5)
            return;
        long pos = System.currentTimeMillis() - m_startTime;
        if ( ! isPlaying)
            pos = (long)(m_currentEvent.getTick() * m_multiple);
        String syllabe = toOneLine(new String(meta.getData()));
        Object[] objs = {new Long(pos), new Long(0), syllabe};
        if (syllabe.trim().length() > 0){
            m_lyric.add(objs);
            if (isVerbosing)
                System.out.println(objs[0]+"\t"+objs[1]+"\t0\t"+toOneLine((String)objs[2]));
        }
    }
    
    /** Creates a new instance of Main */
    public Kar2Txt_1() {
    }
    
    
    public void extract(InputStream midiStream) throws Exception{
        m_lyric.clear();
        if (isPlaying){
            Sequencer sequencer = MidiSystem.getSequencer();
            m_sequence = MidiSystem.getSequence(midiStream);
            sequencer.setSequence(m_sequence);
            sequencer.addMetaEventListener(this);
            sequencer.open();
            sequencer.start();
            m_startTime = System.currentTimeMillis();
            while(true) {
                if(sequencer.isRunning()) {
                    try {
                        Thread.sleep(1000); // Check every second
                    } catch(InterruptedException ignore) {
                        break;
                    }
                } else {
                    break;
                }
            }
            // Close the MidiDevice & free resources
            sequencer.stop();
            sequencer.close();
        } else {
            m_sequence = MidiSystem.getSequence(midiStream);
            m_multiple = 1.0 * m_sequence.getMicrosecondLength() / 1000 / m_sequence.getTickLength();
            Track [] tracks = m_sequence.getTracks();
            if ( tracks == null )
                return;
            ArrayList lyricTracks[] = new ArrayList[tracks.length];
            for ( int i = 0; i < tracks.length; i++ ) {
                Track track = tracks[ i ];
                for ( int j = 0; j < track.size(); j++ ) {
                    m_currentEvent = track.get( j );
                    MidiMessage msg = m_currentEvent.getMessage();
                    if (msg instanceof MetaMessage){
                        meta((MetaMessage)msg);
                    }
                }
            }
        }
        
    }
    public void organize(){
        // poustawianie czasów trwania
        long lastTime = 0;
        Object[] oldObjs = null;
        for(Object[] newObjs : m_lyric){
            if (oldObjs == null)
                oldObjs = newObjs;
            long delta = ((Long)newObjs[0]).intValue() -((Long)oldObjs[0]).intValue() - SYLABE_GAP_MIN;
            delta = Math.min(SYLABE_TIME_MAX, Math.max(0,delta));
            lastTime = ((Long)oldObjs[0]).longValue();
            oldObjs[1] = new Long(delta);
            if (oldObjs == newObjs)
                continue;
            String prefix = "";
            if (oldObjs[0].equals(oldObjs[1]))
                prefix = "// ";
            if (((String)oldObjs[2]).trim().length() == 0)
                prefix = "// ";
            oldObjs = newObjs;
        }
        // usuwanie czasów trwania 0
        for(Iterator it = m_lyric.iterator(); it.hasNext(); ){
            Object[] objs = (Object[])it.next();
            long delta = ((Long)objs[1]).longValue();
            if (delta <= SYLABE_TIME_MIN){
                if (it.hasNext()){
                    Object[] objsNew = (Object[])it.next();
                    objsNew[2] = String.valueOf(objs[2]) + String.valueOf(objsNew[2]);
                    m_lyric.remove(objs);
                    // od nowa
                    it = m_lyric.iterator();
                } else{
                    objs[1] = new Long(SYLABE_TIME_MIN);
                }
            }
        }
        
        
        
    }
    public void write(PrintStream out){
        out.println("//d³ugoœæ\t"+toTimeString(m_sequence.getMicrosecondLength()/1000));
        out.println("MILLIS\t"+m_sequence.getMicrosecondLength()/1000);
        out.println("TITLE\t<empty>");
        out.println("ARTIST\t<empty>");
        out.println("MUSIC\t<empty>");
        out.println("LYRICS\t<empty>");
        out.println("CREATOR\t<empty>");
        out.println("VERSION\t<empty>");
        out.println("NOTE\t<empty>");
        for(Object[] objs : m_lyric){
            String prefix = "";
            if (objs[0].equals(objs[1]))
                prefix = "// ";
            if (((String)objs[2]).trim().length() == 0)
                prefix = "// ";
            out.println(prefix+objs[0]+"\t"+objs[1]+"\t0\t"+toOneLine((String)objs[2]));
        }
        
    }
    /**
     * @param args the command line arguments
     */
    
    public static final String toTimeString(long timeMillis){
        return new MessageFormat("{0,date,mm:ss.SSS}").format(new Object[]{new java.util.Date(timeMillis)});
        
    }
    public static final String toOneLine(String str){
        String ret = "";
        for (int i=0; i<str.length(); i++){
            if (str.charAt(i) == '\n')
                ret += '/';
            else
                ret += str.charAt(i);
        }
        if (ret != null)
            ret = ret.replace('_', ' ');
        return ret;
    }
    public static void main(String[] args) throws Exception {
        String fileNameIn = null;
        String fileNameOut = null;
        PrintStream out = System.out;
        int opt = 0;
        if (args.length > 0){
            if ("-q".equals(args[opt])){
                isPlaying = false;
                opt++;
            }
            if ("-v".equals(args[opt])){
                isVerbosing = true;
                opt++;
            }
            if (args.length > opt){
                fileNameIn = args[opt];
                opt++;
            }
            if (args.length > opt){
                fileNameOut = args[opt];
                out = new PrintStream(new FileOutputStream(fileNameOut));
                opt++;
            }
        }
        if (fileNameIn == null || fileNameOut == null){
            System.out.println("Parametry -q -v plik_wejœciowy plik_wyjœciowy");
            System.out.println("q -podczas konwersji nie bêdzie odtwarzana muzyka");
            System.out.println("v -podczas konwersji bêd¹ prezentowane informacje z przebiegu konwersji");
            return;
        }
        Kar2Txt_1 parser = new Kar2Txt_1();
        parser.extract(new FileInputStream(fileNameIn));
        parser.organize();
        parser.write(out);
    }
    
}
