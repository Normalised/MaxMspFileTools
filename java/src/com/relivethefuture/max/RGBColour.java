package com.relivethefuture.max;/*
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

public class RGBColour {
	public int r;
	public int g;
	public int b;
	
	public static RGBColour WHITE = new RGBColour(255,255,255);
	public static RGBColour BLACK = new RGBColour(0,0,0);
	
	public RGBColour(int r, int g, int b) {
		this.r = r;
		this.g = g;
		this.b = b;
	}
	
	public RGBColour dark(int ra,int ga, int ba) {
		return new RGBColour ( r-ra,g-ga,b-ba);
	}
	public int[] toArray() {
		return new int[] {r,g,b};
	}
}
