/*
 * Main.java
 *
 * Created on 26 luty 2005, 16:22
 */

package karparser;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.MessageFormat;
import java.util.*;

/**
 *
 * @author a
 */
public class Kar2Txt {
    
    /** Creates a new instance of Main */
    public Kar2Txt() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        String fileNameIn = "Mocny_IRA.mid";
        String fileNameOut = "Mocny_IRA.txt";
        if (args.length > 0){
            fileNameIn = args[0];
            fileNameOut = fileNameIn+".txt";
        }
        if (args.length > 1){
            fileNameOut = args[1];
        }
        //Sequence sequence = MidiSystem.getSequence(new FileInputStream(fileNameIn));
        Sequence sequence = (new StandardMidiFileReader()).getSequence(new FileInputStream(fileNameIn));
        long lenMillis = sequence.getMicrosecondLength() / 1000;
        long lenTicks = sequence.getTickLength();
        MidiUtils.TempoCache tempoCache = new MidiUtils.TempoCache();
        Track [] tracks = sequence.getTracks();
        if ( tracks != null ) {
            ArrayList lyricTracks[] = new ArrayList[tracks.length];
            for ( int i = 0; i < tracks.length; i++ ) {
                lyricTracks[i] = new ArrayList();
                Track track = tracks[ i ];
                for ( int j = 0; j < track.size(); j++ ) {
                    MidiEvent event = track.get( j );
                    MidiMessage msg = event.getMessage();
                    if (msg instanceof MetaMessage){
                        MetaMessage meta = (MetaMessage)msg;
                        if (meta.getType() != 5)
                            continue;
                        // long pos = event.getTick() * lenMillis / lenTicks;
                        long pos = MidiUtils.tick2microsecond(sequence, event.getTick(), tempoCache) / 1000;
                        String syllabe = toOneLine(new String(meta.getData()));
                        Object[] objs = {new Long(pos), new Long(0), syllabe};
                        if (syllabe.trim().length() > 0){
                            lyricTracks[i].add(objs);
                        }
                        if (lyricTracks[i].size() > 1){
                            objs = (Object[])lyricTracks[i].get(lyricTracks[i].size()-2);
                            long lastduration = pos - ((Long)objs[0]).longValue();
                            if (lastduration > 200)
                                lastduration -= 100;
                            objs[1] = new Long(lastduration);
                        }
                    }
                }
                if (lyricTracks[i].size() > 1){
                    Object[] objs = (Object[])lyricTracks[i].get(lyricTracks[i].size()-1);
                    long lastduration = lenMillis - ((Long)objs[0]).longValue();
                    objs[1] = new Long(lastduration);
                }
            }
            int biggestTrackIdx = 0;
            int biggestTrackSize = 0;
            for (int i=0; i<lyricTracks.length; i++){
                if (biggestTrackSize < lyricTracks[i].size()){
                    biggestTrackIdx = i;
                    biggestTrackSize = lyricTracks[i].size();
                }
            }
            PrintStream out = new PrintStream(new FileOutputStream(fileNameOut));
            int lirycTrack = biggestTrackIdx;
            out.println("//d³ugoœæ\t"+toTimeString(sequence.getMicrosecondLength()/1000));
            out.println("MILLIS\t"+sequence.getMicrosecondLength()/1000);
            out.println("AUTHOR\t<empty>");
            out.println("TITLE\t<empty>");
            out.println("CREATOR\t<empty>");
            out.println("VERSION\t<empty>");
            for (int i=0; i<lyricTracks.length; i++){
                if (i == biggestTrackIdx)
                    continue;
                for (int j=0; j<lyricTracks[i].size(); j++){
                    Object[] objs = (Object[])lyricTracks[i].get(j);
                    out.println("// "+objs[0]+"\t"+objs[1]+"\t"+objs[2]+"\t");
                }
            }
            for (int j=0; j<lyricTracks[lirycTrack].size(); j++){
                Object[] objs = (Object[])lyricTracks[lirycTrack].get(j);
                String prefix = "";
                if (objs[0].equals(objs[1]))
                    prefix = "// ";
                if (((String)objs[2]).trim().length() == 0)
                    prefix = "// ";
                out.println(prefix+objs[0]+"\t"+objs[1]+"\t0\t"+toOneLine((String)objs[2]));
            }
            
        }
    }
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
        return ret;
    }
}
