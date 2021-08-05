/*
 * Colin Fitzpatrick
 * 
 * Professor Gendreau
 * 
 * Homework 5
 * 
 * AVL Tree
 */

import java.io.*;
import java.util.*;

public class AVLTree {
    private RandomAccessFile f;
    private long root;
    private long free;
    private int numFields;
    private int fieldLengths[];

    private class Node {
        private int key;
        private char fields[][];
        private long left;
        private long right;
        private int height;

        private Node(long l, int d, long r, char fields2[][]) {
          // Constructor for the AVL Tree Nodes
            left = l;
            key = d;
            right = r;
            fields = fields2;
            height = 0;
        }

        private Node(long addr) throws IOException {
          //  
            f.seek(addr);
            key = f.readInt();
            fields = new char[numFields][];
            int temp = 0;
            
            for (int i = 0; i < numFields; i++) {
                for (int j = 0; j < fieldLengths[i]; j++) {
                    temp++;
                }
                fields[i] = new char[temp];
                temp = 0;
            }
            for (int i = 0; i < numFields; i++) {
                for (int j = 0; j < fieldLengths[i]; j++) {
                    fields[i][j] = f.readChar();
                }
            }
            left = f.readLong();
            right = f.readLong();
            height = f.readInt();
        }

        private void writeNode(long addr) throws IOException {
            f.seek(addr);
            f.writeInt(key);
            for (int i = 0; i < numFields; i++) {
                for (int j = 0; j < fieldLengths[i]; j++) {
                    if (fields[i][j] != '\0') {
                        f.writeChar(fields[i][j]);
                    }
                    else {
                        f.writeChar('\0');
                    }
                }
            }
            f.writeLong(left);
            f.writeLong(right);
            f.writeInt(height);
        }
    }

    public AVLTree(String fname, int fieldLengths2[]) throws IOException {
        //creates a new empty AVL tree stored in the file fname
        //the number of character string fields is fieldLengths.length
        //fieldLengths contains the length of each field
        File file = new File(fname);
        if (file.exists()) {
            file.delete();
        }
        f = new RandomAccessFile(file, "rw");
        fieldLengths = fieldLengths2;
        numFields = fieldLengths.length;
        root = 0;
        free = 0;
        f.seek(0);
        f.writeLong(root);
        f.writeLong(free);
        f.writeInt(numFields);
        for (int i : fieldLengths) {
            f.writeInt(i);
        }
    }

    public AVLTree(String fname) throws IOException {
        //reuse an existing tree store in the file fname
        File file = new File(fname);
        f = new RandomAccessFile(file, "rw");
        f.seek(0);
        root = f.readLong();
        free = f.readLong();
        f.seek(16);
        numFields = f.readInt();
        fieldLengths = new int[numFields];
        for (int i = 0; i < numFields; i++) {
            fieldLengths[i] = f.readInt();
        }
    }

    public void insert(int k, char fields[][]) throws IOException {
        // This is the main insert method which caller the helper method below
        root = insertHelper(root, k, fields);
    }

    
    private long insertHelper(long r, int k, char fields[][]) throws IOException {
        // This method will insert integers at the long address and insert them at the specified fields
        Node tempN;
        if (r == 0) {
            tempN = new Node(0, k, 0, fields);
            long addr = getFree();
            removeFromFree(addr);
            tempN.writeNode(addr);
            return addr;
        }
        tempN = new Node(r);
        // Node being inserted is less than the node currently in view
        if (k < tempN.key) {
            tempN.left = insertHelper(tempN.left, k, fields);
        }
        // Node being inserted is greater than the node currently in view
        else if (k > tempN.key) {
            tempN.right = insertHelper(tempN.right, k, fields);
        }
        else {
            return r;
        }
        tempN.height = getHeight(tempN);
        tempN.writeNode(r);
        int heightDifference = getHeightDifference(r);
        if (heightDifference < -1) {
            if (getHeightDifference(tempN.right) > 0) {
                tempN.right = rightRotate(tempN.right);
                tempN.writeNode(r);
                return leftRotate(r);
            }
            else {
                return leftRotate(r);
            }
        }
        else if (heightDifference > 1) {
            if (getHeightDifference(tempN.left) < 0) {
                tempN.left = leftRotate(tempN.left);
                tempN.writeNode(r);
                return rightRotate(r);
            }
            else {
                return rightRotate(r);
            }
        }
        return r;
    }

