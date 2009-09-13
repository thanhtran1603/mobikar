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
//#if MIDP_20        
        m_isSupported = true;
//#endif
    }
    public boolean isSupported(){
        return m_isSupported;
    }
    public boolean platformRequest(String url){
        boolean ret = false;
//#if MIDP_20        
        try{
            if (m_isSupported){
                m_midlet.platformRequest(url);
                ret = true;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
//#endif
        return ret;
    }
}
