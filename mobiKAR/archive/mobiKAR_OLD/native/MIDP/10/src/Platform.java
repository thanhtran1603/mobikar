/*
 * Platform.java
 *
 * Created on 14 listopad 2004, 13:33
 */

import javax.microedition.midlet.*;

/**
 *
 * @author  MiKO
 * @version
 */
public class Platform {
    MIDlet m_midlet = null;
    boolean m_isSupported = false;
    public Platform(MIDlet midlet){
    }
    public boolean isSupported(){
        return false;
    }
    public boolean platformRequest(String url){
        return false;
    }
}
