//Erik Enriquez - eenrique@u.rochester.edu

import java.util.Collection;

class minHeap<T extends Comparable<T>> {
    private T[] arr;
    private int length;
    public minHeap() {
        int INIT_CAPACITY = 4;
        arr = (T[]) new Comparable[INIT_CAPACITY];
    }
    public minHeap(int capacity) {
        arr = (T[]) new Comparable[capacity];
    }
    public minHeap(Collection<T> elements) {
        // start with double allocation and length is existing elements
        arr = (T[]) new Comparable[elements.size() * 2];
        length = elements.size();

        // copy elements into the array
        int i = 0;
        for (T element : elements) {
            arr[i] = element;
            i++;
        }

        // heapify those elements
        heapify();
    }
    private void heapify() {
        int internalNodes = (length / 2) - 1;

        while (internalNodes != -1) {
            bubbleDown(internalNodes);
            internalNodes = internalNodes - 1;
        }
    }
    // Will insert the comparable into the priority queue, bubble its location, throw if null
    public void insert(T item) {
        if (item == null) {
            throw new IllegalArgumentException("Can not enter item null into priority queue");
        }
        // if data reaches allocation size, increase size
        if (length >= (arr.length/4)*3) {
            resize();
        }

        int i = 0;
        while(arr[i] != null) {
            i++;
        }
        // place in next available location (though it may be temporary)
        arr[i] = item;
        length = length + 1;

        // make sure it's not the root node before checking for bubbling
        if (length != 1) {
            // if the parent is greater than child, begin bubbling (remember it's a minheap, small at top)
            bubbleUp(i);
        }
    }
    private void bubbleUp(int index) {
        int parentIndex;

        // consider a non null max and switch it to that location

        // while we are not at the root
        while (index > 0) {
            parentIndex = (index - 1) / 2;
            // check if index is finally to parent that is smaller (remember it's a minheap, small at top)
            if (arr[index].compareTo(arr[parentIndex]) >= 0) {
                return;
                // if it is not the case, continue swapping
            } else {
                swap(index, parentIndex);
                index = parentIndex;
            }
        }
    }
    // return true if empty, false if in use
    public boolean isEmpty() {
        return length == 0;
    }
    // return the length (amount of data) in the priority queue
    public int size() {
        return length;
    }
    // will delete the smallest item and return its value
    public T deleteMin() {
        // replace with node in the largest section
        T value = arr[0];

        arr[0] = arr[length - 1];
        arr[length - 1] = null;

        if (length != 1) {
            // if the child is smaller than parent, begin bubbling (remember it's a minheap, smaller at top)
            bubbleDown(0);
        } else {
            arr[0] = null;
        }
        length --;
        return value;
    }
    private void bubbleDown(int index) {
        // continue looking until we find a place for the index
        while (true) {
            int leftChildIndex = 2 * index + 1;
            int rightChildIndex = 2 * index + 2;
            int min = index;
            // if it reaches out of bounds, none of these will execute, but it if it reached the bounds, it was a leaf
            // node, so it will be the max (met heap requirements)

            // ensure that the children are not null, if they are consider other two cases [ensure in bounds for leaf]
            if (leftChildIndex < arr.length - 1 && (arr[leftChildIndex] != null) && rightChildIndex < arr.length - 1
                    &&(arr[rightChildIndex] != null)) {
                if (arr[leftChildIndex].compareTo(arr[rightChildIndex]) < 0) {
                    if (arr[leftChildIndex].compareTo(arr[index]) < 0) {
                        min = leftChildIndex;
                    }
                } else if (rightChildIndex < length && arr[rightChildIndex].compareTo(arr[index]) < 0) {
                    if (arr[rightChildIndex].compareTo(arr[index]) < 0) {
                        min = rightChildIndex;
                    }
                }
                // ok the right is null, at least compare to left child and see if you should switch [ensure in bounds]
            } else if (leftChildIndex < arr.length - 1 && (arr[leftChildIndex] != null) && (arr[rightChildIndex] == null)) {
                if (arr[leftChildIndex].compareTo(arr[index]) < 0) {
                    min = leftChildIndex;
                }
                // ok the left is null, at least compare to the right child and see if you should switch [ensure in bounds]
            } else if (rightChildIndex < arr.length - 1 && (arr[leftChildIndex] == null) && (arr[rightChildIndex] != null)) {
                if (arr[rightChildIndex].compareTo(arr[index]) < 0) {
                    min = rightChildIndex;
                }
            }
            if (index == min) {
                // index is in the right spot, break out
                return;
            }
            // assuming no break out, swap with the smaller child and adjust position in index
            swap(index, min);
            index = min;
        }
    }
    // print out the contents of heap without considering null values
    public void printHeap() {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] != null) {
                System.out.print(arr[i] + ", ");
            }
        }
        System.out.println(" ");
    }
    private void swap(int indexOne, int indexTwo) {
        T temp;

        temp = arr[indexOne];
        arr[indexOne] = arr[indexTwo];
        arr[indexTwo] = temp;
    }
    private void resize() {
        // double the allocation
        T[] newArr = (T[]) new Comparable[arr.length * 2];

        for (int i = 0; i < length; i++) {
            newArr[i] = arr[i];
        }
        arr = newArr;
    }
}