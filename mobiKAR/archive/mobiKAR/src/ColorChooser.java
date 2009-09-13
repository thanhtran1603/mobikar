/*
 * @(#)ColorChooser.java	1.2 03/01/22
 *
 * Copyright (c) 2000-2003 Sun Microsystems, Inc. All rights reserved.
 * PROPRIETARY/CONFIDENTIAL
 * Use is subject to license terms
 */

import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

/**
 * A Color chooser.  This screen can be used to display and
 * choose colors.  The current color is always available
 * via the getColor and getGrayScale methods.  It can be
 * set with setColor.
 * A palette provides some reuse of colors, the current
 * index in the palette can get set and retrieved.
 * When the chooser is active the user may set the index
 * in the palette, and change the red, green, and blue
 * components.
 * The application using the chooser must add commands
 * to the chooser as appropriate to terminate selection
 * and to change to other screens.
 * The chooser adapts to the available screen size
 * and font sizes.
 */
public class ColorChooser extends Canvas {
    private int width, height;	// Width and height of canvas
    private Font font;
    private int label_w;	// width of "999"
    private int label_h;	// height of "999"
    
    private int gray;
    private int rgbColor;
    private int radix = 10;	// radix to display numbers (10 or 16)
    private int delta = 0x10;	// default increment/decrement
    private int ndx = 0;	// 0 == blue, 1 == green, 2 == red
    private boolean isColor;
    private int numColors;
    private int pndx;
    public int[] palette;
    public int fontFlags;
    Image m_imgBkg = null;
    
    
    public ColorChooser(boolean isColor, int numColors, int[] palette, int fontFlags) {
        this.isColor = isColor;
        this.numColors = numColors;
        this.palette = palette;
        this.fontFlags = fontFlags;
        ndx = -1;
        pndx = 0;
        setColor(palette[pndx]);
        
//	width = getWidth();
//	height = getHeight();
        width = MainMIDlet.m_width;
        height = MainMIDlet.m_height;
        
        font = Font.getFont(Font.FACE_SYSTEM,
                Font.STYLE_PLAIN,
                Font.SIZE_SMALL);
        label_h = font.getHeight();
        label_w = font.stringWidth("999");
        try{
            m_imgBkg = Image.createImage("/bkg.png");
        } catch(Exception e){
            if (MainMIDlet.DEBUG) e.printStackTrace();
        }
    }
    
    /**
     * Select which entry in the Palette to use for the current color.
     * @param index index into the palette; 0..10.
     */
    public boolean setPaletteIndex(int index) {
        if (index >= palette.length)
            return false;
        
        pndx = index;
        setColor(palette[index]);
        return true;
    }
    
    /**
     * Get the current palette index.
     * @return the current index in the palette.
     */
    public int getPaletteIndex() {
        return ndx;
    }
    
    /**
     * Sets the current color to the specified RGB values.
     * @param red The red component of the color being set in range 0-255.
     * @param green The green component of the color being set in range 0-255.
     * @param blue The blue component of the color being set in range 0-255.
     */
    public void setColor(int red, int green, int blue) {
        red   = (red   < 0) ? 0 : (red   & 0xff);
        green = (green < 0) ? 0 : (green & 0xff);
        blue  = (blue  < 0) ? 0 : (blue  & 0xff);
        
        setColor((red << 16) | (green << 8) | blue);
    }
    
    /**
     * Sets the current color to the specified RGB values. All subsequent
     * rendering operations will use this specified color. The RGB value
     * passed in is interpreted with the least significant eight bits
     * giving the blue component, the next eight more significant bits
     * giving the green component, and the next eight more significant
     * bits giving the red component. That is to say, the color component
     * is specified like 0x00RRGGBB.
     * @param RGB The color being set.
     */
    public void setColor(int RGB) {
        rgbColor = RGB & 0x00ffffff;
        palette[pndx] = rgbColor;
        updateGray();
    }
    
