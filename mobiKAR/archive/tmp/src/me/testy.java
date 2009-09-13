/*
 * testy.java
 *
 * Created on 13 lipiec 2006, 10:01
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package me;

import java.util.Hashtable;
import java.util.Random;

/**
 *
 * @author a
 */
public class testy {
    
    /** Creates a new instance of testy */
    public testy() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
//        Random rnd = new Random();
//        for (int i=0; i<100; i++){
//            System.out.println(i+": "+(Math.abs(rnd.nextInt()) % 10));
//        }
        loadVehicles();
    }
    static int onWayCars = 1023;
    static void loadVehicles(){
        byte vehiclesMax = 0;
        for (int i=0; i<64; i++){
            long bit = 1L<<i;
            if ((onWayCars & bit) == bit){
                vehiclesMax += 2;
            }
        }
        System.out.println("vehiclesMax: "+vehiclesMax);
        Object vehicleImages[] = new Object[vehiclesMax << 1];
        for (int i=0; i<vehiclesMax; i++){
            long bit = 1L<<i;
            if ((onWayCars & bit) == bit){
                for (int j=0; j<2; j++){
                    String name = "v"+i+j;
                    int idx = (i<<1)+j;
                    vehicleImages[idx] = name;
                    System.out.println("vehicleImages["+idx+"] = MainMidlet.loadImage(/"+name+".png))");
                }
            }
        }
    }
    
    
}
