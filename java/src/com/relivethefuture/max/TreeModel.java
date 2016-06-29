package com.relivethefuture.max;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Iterator;

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

public class TreeModel {
	TreeNode rootNode;
	ArrayList<TreeNode> nodeList = new ArrayList<TreeNode>();
	private TreeNode selected;
	private int selectedIndex = -1;
    private FilenameFilter filter;
    private SelectionChangeListener selectionChangeListener;
    private File rootFile;

	public TreeModel(File root, FilenameFilter filter) {
		setRoot(root);
		this.filter = filter;
	}

    public void setFilter(FilenameFilter filter) {
        this.filter = filter;
        nodeList.clear();
    }

	public void setRoot(File root) {
        rootFile = root;
		rootNode = new TreeNode(rootFile,null,-1);
		nodeList.clear();
	}
	
	public void expandRoot() {
		rootNode.expand(filter);
		nodeList.addAll(rootNode.children);
	}
	
	public void expand(TreeNode node) {
		//MaxObject.post("Expand Node : " + node.name + " : " + node.depth);
		int index = nodeList.indexOf(node);
		node.expand(filter);
		// Insert children into list after the parent, this means all subsequent nodes
		// get shifted along, which is exactly what we need.
		nodeList.addAll(index + 1, node.children);
	}
	
	public TreeNode getNodeAtRow(int row) {
		//MaxObject.post("Get Node At row " + row + " : " + nodeList.size());
		return nodeList.get(row);
	}

	public int rowCount() {
		return nodeList.size();
	}

	public void setSelectedNode(TreeNode node) {
		//MaxObject.post("Set Selected Node : " + node.name);
		selected = node;
		selectedIndex = nodeList.indexOf(selected);
		dispatchSelectionChangeEvent();
	}
	
	public TreeNode getSelectedNode() {
		return selected;
	}

	public void toggleOpen(TreeNode node) {
		//MaxObject.post("Toggle Open " + node.name + " : " + node.open);
		node.open = !node.open;
		if(node.open) {
			expand(node);
		} else {
			contract(node);
		}
	}

	/**
	 * Close a node and recursively close all of its children if they are open
	 * 
	 * @param node
	 */
	private void contract(TreeNode node) {
		ArrayList<TreeNode> children = node.children;
		Iterator<TreeNode> i = children.iterator();
		while(i.hasNext()) {
			TreeNode child = i.next();
			if(child.open) {
				contract(child);
			}
		}
		nodeList.removeAll(node.children);
	}

	public void setSelectedIndex(int index) {
		if(index > -1 && index < nodeList.size()) {
			selectedIndex = index;
			selected = nodeList.get(selectedIndex);
			dispatchSelectionChangeEvent();
		}
	}
	
	private void dispatchSelectionChangeEvent() {
		selectionChangeListener.selectionChanged(selected);
	}

	public int getSelectedIndex() {
		return selectedIndex;
	}

	public void addSelectionListener(SelectionChangeListener listener) {
		selectionChangeListener = listener;
	}
}