    /*
     * Compute the gray value from the RGB value.
     */
    private void updateGray() {
        /*
         * REMIND: change this, if the spec allows it
         *
         * Gray is the value according to HSV.  I think it would
         * make more sense to use NTSC gray, but that's not what
         * is currently in the spec.   -- rib 20 Mar 2000
         */
        gray = Math.max((rgbColor >> 16) & 0xff,
                Math.max((rgbColor >> 8) & 0xff, rgbColor & 0xff));
    }
    
    /**
     * Gets the current color.
     * @return an integer in form 0x00RRGGBB
     * @see #setColor(int, int, int)
     */
    public int getColor() {
        return rgbColor;
    }
    
    /**
     * Gets the red component of the current color.
     * @return integer value in range 0-255
     * @see #setColor(int, int, int)
     */
    public int getRedComponent() {
        return (rgbColor >> 16) & 0xff;
    }
    
    /**
     * Gets the green component of the current color.
     * @return integer value in range 0-255
     * @see #setColor(int, int, int)
     */
    public int getGreenComponent() {
        return (rgbColor >> 8) & 0xff;
    }
    
    /**
     * Gets the blue component of the current color.
     * @return integer value in range 0-255
     * @see #setColor(int, int, int)
     */
    public int getBlueComponent() {
        return rgbColor & 0xff;
    }
    
    /*
     * Get the current grayscale value.
     */
    public int getGrayScale() {
        return gray;
    }
    
    /**
     * Sets the current grayscale.
     * For color the value is used to set each component.
     * @param the value in range 0-255
     */
    public void setGrayScale(int value) {
        setColor(value, value, value);
    }
    
    /**
     * The canvas is being displayed.  Compute the
     * relative placement of items the depend on the screen size.
     */
    protected void showNotify() {
    }
    
    /*
     * Paint the canvas with the current color and controls to change it.
     */
    protected void paint(Graphics g) {
        if (isColor) {
            colorPaint(g);
        } else {
            grayPaint(g);
        }
    }
    
    /**
     * Set the radix used to display numbers.
     * The default is decimal (10).
     */
    public void setRadix(int rad) {
        if (rad != 10 && rad != 16) {
            throw new IllegalArgumentException();
        }
        radix = rad;
        repaint();
    }
    
    /**
     * Get the radix used to display numbers.
     */
    public int getRadix() {
        return radix;
    }
    
    
    /**
     * Set the delta used to increment/decrement.
     * The default is 32.
     */
    public void setDelta(int delta) {
        if (delta > 0 && delta <= 128) {
            this.delta = delta;
        }
    }
    
    /**
     * Get the delta used to increment/decrement.
     */
    public int getDelta() {
        return delta;
    }
    
    /*
     * Use Integer toString to convert.
     * padding may be required.
     */
    private String format(int num) {
        String s = Integer.toString(num, radix);
        if (radix == 10 || s.length() >= 2)
            return s;
        return "0" + s;
    }
    
    static final int BORDER = 1;
    
