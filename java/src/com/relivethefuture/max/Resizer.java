package com.relivethefuture.max;

import com.cycling74.max.Atom;
import com.cycling74.max.Executable;
import com.cycling74.max.MaxBox;
import com.cycling74.max.MaxObject;
import com.cycling74.max.MaxPatcher;
import com.cycling74.max.MaxWindow;

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

public class Resizer implements Executable {

	private MaxObject mxj;
	
	public Resizer(MaxObject maxObj) {
		mxj = maxObj;
	}

	public void execute() {
		MaxObject.post("EXECUTE");
		
		
		MaxPatcher patcher = mxj.getParentPatcher();
		if(patcher == null) {
			MaxObject.post("No Parent");
			return;
		}
		
		MaxBox box = mxj.getMaxBox();
		
		if(box != null) {
			int[] rect = box.getRect();
			MaxObject.post("BOX " + rect[0] + "," + rect[1] + " : " + rect[2] + ", " + rect[3]);
		}
		
		MaxObject.post("BPatcher " + patcher.isBPatcher());
		if(patcher.isBPatcher()) {
			MaxBox[] boxes = patcher.getAllBoxes();
			for (int i = 0; i < boxes.length; i++) {
				MaxBox b = boxes[i];
				MaxObject.post("BOX " + b.getName());
			}
		}
		
		MaxWindow win = patcher.getWindow();
		if(win != null) {
			int[] size = patcher.getWindow().getSize();
			MaxObject.post("Size is " + size[0] + "," + size[1]);
			MaxPatcher p = win.getPatcher();
			MaxObject.post("P : " + p.getName() + " : " + p.getPath());
			MaxBox browser = patcher.getNamedBox("FileBrowser");
			
			patcher.send("script size " + p.getName(), new Atom[] {Atom.newAtom(300),Atom.newAtom(300)});
			if(browser != null) {
				//browser.setRect(0,0,200,400);
			}
			
		}
			
			
			
//			   var patch = this.patcher.wind.assoc;
	//
//				var w = patch.box.rect[2] - patch.box.rect[0];
//				var h = patch.box.rect[3] - patch.box.rect[1];
	//
//			    if(patch.box){
//			        if((patch.box.rect[3] - patch.box.rect[1]) < 50){
//			            // we are in patcher context...
//			            this.patcher.wind.size = [newWidth, newHeight];
//			            // remove the scroll bars
//			            this.patcher.wind.hasgrow = 0;                        
//			        }
//			        else{    // we are in bpatcher context...
//			            var left = patch.box.rect[0];
//			            var top = patch.box.rect[1];
//			            var right = left + newWidth;
//			            var bottom = top + newHeight;
//			            patch.box.rect = [left, top, right, bottom];
//			        }
//			    } else {
//					post("Couldnt get patch box");
//					post();
//				}		
		}
		
}
