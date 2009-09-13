/*
 * Foo.java
 *
 * Created on 27 luty 2006, 20:10
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/**
 *
 * @author a
 */

/** Creates a new instance of Foo */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Foo {
    
    public static void main(String[] args){
        //System.getProperties().setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
        Log log = LogFactory.getLog(Foo.class);
        System.out.println("log:"+log);
        log.fatal("fatal");
        log.error("error");
        log.warn("warn");
        log.info("info");
        log.debug("debug");
        log.trace("trace");
    }
}
