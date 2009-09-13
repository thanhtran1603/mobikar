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
    Image m_logo = null;
    Image m_web = null;
    int m_webY;
    
    /** Creates a new instance of Intro */
    public Intro(Display display) {
        m_stop = false;
        try{
            m_logo = Image.createImage("/l.png");
            m_web = Image.createImage("/web.png");
        } catch (Exception e){
            if (MainMIDlet.DEBUG) e.printStackTrace();
        }
        display.setCurrent(this);
        new Thread(this).start();
    }
    
    public void paint(Graphics g){
        MainMIDlet.check_Point(AlertType.INFO);
        if (MainMIDlet.m_width < 0 || MainMIDlet.m_height < 0){
            MainMIDlet.m_width = g.getClipWidth();
            MainMIDlet.m_height = g.getClipHeight();
            m_webY = MainMIDlet.m_height;
        }
        g.setColor(0x004050);
        g.fillRect(0,0, MainMIDlet.m_width, MainMIDlet.m_height);
        if (m_logo != null){
            if (m_logo.getHeight() <= MainMIDlet.m_height){
                g.drawImage(m_logo, MainMIDlet.m_width >> 1, MainMIDlet.m_height >> 1, g.HCENTER|g.VCENTER);
            } else{
                g.drawImage(m_logo, MainMIDlet.m_width >> 1, 0, g.HCENTER|g.TOP);
            }
        }
        if (m_web != null && m_webY < MainMIDlet.m_height){
            g.drawImage(m_web, MainMIDlet.m_width, m_webY, g.RIGHT|g.TOP);
        }
    }
    
    public void run() {
        m_webY = MainMIDlet.m_height;
        while (! m_stop){
            try{
                repaint();
                if (MainMIDlet.DEBUG) System.err.println("repaint();");
                Thread.sleep(500);
                if (MainMIDlet.DEBUG) System.err.println("Thread.sleep(1000);");
                repaint();
                int webHeight = m_web.getHeight();
                for (int i=0; i<webHeight; i++){
                    m_webY--;
                    repaint();
                    Thread.sleep(50);
                }
                Thread.sleep(2500);
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