    public void remove(int k) throws IOException {
      // will be used to remove integer K
        root = removeHelper(root, k);
    }

    private long removeHelper(long r, int k) throws IOException {
      // This is the recursive helper method that removes the int k at the address r
      // if the address is equal to 0 we need to return 0
      // 
        if (r == 0) {
            return 0;
        }
        long ret = r;
        Node current = new Node(r);
        if (current.key == k) {
            if (current.left == 0) {
                ret = current.right;
                addToFree(r);
            }
            else if (current.right == 0) {
                ret = current.left;
                addToFree(r);
            }
            else {
                current.left = replace(current.left, current);
                current.height = getHeight(current);
                current.writeNode(r);
            }
        }
        else if (current.key > k) {
            current.left = removeHelper(current.left, k);
            current.height = getHeight(current);
            current.writeNode(r);
        }
        else {
            current.right = removeHelper(current.right, k);
            current.height = getHeight(current);
            current.writeNode(r);
        }
        int heightDifference = getHeightDifference(r);
        if (heightDifference < -1) {
            if (getHeightDifference(current.right) > 0) {
                current.right = rightRotate(current.right);
                current.writeNode(r);
                return leftRotate(r);
            }
            else {
                return leftRotate(r);
            }
        }
        else if (heightDifference > 1) {
            if (getHeightDifference(current.left) < 0) {
                current.left = leftRotate(current.left);
                current.writeNode(r);
                return rightRotate(r);
            }
            else {
                return rightRotate(r);
            }
        }
        return ret;
    }

    
    private long replace(long r, Node change) throws IOException {
      // This method changes a nodes value at the address in the 
      // file given at place r
        Node current = new Node(r);
        if (current.right != 0) {
            current.right = replace(current.right, change);
            current.height = getHeight(current);
            current.writeNode(r);
            int heightDifference = getHeightDifference(r);
            if (heightDifference < -1) {
                if (getHeightDifference(current.right) > 0) {
                    current.right = rightRotate(current.right);
                    current.writeNode(r);
                    return leftRotate(r);
                }
                else {
                    return leftRotate(r);
                }
            }
            else if (heightDifference > 1) {
                if (getHeightDifference(current.left) < 0) {
                    current.left = leftRotate(current.left);
                    current.writeNode(r);
                    return rightRotate(r);
                }
                else {
                    return rightRotate(r);
                }
            }
            return r;
        }
        else {
            change.key = current.key;
            change.fields = current.fields;
            addToFree(r);
            return current.left;
        }
    }

    private long leftRotate(long n) throws IOException {
   // This rotates the node on the left hand side of the tree
   // to the right side to balance out the tree once again
        Node current = new Node(n);
        long currentRight = current.right;
        Node tempN = new Node(current.right);
        current.right = tempN.left;
        tempN.left = n;
        current.height = getHeight(current);
        current.writeNode(n);
        tempN.height = getHeight(tempN);
        tempN.writeNode(currentRight);
        return currentRight;
    }

    private long rightRotate(long n) throws IOException {
      // This rotates the node on the right hand side of the tree
      // to the left side to balance out the tree once again
        Node current = new Node(n);
        long currentLeft = current.left;
        Node tempN = new Node(current.left);
        current.left = tempN.right;
        tempN.right = n;
        current.height = getHeight(current);
        current.writeNode(n);
        tempN.height = getHeight(tempN);
        tempN.writeNode(currentLeft);
        return currentLeft;
    }

    private int getHeightDifference(long addr) throws IOException {
      // This method gets the difference of the height between the two sides of the AVL tree
      // this will be useful later as this method will allow use to determine
      // which rotation to turn and how many rotations to do whether that being 
      // a single or double rotation
        Node current = new Node(addr);
        Node left = new Node(current.left);
        Node right = new Node(current.right);

        if (current.left == 0) {
            left.height = -1;
        }
        if (current.right == 0) {
            right.height = -1;
        }
        return left.height - right.height;
    }