    private void colorPaint(Graphics g) {
        int face = 0;
        if ((fontFlags & Font.FACE_MONOSPACE) == Font.FACE_MONOSPACE)
            face = Font.FACE_MONOSPACE;
        else if ((fontFlags & Font.FACE_PROPORTIONAL) == Font.FACE_PROPORTIONAL)
            face = Font.FACE_PROPORTIONAL;
        int style = 0;
        if ((fontFlags & Font.STYLE_BOLD) == Font.STYLE_BOLD)
            style |= Font.STYLE_BOLD;
        if ((fontFlags & Font.STYLE_ITALIC) == Font.STYLE_ITALIC)
            style |= Font.STYLE_ITALIC;
        if ((fontFlags & Font.STYLE_UNDERLINED) == Font.STYLE_UNDERLINED)
            style |= Font.STYLE_UNDERLINED;
        int size = 0;
        if ((fontFlags & Font.SIZE_LARGE) == Font.SIZE_LARGE)
            size = Font.SIZE_LARGE;
        else if ((fontFlags & Font.SIZE_SMALL) == Font.SIZE_SMALL)
            size = Font.SIZE_SMALL;
        
        Font f = Font.getFont(face, style, size);
        
        int bar_h = font.getHeight() / 3;
        if (bar_h < 4)
            bar_h = 4;
        
        int sample_x = 0;
        int sample_y = 3 * bar_h;
        int sample_w = width;
        int sample_h = height - sample_y;
        
        // czyszczenie ca³ego ekranu
        g.setColor(0xffffff);
        g.fillRect(0, 0, width, height);
        
        // malowanie t³a
        int background = MainMIDlet.m_instance.m_store.getInteger(Store.INT_BACKGROUND);
        g.setColor(palette[0]);
        g.fillRect(sample_x, sample_y, sample_w, sample_h);
        if ((background & MainMIDlet.FLAG_GRADIENT) == MainMIDlet.FLAG_GRADIENT){
            int color0 = palette[7];
            int color9 = palette[8];
            int red0 = (color0 >> 16) & 0xff;
            int green0 = (color0 >> 8) & 0xff;
            int blue0 = (color0) & 0xff;
            int red9 = (color9 >> 16) & 0xff;
            int green9 = (color9 >> 8) & 0xff;
            int blue9 = (color9) & 0xff;
            final int prec = 16;
            long deltaRed = ((red9 - red0) << prec) / (sample_h);
            long deltaGreen = ((green9 - green0) << prec) / (sample_h);
            long deltaBlue = ((blue9 - blue0) << prec) / (sample_h);
            for (int i=0; i<sample_h; i++){
                int iRed = (int)(red0+((i*deltaRed)>>prec));
                int iGreen = (int)(green0+((i*deltaGreen)>>prec));
                int iBlue = (int)(blue0+((i*deltaBlue)>>prec));
                if (MainMIDlet.DEBUG) System.err.println(" "+iRed+" "+iGreen+" "+iBlue+" ");
                g.setColor(iRed>255?255:(iRed<0?0:iRed), iGreen>255?255:(iGreen<0?0:iGreen), iBlue>255?255:(iBlue<0?0:iBlue));
                g.drawLine(sample_x, (int)sample_y+i, sample_x+sample_w, sample_y+i);
            }
        }
        if ((background & MainMIDlet.FLAG_BACKGROUND) == MainMIDlet.FLAG_BACKGROUND){
            g.drawImage(m_imgBkg, MainMIDlet.m_width >> 1, MainMIDlet.m_height >> 1, g.HCENTER|g.VCENTER);
        }
        
        
        
        // malujemy suwaki
        int cyfrak_x = BORDER * 4 + label_w;
        int bar_x = cyfrak_x + BORDER ;
        int bar_w = width - bar_x - BORDER;
        int bar_y = 0;
        int suwaki_y = bar_y;
        g.setFont(font);
        if (ndx == 0){
            g.setColor(0, 0, 0);
            g.drawString(format(getBlueComponent()), cyfrak_x, suwaki_y,  Graphics.TOP|Graphics.RIGHT);
        }
        int b_w = (bar_w * getBlueComponent()) / 255;
        g.setColor(0, 0, 255);
        g.fillRect(bar_x, bar_y, b_w, bar_h - BORDER);
        g.setColor((ndx == 0) ? 0x000000 : 0x0000FF);
        g.setStrokeStyle((ndx == 0) ? g.DOTTED : g.SOLID);
        g.drawRect(bar_x, bar_y, bar_w - 1, bar_h - BORDER - 1);
        
        bar_y += bar_h;
        if (ndx == 1){
            g.setColor(0, 0, 0);
            g.drawString(format(getGreenComponent()), cyfrak_x, suwaki_y,  Graphics.TOP|Graphics.RIGHT);
        }
        int g_w = (bar_w * getGreenComponent()) / 255;
        g.setColor(0, 255, 0);
        g.fillRect(bar_x, bar_y, g_w, bar_h - BORDER);
        g.setColor((ndx == 1) ? 0x000000 : 0x00FF00);
        g.setStrokeStyle((ndx == 1) ? g.DOTTED : g.SOLID);
        g.drawRect(bar_x, bar_y, bar_w - 1, bar_h - BORDER - 1);
        
        bar_y += bar_h;
        g.setColor(0, 0, 0);
        if (ndx == 2){
            g.setColor(0, 0, 0);
            g.drawString(format(getRedComponent()), cyfrak_x, suwaki_y,  Graphics.TOP|Graphics.RIGHT);
        }
        int r_w = (bar_w * getRedComponent()) / 255;
        g.setColor(255, 0, 0);
        g.fillRect(bar_x, bar_y, r_w, bar_h - BORDER);
        g.setColor((ndx == 2) ? 0x000000 : 0xFF0000);
        g.setStrokeStyle((ndx == 2) ? g.DOTTED : g.SOLID);
        g.drawRect(bar_x, bar_y, bar_w - 1, bar_h - BORDER - 1);
        
        bar_y += bar_h;
        
        // malujemy tubki ;)
        // srednica tubki
        
        int d_tube = 6;
        g.setColor(0xffffff);
        g.fillRect(BORDER, bar_y, BORDER+palette.length* (BORDER+d_tube), d_tube);
        for (int i=0; i<palette.length; i++){
            g.setColor(palette[i]);
            g.fillArc(BORDER+i* (BORDER+d_tube), bar_y, d_tube, d_tube, 0, 360);
            if (i == pndx) {
                g.setColor((ndx < 0) ? 0x000000 : 0x808080);
                g.setStrokeStyle(g.DOTTED);
                g.drawRect(BORDER+i* (BORDER+d_tube), bar_y, d_tube, d_tube);
                g.setStrokeStyle(g.SOLID);
            }
        }
        
        // malowanie tekstów
        // obliczam linie na których po³o¿ê tekst
        int linia0 = sample_y + d_tube + BORDER;
        int linia1 = linia0 + BORDER + f.getHeight();
        // obliczam po³o¿enia tekstów
        int xText0 = sample_x;
        int xText1 = sample_x + f.stringWidth(" "+MainMIDlet.m_instance.m_lang.m_dic[Lang.TEXT_OLD]);
        int xText2 = sample_x;
        int xText3 = sample_x + f.stringWidth(" "+MainMIDlet.m_instance.m_lang.m_dic[Lang.TEXT_NEW0]);
        
        g.setFont(f);
        //palette = { background, after, active, foreground0, foreground1, outline, shadow, gradient0, gradient9 };
        g.setColor(palette[6]); // shadow
        g.drawString(MainMIDlet.m_instance.m_lang.m_dic[Lang.TEXT_OLD], xText0+2, linia0+2, g.LEFT|g.TOP);
        g.setColor(palette[5]); // outline
        g.drawString(MainMIDlet.m_instance.m_lang.m_dic[Lang.TEXT_OLD], xText0-1, linia0-1, g.LEFT|g.TOP);
        g.drawString(MainMIDlet.m_instance.m_lang.m_dic[Lang.TEXT_OLD], xText0+1, linia0+1, g.LEFT|g.TOP);
        g.setColor(palette[1]); // after
        g.drawString(MainMIDlet.m_instance.m_lang.m_dic[Lang.TEXT_OLD], xText0, linia0, g.LEFT|g.TOP);
        
        g.setColor(palette[6]); // shadow
        g.drawString(MainMIDlet.m_instance.m_lang.m_dic[Lang.TEXT_ACTIVE], xText1+2, linia0+2, g.LEFT|g.TOP);
        g.setColor(palette[5]); // outline
        g.drawString(MainMIDlet.m_instance.m_lang.m_dic[Lang.TEXT_ACTIVE], xText1-1, linia0-1, g.LEFT|g.TOP);
        g.drawString(MainMIDlet.m_instance.m_lang.m_dic[Lang.TEXT_ACTIVE], xText1+1, linia0+1, g.LEFT|g.TOP);
        g.setColor(palette[2]); // active
        g.drawString(MainMIDlet.m_instance.m_lang.m_dic[Lang.TEXT_ACTIVE], xText1, linia0, g.LEFT|g.TOP);
        
        g.setColor(palette[6]); // shadow
        g.drawString(MainMIDlet.m_instance.m_lang.m_dic[Lang.TEXT_NEW0], xText2+2, linia1+2, g.LEFT|g.TOP);
        g.setColor(palette[5]); // outline
        g.drawString(MainMIDlet.m_instance.m_lang.m_dic[Lang.TEXT_NEW0], xText2-1, linia1-1, g.LEFT|g.TOP);
        g.drawString(MainMIDlet.m_instance.m_lang.m_dic[Lang.TEXT_NEW0], xText2+1, linia1+1, g.LEFT|g.TOP);
        g.setColor(palette[3]); // foreground0
        g.drawString(MainMIDlet.m_instance.m_lang.m_dic[Lang.TEXT_NEW0], xText2, linia1, g.LEFT|g.TOP);
        
        g.setColor(palette[6]); // shadow
        g.drawString(MainMIDlet.m_instance.m_lang.m_dic[Lang.TEXT_NEW1], xText3+2, linia1+2, g.LEFT|g.TOP);
        g.setColor(palette[5]); // outline
        g.drawString(MainMIDlet.m_instance.m_lang.m_dic[Lang.TEXT_NEW1], xText3-1, linia1-1, g.LEFT|g.TOP);
        g.drawString(MainMIDlet.m_instance.m_lang.m_dic[Lang.TEXT_NEW1], xText3+1, linia1+1, g.LEFT|g.TOP);
        g.setColor(palette[4]); // foreground1
        g.drawString(MainMIDlet.m_instance.m_lang.m_dic[Lang.TEXT_NEW1], xText3, linia1, g.LEFT|g.TOP);
        // ...
        
    }
    
