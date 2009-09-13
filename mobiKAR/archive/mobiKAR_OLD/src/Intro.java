/*
 * Intro.java
 *
 * Created on 26 wrzesieñ 2004, 23:00
 */

import javax.microedition.lcdui.*;

/**
 *
 * @author  MiKO
 */
public class Intro extends Canvas implements Runnable{
    private boolean m_stop;
    
    /** Creates a new instance of Intro */
    public Intro(Display display) {
        m_stop = false;
        display.setCurrent(this);
        new Thread(this).start();
    }
    
    public void paint(Graphics g){
        g.setColor(0x0000ff);
        g.drawString("mobiKAR", g.getClipWidth()>>1, g.getClipHeight()/3, g.TOP|g.LEFT);
        g.setColor(0xff0000);
        g.drawString("mobi", g.getClipWidth()>>1, g.getClipHeight()/3, g.TOP|g.LEFT);
    }
    
    public void run() {
        while (! m_stop){
            try{
                repaint();
                if (MainMIDlet.DEBUG) System.err.println("repaint();");
                Thread.sleep(1000);
                if (MainMIDlet.DEBUG) System.err.println("Thread.sleep(1000);");
            } catch (Exception e){
                if (MainMIDlet.DEBUG) System.err.println(e.toString());
            }
            m_stop = true;
            if (MainMIDlet.DEBUG) System.err.println("m_stop = true;");
        }
        if (MainMIDlet.DEBUG) System.err.println("MainMIDlet.m_instance:"+MainMIDlet.m_instance);
        MainMIDlet.m_instance.startUI();
    }
    
}
