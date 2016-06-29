package com.relivethefuture.max;


/*
Copyright (c) 2009 Martin Wood-Mitrovski

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Scrollbar {

    private static final int MIN_HANDLE_HEIGHT = 8;
    Logger logger = LoggerFactory.getLogger(Scrollbar.class);

	private static final String SPRITE_NAME = "vscrollbar";

	private int width = 12;
	private int height = 400;
	
	private LCDWrapper lcd;
	
	private ChangeHandler changeHandler;
	
	private int handleY = 0;
	private int handleHeight = 0;
	private boolean visible = false;
	private int handleClickOffset = 0;
	
	private RGBColour backgroundColour;
	private RGBColour handleColour;
	
	private int visibleRows = 1;
	private int totalRows = 1;
	// First visible row
	private int topRow = 0;
	
	private int x = 0;
	private int y = 0;

	private boolean autoHide = true;
	
	private boolean dragging;
	
	public Scrollbar(LCDWrapper context) {
		lcd = context;
		
		backgroundColour = RGBColour.WHITE;
		handleColour = RGBColour.BLACK;
	}

	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public void setSize(int width,int height) {
		this.width = width;
		this.height = height;
		updateHandleSize();
		draw();
	}
	
	public void setScrollProperties(int visible, int total) {
		//MaxObject.post("setScrollProperties " + visible + " : " + total + " : " + topRow);
		visibleRows = visible;
		totalRows = total;

		updateHandleSize();
		
		if((totalRows - topRow) < visibleRows) {
			scrollBy((totalRows - topRow) - visibleRows);
		}

		if(autoHide) {
			setVisible(totalRows > visibleRows);
		}
	}
	
	private void updateHandleSize() {
		double handleSize = (double) visibleRows / (double) totalRows;
        logger.debug("Visible rows " + visibleRows + ". Total Rows " + totalRows);
		logger.debug("Handle size " + handleSize);
		handleHeight = (int) (height * handleSize);
        if(handleHeight < MIN_HANDLE_HEIGHT) {
            handleHeight = MIN_HANDLE_HEIGHT;
        }
	}

	public void draw() {
		lcd.beginSprite(SPRITE_NAME);
		// clear rect;
		lcd.setFRGB(backgroundColour);
		lcd.paintRect(0, 0, width, height);

		// draw bar area
		lcd.setFRGB(handleColour);
		lcd.frameRect(0, 0, width, height);

		// draw handle
		lcd.paintRect(0, handleY, width, handleHeight);
		lcd.endSprite();
		lcd.drawSprite(SPRITE_NAME, x, y);
	}

	public void onMouseDown(int xPos, int yPos) {
		if(xPos >= x && xPos <= x + width) {
			if(yPos >= handleY && yPos <= (handleY + handleHeight)) {
				handleClickOffset = yPos - handleY;
				dragging= true;
				//MaxObject.post("Start Drag at " + handleClickOffset);
			}
		}
	}
	
	public void onMouseUp(int xPos, int yPos) {
		
		dragging = false;
		
		if(xPos >= x && xPos <= x + width) {
			if ((yPos < handleY) || (yPos > handleY + handleHeight)) {
				
				// move it by the size of the handle
				if (yPos > handleY) {
					moveScrollbarHandle(handleY + handleHeight);
				} else {
					moveScrollbarHandle(handleY - handleHeight);
				}
			}
		}
	}
	
	public void onMouseMove(int x, int y) {
		//MaxObject.post("onMouseMove " + x + ", " + y);
		moveScrollbarHandle(y - handleClickOffset);
	}
	
	public void moveScrollbarHandle(int newY) {
		if (visible) {
			if (newY < 0) {
				newY = 0;
			} else if (newY > height - handleHeight) {
				newY = height - handleHeight;
			}
			
			handleY = newY;
			double position = (double) handleY / (double) (height - handleHeight);
			topRow = (int) (((double) (totalRows - visibleRows)) * position);
			//MaxObject.post("Move " + topRow + " : " + position + " : " + handleY + " : " + height + " : " + handleHeight);
			draw();
			dispatchChangeEvent();
		}
	}

	private void dispatchChangeEvent() {
		changeHandler.positionChanged(topRow);
	}

	public void setVisible(boolean b) {
		visible = b;
		if(visible) {
			draw();
		} else {
			lcd.hideSprite(SPRITE_NAME);
		}
	}

	public boolean isVisible() {
		return visible;
	}

	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}

	public boolean isDragging() {
		return dragging;
	}

	public void reset() {
		setTopRow(0);
	}

	public void setChangeHandler(ChangeHandler handler) {
		changeHandler = handler;
	}

	public void stopDrag() {
		dragging = false;
	}

	public String getSpriteName() {
		return SPRITE_NAME;
	}

	public int getTopRow() {
		return topRow;
	}

	public void scrollBy(int i) {
		// scroll up or down
		//MaxObject.post("scrollBy " + i + " : " + topRow);
		int newTop = topRow + i;
		if(newTop < 0) {
			newTop = 0;
		} else if (newTop > (totalRows - visibleRows)) {
			newTop = totalRows - visibleRows;
		}
		
		setTopRow(newTop);
	}

	private void setTopRow(int newTop) {
		topRow = newTop;
		handleY = Math.round((((float) topRow / (float) (totalRows - visibleRows)) * (height - handleHeight)));
		draw();
		dispatchChangeEvent();
	}
}
