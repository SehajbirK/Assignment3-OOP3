package implementations;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import utilities.BSTreeADT;
import utilities.Iterator;

public class BSTree<E extends Comparable<? super E> & Serializable> implements BSTreeADT<E>, Serializable {

    private static final long serialVersionUID = 1L;

    private BSTreeNode<E> root;
    private int size;

    public BSTree() {
        root = null;
        size = 0;
    }

    public BSTree(E element) {
        if (element == null) throw new NullPointerException("Cannot add null");
        root = new BSTreeNode<>(element);
        size = 1;
    }

    @Override
    public BSTreeNode<E> getRoot() {
        if (root == null) throw new NullPointerException("Tree is empty");
        return root;
    }

    @Override
    public int getHeight() {
        return height(root);
    }

    private int height(BSTreeNode<E> node) {
        if (node == null) return 0;
        return 1 + Math.max(height(node.getLeft()), height(node.getRight()));
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    @Override
    public boolean contains(E entry) {
        return search(entry) != null;
    }

    @Override
    public BSTreeNode<E> search(E entry) {
        if (entry == null) throw new NullPointerException("Cannot search for null");
        return search(root, entry);
    }

    private BSTreeNode<E> search(BSTreeNode<E> node, E entry) {
        if (node == null) return null;
        int cmp = entry.compareTo(node.getElement());
        if (cmp == 0) return node;
        else if (cmp < 0) return search(node.getLeft(), entry);
        else return search(node.getRight(), entry);
    }

    @Override
    public boolean add(E newEntry) {
        if (newEntry == null) throw new NullPointerException("Cannot add null");
        if (root == null) {
            root = new BSTreeNode<>(newEntry);
            size++;
            return true;
        }
        return add(root, newEntry);
    }

    private boolean add(BSTreeNode<E> node, E entry) {
        int cmp = entry.compareTo(node.getElement());
        if (cmp == 0) return false;
        else if (cmp < 0) {
            if (node.getLeft() == null) {
                node.setLeft(new BSTreeNode<>(entry));
                size++;
                return true;
            }
            return add(node.getLeft(), entry);
        } else {
            if (node.getRight() == null) {
                node.setRight(new BSTreeNode<>(entry));
                size++;
                return true;
            }
            return add(node.getRight(), entry);
        }
    }

    // -------------------- removeMin / removeMax --------------------
    @Override
    public BSTreeNode<E> removeMin() {
        if (root == null) return null;
        BSTreeNode<E> minNode = getMin(root);
        root = removeMin(root);
        size--;
        return minNode;
    }

    private BSTreeNode<E> removeMin(BSTreeNode<E> node) {
        if (node.getLeft() == null) return node.getRight();
        node.setLeft(removeMin(node.getLeft()));
        return node;
    }

    private BSTreeNode<E> getMin(BSTreeNode<E> node) {
        while (node.getLeft() != null) node = node.getLeft();
        return node;
    }

    @Override
    public BSTreeNode<E> removeMax() {
        if (root == null) return null;
        BSTreeNode<E> maxNode = getMax(root);
        root = removeMax(root);
        size--;
        return maxNode;
    }

    private BSTreeNode<E> removeMax(BSTreeNode<E> node) {
        if (node.getRight() == null) return node.getLeft();
        node.setRight(removeMax(node.getRight()));
        return node;
    }

    private BSTreeNode<E> getMax(BSTreeNode<E> node) {
        while (node.getRight() != null) node = node.getRight();
        return node;
    }

    // -------------------- Traversal Iterators --------------------
    @Override
    public Iterator<E> inorderIterator() {
        ArrayList<E> list = new ArrayList<>();
        inorder(root, list);
        return new ArrayListIterator<>(list);
    }

    private void inorder(BSTreeNode<E> node, ArrayList<E> list) {
        if (node == null) return;
        inorder(node.getLeft(), list);
        list.add(node.getElement());
        inorder(node.getRight(), list);
    }

    @Override
    public Iterator<E> preorderIterator() {
        ArrayList<E> list = new ArrayList<>();
        preorder(root, list);
        return new ArrayListIterator<>(list);
    }

    private void preorder(BSTreeNode<E> node, ArrayList<E> list) {
        if (node == null) return;
        list.add(node.getElement());
        preorder(node.getLeft(), list);
        preorder(node.getRight(), list);
    }

    @Override
    public Iterator<E> postorderIterator() {
        ArrayList<E> list = new ArrayList<>();
        postorder(root, list);
        return new ArrayListIterator<>(list);
    }

    private void postorder(BSTreeNode<E> node, ArrayList<E> list) {
        if (node == null) return;
        postorder(node.getLeft(), list);
        postorder(node.getRight(), list);
        list.add(node.getElement());
    }

    // -------------------- Internal ArrayList Iterator --------------------
    private class ArrayListIterator<T> implements Iterator<T> {
        private int index = 0;
        private ArrayList<T> data;

        public ArrayListIterator(ArrayList<T> data) {
            this.data = data;
        }

        @Override
        public boolean hasNext() {
            return index < data.size();
        }

        @Override
        public T next() {
            if (!hasNext()) throw new NoSuchElementException();
            return data.get(index++);
        }
    }
}
