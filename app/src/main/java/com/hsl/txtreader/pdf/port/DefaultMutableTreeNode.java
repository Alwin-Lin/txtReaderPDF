package com.hsl.txtreader.pdf.port;

import java.util.ArrayList;

public class DefaultMutableTreeNode {
    private DefaultMutableTreeNode parent;
    private Object userObject;
    private ArrayList<DefaultMutableTreeNode> children;
    protected DefaultMutableTreeNode() {
        parent = null;
        userObject = null;
        children = new ArrayList<DefaultMutableTreeNode>();
    }
    protected Object getUserObject() {
        return userObject;
    }
    protected void setUserObject(Object userObject) {
        this.userObject = userObject;
    }

    public void add(DefaultMutableTreeNode newChild) {
        newChild.parent = this;
        children.add(newChild);
    }

    public ArrayList<String> getChildrenList() {
        ArrayList<String> arrayList = new ArrayList<String>();

        for (DefaultMutableTreeNode curNode : children) {
            arrayList.add(curNode.getUserObject().toString());
        }
        return arrayList;
    }

    public ArrayList<DefaultMutableTreeNode> getChildren() {
        return children;
    }


    public DefaultMutableTreeNode getParent() {
        return parent;
    }

    public int getChildCount() {
        return children.size();
    }
}
