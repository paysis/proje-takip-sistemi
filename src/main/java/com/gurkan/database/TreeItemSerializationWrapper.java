package com.gurkan.database;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Stack;

import javafx.scene.control.TreeItem;

/**
 * Inspired from
 * https://stackoverflow.com/questions/61023834/how-do-i-make-javafx-treeview-and-treeitem-serializable
 * https://docs.oracle.com/en/java/javase/13/docs/api/java.base/java/io/Serializable.html
 */
public class TreeItemSerializationWrapper<T extends Serializable> implements Serializable {
    private transient TreeItem<T> treeItem;

    public TreeItemSerializationWrapper(TreeItem<T> treeItem) {
        if (treeItem == null) {
            throw new IllegalArgumentException();
        }

        this.treeItem = treeItem;
    }

    private void writeObject(ObjectOutputStream outStream) throws IOException {
        Stack<TreeItem<T>> stack = new Stack<>();

        stack.push(treeItem);

        outStream.defaultWriteObject();
        do {
            TreeItem<T> currentTreeItem = stack.pop();

            int treeSize = currentTreeItem.getChildren().size();
            outStream.writeInt(treeSize);

            outStream.writeObject(currentTreeItem.getValue());

            for (int i = treeSize - 1; i >= 0; i--) {
                stack.push(currentTreeItem.getChildren().get(i));
            }
        } while (!stack.isEmpty());
    }

    private void readObject(ObjectInputStream inStream) throws IOException, ClassNotFoundException {
        inStream.defaultReadObject();

        Container root = new Container(inStream);
        this.treeItem = root.getTreeItem();

        if (root.getCount() > 0) {
            Stack<Container> stack = new Stack<>();
            stack.push(root);

            do {
                Container currentContainer = stack.peek();
                currentContainer.setCount(currentContainer.getCount() - 1);

                if (currentContainer.getCount() <= 0) {
                    stack.pop();
                }

                Container childContainer = new Container(inStream);
                currentContainer.getTreeItem().getChildren().add(childContainer.getTreeItem());

                if (childContainer.getCount() > 0) {
                    stack.push(childContainer);
                }
            } while (!stack.isEmpty());
        }
    }

    private Object readResolve() throws ObjectStreamException {
        return treeItem;
    }
}

class Container<T> {
    private int count;
    private final TreeItem<T> treeItem;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public TreeItem<T> getTreeItem() {
        return treeItem;
    }

    public Container(ObjectInputStream inStream) throws IOException, ClassNotFoundException {
        this.count = inStream.readInt();
        this.treeItem = new TreeItem<>((T) inStream.readObject());
    }

}