/*
 * Main.java
 *
 * Created on 20 listopad 2004, 16:58
 */

package karparser;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
/**
 *
 * @author  MiKO
 */


public class Main {
    
    /** Creates a new instance of Main */
    public Main() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception{
        
//        InputStream is = new BufferedInputStream(new FileInputStream("piosenka.kar"));
        InputStream is = new BufferedInputStream(new FileInputStream("DAAB-Ogrodu_serce.kar"));
        Sequence sequence = (new StandardMidiFileReader()).getSequence(is);
        System.out.println("Length: "+sequence.getMicrosecondLength()/1000);
        MidiUtils.TempoCache tempoCache = new MidiUtils.TempoCache();
        Track [] tracks = sequence.getTracks();
        if ( tracks != null ) {
            for ( int i = 0; i < tracks.length; i++ ) {
                System.out.println( "Track " + i + ":" );
                Track track = tracks[ i ];
                for ( int j = 0; j < track.size(); j++ ) {
                    MidiEvent event = track.get( j );
                    MidiMessage msg = event.getMessage();
                    if (msg instanceof MetaMessage){
                        MetaMessage meta = (MetaMessage)msg;
                        int type = meta.getType();
                        if (type != 5)
                            continue;
//                        System.out.println( type + " "+ event.getTick()
//                        + ", " + MidiUtils.tick2microsecond(sequence, event.getTick(), tempoCache)
//                        + ": "+ new String(smooth(meta.getData())));
                        System.out.println( MidiUtils.tick2microsecond(sequence, event.getTick(), tempoCache) / 1000
                        + "\t"+ new String(smooth(meta.getData()))+"");
                    }
                    
                } // for
            } // for
        } // if
        
        
    }
    private static byte[] smooth(byte[] data) {
        int i = 0;
        for (; i < data.length && data[i] != 0; i++)
            ;
        if (i == data.length)
            return data;
        
        
        byte[] data1 = new byte[i];
        for (int j = 0; j < i; ++j) {
            if(Character.isISOControl((char) data[j])) {
                data1[j] = 0x20;
            } else {
                data1[j] = data[j];
            }
            
            //data1[j] = data[j] < 0x20 ? data1[j] = 0x20 : data[j];
        }
        
        return data1;
    }
    
}
