/*
 * Txt2mLYR.java
 *
 * Created on 19 marzec 2005, 15:02
 */

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 *
 * @author a
 */
public class Txt2mLYR {
    
    /** Creates a new instance of Txt2mLYR */
    public Txt2mLYR() {
    }
    
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        String fileNameIn = null;
        String fileNameOut = null;
        if (args.length > 0){
            fileNameIn = args[0];
            fileNameOut = fileNameIn+".mlyr";
        }
        if (args.length > 1){
            fileNameOut = args[1];
        }
        if (fileNameIn == null || fileNameOut == null){
            System.out.println("Parametry plik_wejœciowy.txt plik_wyjœciowy.mlyr");
            System.out.println("plik_wejœciowy - plik tekstowy z rozpisan¹ piosenk¹");
            System.out.println("plik_wyjœciowy - plik binarny ze skompilowan¹ piosenk¹");
            return;
        }

        String title = "title";
        String music = "music";
        String lyrics = "lyrics";
        String artist = "artist";
        String creator = "creator";
        String version = "version";
        String note = "note";
        String text  = "";
        long milis = 0;
        Collection idxs = new ArrayList();
        BufferedReader br = new BufferedReader(new FileReader(fileNameIn));
        String line = null;
        while((line = br.readLine()) != null){
            String[] parts = line.split("\t");
            if (parts.length == 2){
                if (parts[0].startsWith("TITLE")){
                    title = parts[1];
                }
                if (parts[0].startsWith("ARTIST")){
                    artist = parts[1];
                }
                if (parts[0].startsWith("MUSIC")){
                    music = parts[1];
                }
                if (parts[0].startsWith("LYRICS")){
                    lyrics = parts[1];
                }
                if (parts[0].startsWith("CREATOR")){
                    creator = parts[1];
                }
                if (parts[0].startsWith("VERSION")){
                    version = parts[1];
                }
                if (parts[0].startsWith("NOTE")){
                    note = parts[1];
                }
                if (parts[0].startsWith("MILLIS")){
                    milis = Long.parseLong(parts[1]);
                }
            }
            if (parts.length == 4){
                try{
                    long timeStart = Long.parseLong(parts[0]);
                    long timeLong = Long.parseLong(parts[1]);
                    byte singer = Byte.parseByte(parts[2]);
                    String sylabe = parts[3];
                    int sylIdx = text.length();
                    int sylLen = sylabe.length();
                    text += sylabe;
                    long pack = 0;
                    // todo: wstawiæ jeden bit
                    pack |= timeStart << (64 - 24);
                    pack |= timeLong << (64 - 24 - 16);
                    pack |= sylIdx << (64 - 24 - 16 - 16);
                    pack |= sylLen << (64 - 24 - 16 - 16 - 6);
                    pack |= singer << (64 - 24 - 16 - 16 - 6 - 1);
                    // zosta³o wolnych bitów: 1
                    idxs.add(new Long(pack));
                } catch (Exception e){
                }
            }
        }
        br.close();
        DataOutputStream dos = new DataOutputStream(new FileOutputStream(fileNameOut));
        ByteArrayOutputStream baosGlobal = new ByteArrayOutputStream();
        DataOutputStream bufGlobal = new DataOutputStream(baosGlobal);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream buf = new DataOutputStream(baos);
        byte[] bytes = null;
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
        buf.writeUTF(title);
        bytes = baos.toByteArray();
        bufGlobal.writeInt(TAG_TITLE);
        bufGlobal.writeInt(bytes.length);
        bufGlobal.write(bytes);
        
        baos.reset();
        buf.writeUTF(music);
        bytes = baos.toByteArray();
        bufGlobal.writeInt(TAG_MUSIC);
        bufGlobal.writeInt(bytes.length);
        bufGlobal.write(bytes);
        
        baos.reset();
        buf.writeUTF(lyrics);
        bytes = baos.toByteArray();
        bufGlobal.writeInt(TAG_LYRICS);
        bufGlobal.writeInt(bytes.length);
        bufGlobal.write(bytes);
        
        baos.reset();
        buf.writeUTF(artist);
        bytes = baos.toByteArray();
        bufGlobal.writeInt(TAG_ARTIST);
        bufGlobal.writeInt(bytes.length);
        bufGlobal.write(bytes);
        
        baos.reset();
        buf.writeUTF(creator);
        bytes = baos.toByteArray();
        baos.reset();
        bufGlobal.writeInt(TAG_CREATOR);
        bufGlobal.writeInt(bytes.length);
        bufGlobal.write(bytes);
        
        baos.reset();
        buf.writeUTF(version);
        bytes = baos.toByteArray();
        bufGlobal.writeInt(TAG_VERSION);
        bufGlobal.writeInt(bytes.length);
        bufGlobal.write(bytes);

        baos.reset();
        buf.writeUTF(note);
        bytes = baos.toByteArray();
        bufGlobal.writeInt(TAG_NOTE);
        bufGlobal.writeInt(bytes.length);
        bufGlobal.write(bytes);

        baos.reset();
        buf.writeLong(milis);
        bytes = baos.toByteArray();
        bufGlobal.writeInt(TAG_MILLIS);
        bufGlobal.writeInt(bytes.length);
        bufGlobal.write(bytes);

        baos.reset();
        buf.writeUTF(text);
        bytes = baos.toByteArray();
        bufGlobal.writeInt(TAG_TEXT);
        bufGlobal.writeInt(bytes.length);
        bufGlobal.write(bytes);

        baos.reset();
        buf.writeInt(idxs.size());
        for (Iterator it=idxs.iterator(); it.hasNext(); )
            buf.writeLong(((Long)it.next()).longValue());
        bytes = baos.toByteArray();
        bufGlobal.writeInt(TAG_IDXS);
        bufGlobal.writeInt(bytes.length);
        bufGlobal.write(bytes);

        dos.writeInt(TAG_MLYR);
        bytes = baosGlobal.toByteArray();
        dos.writeInt(bytes.length);
        dos.write(bytes);
        dos.close();
    }
    
}
