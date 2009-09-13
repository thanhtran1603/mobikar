/*
 * WriteLocaleList.java
 *
 * Created on 9 paüdziernik 2004, 18:18
 */
import java.io.*;

/**
 *
 * @author  MiKO
 */
public class TextUtfConventer {
    // detect /var/str.tab /var/str.bin /var/str.txt
    /** Creates a new instance of WriteLocaleList */
    public TextUtfConventer() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception{
        String par_operation = null;
        String par_table = null;
        String par_input = null;
        String par_output = null;
        
        if (args.length != 4){
            System.err.println("params: operation table input output");
            System.err.println("  operation: utf8 - convert to UTF-8");
            System.err.println("  operation: text - convert to text");
            System.err.println("  operation: detect - detects Strings");
            System.err.println("  table: conversion table");
            System.err.println("  input: file input");
            System.err.println("  output: file output");
            System.exit(10);
        }
        par_operation = args[0];
        par_table = args[1];
        par_input = args[2];
        par_output = args[3];
        if ("detect".equals(par_operation)){
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            InputStream is = new FileInputStream(par_input);
            int c;
            while((c=is.read())!= -1)
                baos.write(c);
            DataInputStream inputUTF8 = new DataInputStream(
            new ByteArrayInputStream(baos.toByteArray()));
            BufferedWriter outputTable = new BufferedWriter(new FileWriter(par_table));
            while(true){
                inputUTF8.mark(Integer.MAX_VALUE);
                try{
                    short size = inputUTF8.readShort();
                    inputUTF8.reset();
                    String text = inputUTF8.readUTF();
                    if (text.length() == 0)
                        throw new UTFDataFormatException("Empty string");
                    System.out.println("size:"+size+", isLetterOrDigit: "+Character.isLetterOrDigit(text.charAt(0))+", text["+text+"]");
                    System.out.print("Text is good [y|n]?");
                    c = System.in.read();
                    System.in.skip(Integer.MAX_VALUE);
                    if (c== 'y' || c == 'Y'){
                        outputTable.write("String\n");
                        System.out.println(par_table+": String "+text);
                    }
                    else{
                        throw new UTFDataFormatException("Not accepted");
                    }
                } catch (UTFDataFormatException utfe){
                    inputUTF8.reset();
                    byte b = inputUTF8.readByte();
                    outputTable.write("Byte\n");
                    System.out.println(par_table+": Byte 0x"+Integer.toHexString(b));
                }
                catch (EOFException eofe){
                    inputUTF8.reset();
                    try{
                        byte b = inputUTF8.readByte();
                        outputTable.write("Byte\n");
                        System.out.println(par_table+": Byte 0x"+Integer.toHexString(b));
                    } catch (Exception e){
                        break;
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                    System.exit(20);
                }
            }
            outputTable.close();
            inputUTF8.close();
            par_operation = "text";
        }
        boolean isToUTF8 = "utf8".equals(par_operation);
        BufferedReader inputTable = new BufferedReader(new FileReader(par_table));
        BufferedReader inputText = null;
        DataInputStream inputUTF8 = null;
        BufferedWriter outputText = null;
        DataOutputStream outputUTF8 = null;
        if (isToUTF8){
            inputText = new BufferedReader(new FileReader(par_input));
            outputUTF8 = new DataOutputStream(new FileOutputStream(par_output));
        }
        else{
            inputUTF8 = new DataInputStream(new FileInputStream(par_input));
            outputText = new BufferedWriter(new FileWriter(par_output));
        }
        String javaType = null;
        while((javaType = inputTable.readLine()) != null){
            if (javaType.indexOf("//") != -1)
                javaType = javaType.substring(0, javaType.indexOf("//"));
            javaType = javaType.trim();
            if ("Byte".equals(javaType)){
                if (isToUTF8){
                    outputUTF8.writeByte(Byte.parseByte(inputText.readLine()));
                }
                else{
                    outputText.write(inputUTF8.readByte()+"\n");
                }
            }
            if ("Integer".equals(javaType)){
                if (isToUTF8){
                    outputUTF8.writeInt(Integer.parseInt(inputText.readLine()));
                }
                else{
                    outputText.write(inputUTF8.readInt()+"\n");
                }
            }
            else  if ("String".equals(javaType)){
                if (isToUTF8){
                    outputUTF8.writeUTF(inputText.readLine());
                }
                else{
                    outputText.write(inputUTF8.readUTF()+"\n");
                }
            }
        }
        inputTable.close();
        if (inputText != null)
            inputText.close();
        if (inputUTF8 != null)
            inputUTF8.close();
        if (outputText != null)
            outputText.close();
        if (outputUTF8 != null)
            outputUTF8.close();
    }
    
    
    
}
