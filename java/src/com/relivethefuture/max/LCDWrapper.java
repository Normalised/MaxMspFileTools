package com.relivethefuture.max;

import java.util.HashMap;

import com.cycling74.max.Atom;
import com.cycling74.max.MaxObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LCDWrapper {

	// LCD Messages
	private static final String TEXTFACE = "textface";
	private static final String ASCII = "ascii";
	private static final String MOVETO = "moveto";
	private static final String FONT = "font";
	private static final String SCROLLRECT = "scrollrect";
	private static final String PAINTROUNDRECT = "paintroundrect";
	private static final String FRAMEROUNDRECT = "frameroundrect";
	private static final String FRAMEARC = "framearc";
	private static final String PAINTARC = "paintarc";
	private static final String LINESEGMENT = "linesegment";
	private static final String PAINTRECT = "paintrect";
	private static final String FRAMERECT = "framerect";
	private static final String PAINTOVAL = "paintoval";
	private static final String FRAMEOVAL = "frameoval";
	private static final String BRGB = "brgb";
	private static final String FRGB = "frgb";
	private static final String RESET = "reset";
	private static final String CLEAR = "clear";
	private static final String SIZE = "size";
	private static final String CLEARSPRITES = "clearsprites";
	private static final String FRONTSPRITE = "frontsprite";
	private static final String BACKSPRITE = "backsprite";
	private static final String DELETESPRITE = "deletesprite";
	private static final String HIDESPRITE = "hidesprite";
	private static final String DRAWSPRITE = "drawsprite";
	private static final String CLOSESPRITE = "closesprite";
	private static final String RECORDSPRITE = "recordsprite";
	private static final String ENABLESPRITES = "enablesprites";
//	private static final String BORDER = "border";
	private static final String LOCAL = "local";
	private static final String READPICT = "readpict";
	private static final String PAINTPOLY = "paintpoly";
	private static final String FRAMEPOLY = "framepoly";
	
	private static final String NORMAL = "normal";

	private String currentSprite = null;

	private Atom[] drawSprite = new Atom[] { Atom.newAtom(DRAWSPRITE), null, null, null };
	private Atom[] setSize = new Atom[] { Atom.newAtom(SIZE), null, null };
	private Atom[] fontName = new Atom[] { Atom.newAtom(FONT), null, null };
	private Atom   ascii = Atom.newAtom(ASCII);

	private HashMap<String,LCDImage> imageMap = new HashMap<String,LCDImage>();

	private String[] defaultTextFace = new String[] { TEXTFACE, NORMAL };

	Logger logger = LoggerFactory.getLogger(LCDWrapper.class);

	MaxObject peer = null;
	int peerOutlet = 0;

	public LCDWrapper(MaxObject peer) {
        logger.debug("Created LCD Wrapper for " + peer);
		this.peer = peer;
		this.peerOutlet = 0;
	}

	public LCDWrapper(MaxObject peer, int peer_outlet) {
        logger.debug("Created LCD Wrapper for " + peer + " and outlet " + peer_outlet);
		this.peer = peer;
		this.peerOutlet = peer_outlet;
	}

	public void init(int width, int height) {
        logger.debug("LCD Init");
		localOff();
		// border_off();
		enableSprites();
		setSize(width, height);
		clear();
	}

	// turn off mouse drawing on LCD surface
	private void localOff() {
		sendMessage(LOCAL, new Atom[] { Atom.newAtom(0) });
	}

//	private void borderOff() {
//		sendMessage(BORDER, new Atom[] { Atom.newAtom(0) });
//	}

	private void enableSprites() {
		sendMessage(ENABLESPRITES, new Atom[] { Atom.newAtom(1) });
	}

	public void beginSprite(String name) {
		currentSprite = name;
		sendMessage(RECORDSPRITE);
	}


	public void endSprite() {
		peer.outlet(peerOutlet, CLOSESPRITE, currentSprite);
		currentSprite = null;
	}

	public void drawSprite(String spritename, int x, int y) {
		drawSprite[1] = Atom.newAtom(spritename);
		drawSprite[2] = Atom.newAtom(x);
		drawSprite[3] = Atom.newAtom(y);
		sendMessage(drawSprite);
	}

	public void hideSprite(String spriteName) {
		peer.outlet(peerOutlet, HIDESPRITE, spriteName);
	}

	public void deleteSprite(String spriteName) {
		peer.outlet(peerOutlet, DELETESPRITE, spriteName);
	}

	public void spriteToBack(String spriteName) {
		peer.outlet(peerOutlet, BACKSPRITE, spriteName);
	}


	public void spriteToFront(String spriteName) {
		peer.outlet(peerOutlet, FRONTSPRITE, spriteName);
	}

	public void clearSprites() {
		peer.outlet(peerOutlet, CLEARSPRITES);
	}

	public void setSize(int width, int height) {
		setSize[1] = Atom.newAtom(width);
		setSize[2] = Atom.newAtom(height);
		sendMessage(setSize);
	}

	public void clear() {
		peer.outlet(peerOutlet, CLEAR);
	}

	public void reset() {
		peer.outlet(peerOutlet, RESET);
	}

	public void setFRGB(RGBColour col) {
		peer.outlet(peerOutlet, FRGB, col.toArray());
	}
	
	public void setBRGB(RGBColour col) {
		peer.outlet(peerOutlet, BRGB, col.toArray());
	}

	public void setFont(String fontname, int fontsize, String[] textface) {

		fontName[1] = Atom.newAtom(fontname);
		fontName[2] = Atom.newAtom(fontsize);
		sendMessage(fontName);

		if (textface == null) {
			textface = defaultTextFace;
		} else {
			// HACK HACK HACK!!
			if (textface.length == 1) {
				defaultTextFace[1] = textface[0];
				sendMessage(defaultTextFace);
				defaultTextFace[1] = NORMAL;
			}
		}
		// need to make this support other than default
		sendMessage(textface);

	}

	private void moveTo(int x, int y) {
		peer.outlet(peerOutlet, MOVETO, new int[] { x, y });
	}

	public void drawString(String str, int x, int y) {

		if (str == null) {
			str = "";
		}
		byte[] b = str.getBytes();
		Atom[] out = new Atom[b.length + 1];
		out[0] = ascii;
		for (int i = 1; i < out.length; i++) {
			out[i] = Atom.newAtom(b[i - 1]);
		}

		moveTo(x, y);
		sendMessage(out);
	}


	public void frameOval(int x, int y, int width, int height) {
		peer.outlet(peerOutlet, FRAMEOVAL, new int[] { x, y, x + width, y + height });
	}

	public void paintOval(int x, int y, int width, int height) {
		peer.outlet(peerOutlet, PAINTOVAL, new int[] { x, y, x + width, y + height });
	}


	public void frameRect(int x, int y, int width, int height) {
		peer.outlet(peerOutlet, FRAMERECT, new int[] { x, y, x + width, y + height });		
	}


	public void paintRect(int x, int y, int width, int height) {
		peer.outlet(peerOutlet, PAINTRECT, new int[] { x, y, x + width, y + height });
	}

	public void lineSegment(int x, int y, int x2, int y2) {
		peer.outlet(peerOutlet, LINESEGMENT, new int[] { x, y, x2, y2});
	}


	public void paintArc(int x, int y, int width, int height, int startAngle, int endAngle) {
		peer.outlet(peerOutlet, PAINTARC, new int[] { x, y, x + width, y + height,startAngle,endAngle });
	}


	public void frameArc(int x, int y, int width, int height, int startAngle, int endAngle) {
		peer.outlet(peerOutlet, FRAMEARC, new int[] { x, y, x + width, y + height,startAngle,endAngle });
	}


	public void frameRoundRect(int x, int y, int width, int height, int roundX, int roundY) {
		peer.outlet(peerOutlet, FRAMEROUNDRECT, new int[] { x, y, x + width, y + height,roundX,roundY});
	}


	public void paintRoundRect(int x, int y, int width, int height, int roundX, int roundY) {
		peer.outlet(peerOutlet, PAINTROUNDRECT, new int[] { x, y, x + width, y + height,roundX,roundY});
	}

	public void paintPoly(int[] vertices) {
		Atom[] out = new Atom[vertices.length + 1];
		out[0] = Atom.newAtom(PAINTPOLY);
		for (int i = 1; i < out.length; i++) {
			out[i] = Atom.newAtom(vertices[i - 1]);
		}
		sendMessage(out);
	}

	public void framePoly(int[] vertices) {
		Atom[] out = new Atom[vertices.length];
		for (int i = 0; i < out.length; i++) {
			out[i] = Atom.newAtom(vertices[i]);
		}
		sendMessage(FRAMEPOLY, out);
	}

	public void scrollRect(int x, int y, int width, int height, int scrollX, int scrollY) {
		peer.outlet(peerOutlet, SCROLLRECT, new int[] { x, y, x + width, y + height,scrollX, scrollY});
	}

	public LCDImage loadImage(String filename) {
		LCDImage img = new LCDImage(filename);
		imageMap.put(filename, img);
		peer.outlet(peerOutlet, READPICT, filename);
		return img;
	}

	public void pict(String name, String error) {
		System.err.println("(LCDWrapper) error loading " + name);
		imageMap.remove(name);
	}

	public void pict(String name, int width, int height) {
		LCDImage i = (LCDImage) imageMap.get(name);
		if (i != null) {
			i.width = width;
			i.height = height;
		}
	}

	private void sendMessage(String name) {
		peer.outlet(peerOutlet, name);
	}
	
	private void sendMessage(String name, Atom[] args) {
		peer.outlet(peerOutlet,name, args);
	}
	
	private void sendMessage(Atom[] cmd) {
		peer.outlet(peerOutlet, cmd);
	}

	private void sendMessage(String[] cmd) {
		peer.outlet(peerOutlet, cmd);
	}

}
