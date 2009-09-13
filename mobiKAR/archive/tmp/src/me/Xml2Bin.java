/*
 * Xml2Bin.java
 *
 * Created on 19 lipiec 2006, 21:40
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package me;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author a
 */
public class Xml2Bin {
    
    /** Creates a new instance of Xml2Bin */
    public Xml2Bin() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        String filenameIn = "/projects/java/ME/HighwaySurvival/tmp/level_3.xml";
        String filenameOut = "/projects/java/ME/HighwaySurvival/res/lev3";
        if (args.length > 0){
            filenameIn = args[0];
        }
        if (args.length > 1){
            filenameOut = args[1];
        }
        DataOutputStream dos = new DataOutputStream(new FileOutputStream(filenameOut));
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new FileInputStream(filenameIn));
        Node node_root = document.getDocumentElement();
        NodeList list_root = node_root.getChildNodes();
        for(int i = 0; i < list_root.getLength(); i++) {
            Node node_item = list_root.item(i);
            String javaType = node_item.getNodeName();
            int radix = 10;
            NamedNodeMap attrs = node_item.getAttributes();
            if (attrs != null){
                for(int k = 0; k < attrs.getLength(); k++) {
                    Attr attr = (Attr)attrs.item(k);
                    String key = attr.getNodeName();
                    String val = attr.getNodeValue();
                    if("radix".equals(key))
                        radix = Integer.parseInt(val);
                }
            }
            NodeList list_item = node_item.getChildNodes();
            for(int j = 0; j < list_item.getLength(); j++){
                Node node_value = list_item.item(j);
                if (node_value.getNodeType() == Node.TEXT_NODE){
                    String value = node_value.getNodeValue();
                    System.out.println("javaType:"+javaType+", value:\""+value+"\"");
                    if ("string".equals(javaType)){
                        dos.writeUTF(value);
                    } else if ("long".equals(javaType)){
                        long val = Long.parseLong(value.trim(), radix);
                        dos.writeLong(val);
                    } else if ("integer".equals(javaType)){
                        int val = Integer.parseInt(value.trim(), radix);
                        dos.writeInt(val);
                    } else if ("short".equals(javaType)){
                        short val = Short.parseShort(value.trim(), radix);
                        dos.writeShort(val);
                    } else if ("byte".equals(javaType)){
                        byte val = Byte.parseByte(value.trim(), radix);
                        dos.writeByte(val);
                    }
                }
            }
            
        }
        dos.flush();
        dos.close();
    }
}
