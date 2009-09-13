/*
 * mLYR2Txt.java
 *
 * Created on 19 marzec 2005, 16:56
 */

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;

/**
 *
 * @author a
 */
public class mLYR2Txt {
    
    static final int TAG_MLYR = ('m'<<24)|('L'<<16)|('Y'<<8)|('R');
    static final int TAG_TITLE = ('T'<<24)|('I'<<16)|('T'<<8)|('L');
    static final int TAG_ARTIST = ('A'<<24)|('R'<<16)|('T'<<8)|('I');
    static final int TAG_MUSIC = ('M'<<24)|('U'<<16)|('S'<<8)|('I');
    static final int TAG_LYRICS = ('L'<<24)|('Y'<<16)|('R'<<8)|('I');
    static final int TAG_CREATOR = ('C'<<24)|('R'<<16)|('E'<<8)|('A');
    static final int TAG_VERSION = ('V'<<24)|('E'<<16)|('R'<<8)|('$');
    static final int TAG_NOTE = ('N'<<24)|('O'<<16)|('T'<<8)|('E');
    static final int TAG_MILLIS = ('M'<<24)|('S'<<16)|('E'<<8)|('K');
    static final int TAG_TEXT = ('T'<<24)|('E'<<16)|('X'<<8)|('T');
    static final int TAG_IDXS = ('I'<<24)|('D'<<16)|('X'<<8)|('S');
    
    /** Creates a new instance of mLYR2Txt */
    public mLYR2Txt() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        String fileNameIn = null;
        String fileNameOut = null;
        PrintStream out = System.out;
        int par_milis = -1;
        int opt = 0;
        if (args.length > 0){
            if ("--milis".equals(args[opt]) || "-m".equals(args[opt])){
                par_milis = Integer.parseInt(args[++opt]);
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
            System.out.println("Parametry -m plik_wejœciowy.mlyr plik_wyjœciowy.txt");
            System.out.println("m -wymuszenie zmiany tempa piosenki");
            return;
        }
        
        // TODO code application logic here
        DataInputStream dis = new DataInputStream(new FileInputStream(fileNameIn));
        
        int tag = dis.readInt();
        int size = dis.readInt();
        if (tag != TAG_MLYR){
            System.out.println("To niejest format mLYR");
            return;
        }
        String title = "title";
        String music = "music";
        String lyrics = "lyrics";
        String artist = "artist";
        String creator = "creator";
        String version = "version";
        String note = "note";
        long milis  = 0;
        String text  = "";
        long[] packs = null;
        do{
            tag = dis.readInt();
            size = dis.readInt();
            if (tag == TAG_TITLE){
                title = dis.readUTF();
            } else if (tag == TAG_ARTIST){
                artist = dis.readUTF();
            } else if (tag == TAG_MUSIC){
                music = dis.readUTF();
            } else if (tag == TAG_LYRICS){
                lyrics = dis.readUTF();
            } else if (tag == TAG_CREATOR){
                creator = dis.readUTF();
            } else if (tag == TAG_VERSION){
                version = dis.readUTF();
            } else if (tag == TAG_NOTE){
                note = dis.readUTF();
            } else if (tag == TAG_MILLIS){
                milis = dis.readLong();
            } else if (tag == TAG_TEXT){
                text = dis.readUTF();
            } else if (tag == TAG_IDXS){
                int idxSize = dis.readInt();
                packs = new long[idxSize];
                for(int i=0; i<packs.length; i++)
                    packs[i] = dis.readLong();
                break;
            } else{
                dis.skip(size);
            }
        } while(true);
        double multipe = 1;
        if (par_milis > 0){
            multipe = 1.0 * par_milis / milis;
        }
        dis.close();
        if (par_milis > 0){
            out.println("// ORG MILLIS\t"+milis);
        }
        out.println("MILLIS\t"+(int)(milis * multipe));
        out.println("TITLE\t"+title);
        out.println("ARTIST\t"+artist);
        out.println("MUSIC\t"+music);
        out.println("LYRICS\t"+lyrics);
        out.println("CREATOR\t"+creator);
        out.println("VERSION\t"+version);
        out.println("NOTE\t"+note);
        for(int i=0; i<packs.length; i++){
            int timeStart = (int)((packs[i] >> (64 - 24)) & ~(-1 << 24));
            int timeLong =  (int)((packs[i] >> (64 - 24 - 16)) & ~(-1 << 16));
            int sylIdx =    (int)((packs[i] >> (64 - 24 - 16 - 16)) & ~(-1 << 16));
            int sylLen =    (int)((packs[i] >> (64 - 24 - 16 - 16 - 6)) & ~(-1 << 6));
            int singer =    (int)((packs[i] >> (64 - 24 - 16 - 16 - 6 - 1)) & ~(-1 << 1));
            
            out.println((int)(timeStart * multipe)+"\t"+timeLong+"\t"+singer+"\t"+text.substring(sylIdx, sylIdx+sylLen));
        }
    }
    
}