    private void grayPaint(Graphics g) {
        int sample_w = width;
        int sample_h = height / 2;
        int g_y = height / 2;
        int g_w = width - (label_w + BORDER);
        int g_h = label_h + BORDER;
        
        // Fill the background
        g.setGrayScale(0xff);
        g.fillRect(0, 0, width, height);
        
        // Fill in the gray sample
        g.setGrayScale(gray);
        g.fillRect(0, 0, sample_w, sample_h);
        g.setGrayScale(0);
        g.drawRect(0, 0, sample_w-1, sample_h-1);
        
        // Fill in the gray bar
        g.setGrayScale(0);
        g.fillRect(label_w+BORDER, g_y + BORDER, (g_w * gray) / 255, g_h);
        g.drawRect(label_w+BORDER, g_y + BORDER, g_w-1, g_h-1);
        g.drawString(format(gray), label_w,
                g_y+BORDER + g_h,  Graphics.BOTTOM|Graphics.RIGHT);
    }
    
    /*
     * Handle repeat as in pressed.
     */
    public void keyRepeated(int key) {
        keyPressed(key);
    }
    
    /*
     * Left and Right are used to change which color bar to change
     * Up and Down are used to increase/decrease the value of that bar.
     */
    protected void keyPressed(int key) {
        int action = getGameAction(key);
        int dir = 0;
        switch (action) {
            case RIGHT: dir += 1; break;
            case LEFT: dir -= 1; break;
            case UP: ndx -= 1; break;
            case DOWN: ndx += 1; break;
            default:
                return;		// nothing we recognize, exit
        }
        
        // Gray scale event handling is simpler than color
        if (isColor) {
            // Limit selection to r,g,b and palette
            if (ndx < 0)
                ndx = 0;
            if (ndx > 3)
                ndx = 3;
            if (ndx < 3) {
                int v = (rgbColor >> (ndx*8)) & 0xff;
                v += dir * delta;
                if (v < 0)
                    v = 0;
                if (v > 255)
                    v = 255;
                int mask = 0xff << (ndx*8);
                rgbColor = (rgbColor & ~mask) | v << (ndx*8);
                palette[pndx] = rgbColor;
            } else {
                pndx += dir;
                if (pndx < 0)
                    pndx = 0;
                if (pndx >= palette.length)
                    pndx = palette.length-1;
                rgbColor = palette[pndx];
            }
        } else {
            /*
             * Gray scale; multiple dir and add to gray
             * ignore (up/down) there is only one thing to select.
             */
            gray += dir * delta;
            if (gray < 0)
                gray = 0;
            if (gray > 255)
                gray = 255;
        }
        
        repaint();
    }
}