    private int getHeight(Node current) throws IOException {
      // Returns the height if the AVL Tree for both the right
      // and left side of the tree
        Node left = new Node(current.left);
        Node right = new Node(current.right);
        if (current.left == 0 && current.right == 0) {
            return 0;
        }
        if (current.left == 0) {
            return 1 + right.height;
        }
        if (current.right == 0) {
            return 1 + left.height;
        }
        return 1 + Math.max(left.height, right.height);
    }

    private long getFree() throws IOException{
        long addr;
        f.seek(8);
        if (f.readLong() == 0) {
            addr = f.length();
        }
        else {
            f.seek(8);
            addr = f.readLong();
        }
        return addr;
    }

    public LinkedList<Integer> getFreeList() throws IOException {
        LinkedList<Integer> out = new LinkedList<>();
        long addr = getFree();
        if (addr != f.length()) {
            while (addr <= f.length()) {
                out.add((int) addr);
                f.seek(addr);
                addr = f.readLong();
                if (addr == 0) {
                    break;
                }
            }
        }
        return out;
    }

    private void addToFree(long r) throws IOException {
        long free1 = getFree();
        if (free1 == f.length()) {
            f.seek(8);
            f.writeLong(r);
            f.seek(r);
            f.writeLong(0);
            free = r;
        }
        else {
            f.seek(8);
            long temp = f.readLong();
            f.seek(8);
            f.writeLong(r);
            f.seek(r);
            f.writeLong(temp);
            free = r;
        }
    }
    
    private void removeFromFree(long r) throws IOException {
        if (r == f.length()) {
            f.seek(8);
            f.writeLong(0);
            free = 0;
        }
        else {
            f.seek(r);
            long temp = f.readLong();
            f.seek(8);
            if (temp == 0) {
                f.writeLong(0);
                free = 0;
            }
            else {
                f.writeLong(temp);
                free = temp;
            }
        }
    }

    private String printFields(long addr) throws IOException{
        Node tempN = new Node(addr);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < numFields; i++) {
            for (int j = 0; j < fieldLengths[i]; j++) {
                sb.append(tempN.fields[i][j]);
            }
            sb.append(" ");
        }
        return sb.toString();
    }

    public LinkedList<String> find(int k) throws IOException {
        LinkedList<String> out = new LinkedList<>();
        f.seek(20);
        for (int i = 0; i < numFields; i++) {
            f.readInt();
        }
        while (f.getFilePointer() < f.length()) {
            Node current = new Node(f.getFilePointer());
            if (current.key == k) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < numFields; i++) {
                    for (int j = 0; j < fieldLengths[i]; j++) {
                        if (!(current.fields[i][j] == '\0')) {
                            sb.append(current.fields[i][j]);
                        }
                    }
                    out.add(sb.toString());
                    sb.delete(0, sb.length());
                }
                return out;
            }
        }
        return null;
    }

    public void print() throws IOException {
        f.seek(0);
        System.out.println(" [" + f.getFilePointer() + " Root: " + root + "] ");
        f.seek(8);
        System.out.println(" [" + f.getFilePointer() + " Free: " + free + "] ");
        f.seek(16);
        System.out.println(" [" + f.getFilePointer() + " numOtherFields " + numFields + "] ");
        f.seek(root);
        print(root);
        System.out.println();
    }

    private void print(long r) throws IOException {
        if (r == 0) {
            return;
        }
        Node tempN = new Node(r);
        print(tempN.left);
        f.seek(r);
        System.out.println(" [" + f.getFilePointer() + " " + tempN.key + " " + printFields(r) + " " + tempN.left + " " + tempN.right + " " + tempN.height + "] ");
        print(tempN.right);
    }

    public void close() throws IOException {
        f.seek(0);
        f.writeLong(root);
        f.seek(8);
        f.writeLong(free);
        f.close();
    }
}