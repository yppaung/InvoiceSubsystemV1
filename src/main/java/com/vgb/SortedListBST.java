package com.vgb;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Stack;

/**
 * A generic sorted list implementation using a Binary Search Tree (BST) that
 * maintains elements in order based on a provided Comparator
 * 
 * @param <T> The type of elements stored in the list
 */
public class SortedListBST<T> implements Iterable<T> {
	private class Node {
		private T data;
		private Node left;
		private Node right;

		private Node(T data) {
			this.data = data;
			this.left = null;
			this.right = null;
		}
	}

	private class BSTIterator implements Iterator<T> {
		private Stack<Node> stack;

		BSTIterator() {
			stack = new Stack<>();
			Node current = root;
			while (current != null) {
				stack.push(current);
				current = current.left;
			}
		}

		@Override
		public boolean hasNext() {
			return !stack.isEmpty();
		}

		@Override
		public T next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}

			Node node = stack.pop();
			T result = node.data;
			if (node.right != null) {
				node = node.right;
				while (node != null) {
					stack.push(node);
					node = node.left;
				}
			}

			return result;
		}
	}

	private Node root;
	private int size;
	private final Comparator<T> comparator;

	/**
	 * Constructs a new sorted list with the specified comparator.
	 * 
	 * @param comparator The comparator that defines the ordering of elements
	 */
	public SortedListBST(Comparator<T> comparator) {
		this.comparator = comparator;
		this.root = null;
		this.size = 0;
	}

	/**
	 * Adds an element to the list, maintaining the sorted order.
	 * 
	 * @param element The element to add
	 */
	public void add(T element) {
		if (element == null) {
			throw new IllegalArgumentException("Cannot add null element to SortedListBST");
		}

		root = addRecursive(root, element);
		size++;
	}

	/**
	 * Helper method for recursive insertion into BST.
	 * 
	 * @param current The current node
	 * @param element The element to add
	 * @return The updated node
	 */
	private Node addRecursive(Node current, T element) {
		if (current == null) {
			return new Node(element);
		}

		int comparison = comparator.compare(element, current.data);

		if (comparison < 0) {
			current.left = addRecursive(current.left, element);
		} else {
			current.right = addRecursive(current.right, element);
		}

		return current;
	}

	/**
	 * Adds all elements from the provided array to the list.
	 * 
	 * @param elements Array of elements to add
	 */
	public void addAll(T[] elements) {
		if (elements == null) {
			throw new IllegalArgumentException("Cannot add null array to SortedListBST");
		}

		for (T element : elements) {
			add(element);
		}
	}

	/**
	 * Removes the specified element from the list if it exists.
	 * 
	 * @param element The element to remove
	 * @return true if the element was found and removed, false otherwise
	 */
	public boolean remove(T element) {
		if (element == null || root == null) {
			return false;
		}

		int originalSize = size;
		root = removeRecursive(root, element);

		return size < originalSize;
	}

	/**
	 * Helper method for recursive removal from BST.
	 * 
	 * @param current The current node
	 * @param element The element to remove
	 * @return The updated node
	 */
	private Node removeRecursive(Node current, T element) {
		if (current == null) {
			return null;
		}

		int comparison = comparator.compare(element, current.data);

		if (comparison == 0) {

			if (current.left == null && current.right == null) {
				size--;
				return null;
			}

			if (current.left == null) {
				size--;
				return current.right;
			}

			if (current.right == null) {
				size--;
				return current.left;
			}

			T successor = findMin(current.right);
			current.data = successor;
			current.right = removeRecursive(current.right, successor);
			return current;
		}

		if (comparison < 0) {
			current.left = removeRecursive(current.left, element);
		} else {
			current.right = removeRecursive(current.right, element);
		}

		return current;
	}

	/**
	 * Finds the minimum value in a subtree.
	 * 
	 * @param node The root of the subtree
	 * @return The minimum value
	 */
	private T findMin(Node node) {
		while (node.left != null) {
			node = node.left;
		}
		return node.data;
	}

	/**
	 * Returns the element at the specified index in in-order traversal.
	 * 
	 * @param index The index of the element to return
	 * @return The element at the specified index
	 * @throws IndexOutOfBoundsException if the index is out of range
	 */
	public T get(int index) {
		if (index < 0 || index >= size) {
			throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
		}

		Iterator<T> it = iterator();
		for (int i = 0; i < index; i++) {
			it.next();
		}

		return it.next();
	}

	/**
	 * Returns the first element in the list (smallest according to comparator).
	 * 
	 * @return The first element
	 * @throws NoSuchElementException if the list is empty
	 */
	public T getFirst() {
		if (root == null) {
			throw new NoSuchElementException("The list is empty");
		}

		Node current = root;
		while (current.left != null) {
			current = current.left;
		}

		return current.data;
	}

	/**
	 * Returns the last element in the list (largest according to comparator).
	 * 
	 * @return The last element
	 * @throws NoSuchElementException if the list is empty
	 */
	public T getLast() {
		if (root == null) {
			throw new NoSuchElementException("The list is empty");
		}

		Node current = root;
		while (current.right != null) {
			current = current.right;
		}

		return current.data;
	}

	/**
	 * Returns the number of elements in the list.
	 * 
	 * @return The number of elements
	 */
	public int size() {
		return size;
	}

	/**
	 * Returns whether the list is empty.
	 * 
	 * @return true if the list is empty, false otherwise
	 */
	public boolean isEmpty() {
		return size == 0;
	}

	/**
	 * Removes all elements from the list.
	 */
	public void clear() {
		root = null;
		size = 0;
	}

	/**
	 * Returns an iterator over the elements in the list in sorted order.
	 * 
	 * @return An iterator over the elements
	 */
	@Override
	public Iterator<T> iterator() {
		return new BSTIterator();
	}

	/**
	 * Returns a string representation of the list.
	 * 
	 * @return A string representation of the list
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("[");

		Iterator<T> it = iterator();
		while (it.hasNext()) {
			sb.append(it.next());
			if (it.hasNext()) {
				sb.append(", ");
			}
		}

		sb.append("]");
		return sb.toString();
	}
}