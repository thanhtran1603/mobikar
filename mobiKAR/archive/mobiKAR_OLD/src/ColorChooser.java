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
    private int delta = 0x20;	// default increment/decrement
    private int ndx = 0;	// 0 == blue, 1 == green, 2 == red
    private boolean isColor;
    private int numColors;
    private int pndx;
    public int[] palette;
    public int fontFlags;
    
    
    public ColorChooser(boolean isColor, int numColors, int[] palette, int fontFlags) {
	this.isColor = isColor;
	this.numColors = numColors;
        this.palette = palette;
        this.fontFlags = fontFlags;
	ndx = -1;
	pndx = 0;
	setColor(palette[pndx]);

	width = getWidth();
	height = getHeight();

	font = Font.getDefaultFont();
	label_h = font.getHeight();
	if (label_h > (height / 6)) {
	    font = Font.getFont(Font.FACE_SYSTEM,
				Font.STYLE_PLAIN,
				Font.SIZE_SMALL);
	    label_h = font.getHeight();
	}
	label_w = font.stringWidth("999");
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
        g.setColor(0xffffff);
	g.fillRect(0, 0, width, height);
        
	int sample_x = 0;
	int sample_y = 0;
	int sample_w = width;
	int sample_h = height - (BORDER + label_h)* 3 ;

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
        g.setFont(f);
        //palette = { background, after, active, foreground, outline, shadow };
	g.setColor(palette[0]);
	g.fillRect(sample_x, sample_y, sample_w, sample_h);
        g.setColor(palette[4]);
        g.drawString(MainMIDlet.m_instance.m_lang.m_dic[Lang.TEXT_AFTER], (sample_w>>2)-1, (sample_h>>2)-1, g.HCENTER|g.TOP);
        g.drawString(MainMIDlet.m_instance.m_lang.m_dic[Lang.TEXT_AFTER], (sample_w>>2)+1, (sample_h>>2)+1, g.HCENTER|g.TOP);
        g.setColor(palette[1]);
        g.drawString(MainMIDlet.m_instance.m_lang.m_dic[Lang.TEXT_AFTER], sample_w>>2, sample_h>>2, g.HCENTER|g.TOP);
        g.setColor(palette[5]);
        g.drawString(MainMIDlet.m_instance.m_lang.m_dic[Lang.TEXT_ACTIVE], (sample_w>>1)+1, (sample_h>>1)+1, g.HCENTER|g.BOTTOM);
        g.setColor(palette[2]);
        g.drawString(MainMIDlet.m_instance.m_lang.m_dic[Lang.TEXT_ACTIVE], sample_w>>1, sample_h>>1, g.HCENTER|g.BOTTOM);
        g.setColor(palette[3]);
        g.drawString(MainMIDlet.m_instance.m_lang.m_dic[Lang.TEXT_BEFORE], 3*sample_w>>2, 3*sample_h>>2, g.HCENTER|g.BOTTOM);
        
        g.setFont(font);
        // na poczatek malujemy tubki ;)
	// srednica tubki
        int d_tube = label_h;
        int cyfrak_x = BORDER * 4 + label_w;
        int bar_x = cyfrak_x + BORDER ;
        int bar_w = width - bar_x - BORDER;
        int bar_h = label_h + BORDER;
        int bar_y = sample_y+sample_h - d_tube;
        g.setColor(0xffffff);
	g.fillRect(BORDER, bar_y, BORDER+palette.length* (BORDER+d_tube), d_tube);
        for (int i=0; i<palette.length; i++){
            g.setColor(palette[i]);
            g.fillArc(BORDER+i* (BORDER+d_tube), bar_y, d_tube, d_tube, 0, 360);
            if (i == pndx) {
                g.setColor((ndx < 0) ? 0x000000 : 0x808080);
                g.drawRect(BORDER+i* (BORDER+d_tube), bar_y, d_tube, d_tube);
            }
        }
        /*
        g.setColor(palette[0]);
        g.fillArc(BORDER, bar_y, d_tube, d_tube, 0, 360);
        if (0 == pndx) {
            g.setColor((ndx < 0) ? 0x000000 : 0x808080);
            g.drawRect(BORDER, bar_y, d_tube, d_tube);
        }
        g.setColor(palette[1]);
        g.fillArc(BORDER+(BORDER+d_tube), bar_y, d_tube, d_tube, 0, 360);
        if (1 == pndx) {
            g.setColor((ndx < 0) ? 0x000000 : 0x808080);
            g.drawRect(BORDER+(BORDER+d_tube), bar_y, d_tube, d_tube);
        }
        g.setColor(palette[2]);
        g.fillArc(BORDER+2* (BORDER+d_tube), bar_y, d_tube, d_tube, 0, 360);
        if (2 == pndx) {
            g.setColor((ndx < 0) ? 0x000000 : 0x808080);
            g.drawRect(BORDER+2* (BORDER+d_tube), bar_y, d_tube, d_tube);
        }
        g.setColor(palette[3]);
        g.fillArc(BORDER+2* (BORDER+d_tube), bar_y, d_tube, d_tube, 0, 360);
        if (3 == pndx) {
            g.setColor((ndx < 0) ? 0x000000 : 0x808080);
            g.drawRect(BORDER+3* (BORDER+d_tube), bar_y, d_tube, d_tube);
        }
        */
        bar_y += BORDER + d_tube ;
        g.setFont(font);
	g.setColor(0, 0, 0);
	g.drawString(format(getBlueComponent()), cyfrak_x, bar_y,  Graphics.TOP|Graphics.RIGHT);
	int b_w = (bar_w * getBlueComponent()) / 255;
	g.setColor(0, 0, 255);
	g.fillRect(bar_x, bar_y, b_w, bar_h - BORDER);
	g.setColor((ndx == 0) ? 0x000000 : 0x0000FF);
	g.drawRect(bar_x, bar_y, bar_w - 1, bar_h - BORDER - 1);
        
        bar_y += BORDER + d_tube ;
        g.setColor(0, 0, 0);
	g.drawString(format(getGreenComponent()), cyfrak_x, bar_y,  Graphics.TOP|Graphics.RIGHT);
	int g_w = (bar_w * getGreenComponent()) / 255;
	g.setColor(0, 255, 0);
	g.fillRect(bar_x, bar_y, g_w, bar_h - BORDER);
	g.setColor((ndx == 1) ? 0x000000 : 0x00FF00);
	g.drawRect(bar_x, bar_y, bar_w - 1, bar_h - BORDER - 1);
        
        bar_y += BORDER + d_tube ;
        g.setColor(0, 0, 0);
	g.drawString(format(getRedComponent()), cyfrak_x, bar_y,  Graphics.TOP|Graphics.RIGHT);
	int r_w = (bar_w * getRedComponent()) / 255;
	g.setColor(255, 0, 0);
	g.fillRect(bar_x, bar_y, r_w, bar_h - BORDER);
	g.setColor((ndx == 2) ? 0x000000 : 0xFF0000);
	g.drawRect(bar_x, bar_y, bar_w - 1, bar_h - BORDER - 1);
        
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
	    if (ndx < -1)
		ndx = -1;
	    if (ndx > 3)
		ndx = 3;
	    if (ndx >= 0) {
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
