package com.relivethefuture.max;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

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

class TreeNode {
	
	public String name;
	public File file;
	public boolean open;
	public int depth;
	public ArrayList<TreeNode> children;
	public boolean calculated;
	public boolean isFile;
	public TreeNode parent;
	
	TreeNode(File file,TreeNode parent, int depth) {
		this.depth = depth;
		this.file = file;
		this.parent = parent;
		
		calculated = false;
		open = false;
		isFile = file.isFile();
		name = file.getName();
	}

	public boolean hasChildren() {
		return children != null && children.size() > 0;
	}
	
	public void expand(FilenameFilter filter) {
		
		//MaxObject.post("Expand " + name + " : " + isFile);
		
		if (!isFile) {

			File[] files = file.listFiles(filter);

			if (files != null) {
				children = new ArrayList<TreeNode>(files.length);

				for (int i = 0; i < files.length; i++) {
					children.add(new TreeNode(files[i],this,depth + 1));
				}
			}
		}
		calculated = true;
	}
}