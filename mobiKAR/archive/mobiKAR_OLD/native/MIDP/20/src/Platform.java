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
        m_midlet = midlet;
        String profiles = System.getProperty("microedition.profiles");
        if (MainMIDlet.DEBUG) System.out.println("profiles:"+profiles);
        if (profiles != null && (profiles.indexOf("2.0") != -1))
            m_isSupported = true;
    }
    public boolean isSupported(){
        return m_isSupported;
    }
    public boolean platformRequest(String url){
        boolean ret = false;
        try{
            if (m_isSupported){
                m_midlet.platformRequest(url);
                ret = true;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return ret;
    }
}
