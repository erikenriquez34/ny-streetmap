// With the help of Ethan Pan, Lab leaders, lecture Slides, ZyBooks, GeeksForGeeks, and Coding Made Simple on YouTube

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

//assuming that the user specifies the type of LinkedList (etc. string, objects, ints) and uses them consistently
public class URLinkedList<E> implements Iterable<E>{
    private URNode<E> head;
    private URNode<E> tail;
    //the boolean indicates whether the insertion of element e was successful
    public boolean add(E e) {
        URNode<E> newNode = new URNode<>(e, null, null);
        boolean condition = false;
        if (head == null) {
            head = newNode;
            tail = newNode;
            condition = true;
        } else {
            URNode<E> temp = tail;
            tail.setNext(newNode);
            newNode.setPrev(temp);
            tail = newNode;
        }
        return condition;
    }
    //no element returned
    public void add(int index, E element) {
        checkBounds(index);

        URNode<E> newNode = new URNode<>(element, null, null);
        URNode<E> current = head;
        int counter = 0;

        if (index == 0) {
            addFirst(element);
        } else if (index == this.size() - 1){
            addLast(element);
        } else {
            while (current.next()!=null) {
                if (counter == index) {
                    URNode<E> temp = current.prev();

                    current.prev().setNext(newNode);
                    current.setPrev(newNode);

                    newNode.setNext(current);
                    newNode.setPrev(temp);
                }
                counter = counter + 1;
                current = current.next();
            }
        }
    }
    //assuming the returned value indicates whether the add all operation was successful
    public boolean addAll(Collection<? extends E> c) {
        boolean condition = false;
        for (E o : c) {
            if(this.add(o)) {
                condition = true;
            }
        }
        return condition;
    }
    //assuming the return value indicates whether the add all operation was successful
    public boolean addAll(int index, Collection<? extends E> c) {
        checkBounds(index);

        boolean condition = false;

        for (E o : c) {
            this.add(index, o);
            index = index + 1;
        }
        return condition;
    }
    //no return value
    public void clear() {
        head = null;
        tail = null;
    }
    //the return value is used to indicate whether or no element is in the linked list
    public boolean contains(Object o) {

        URNode<E> current = head;
        boolean condition = false;

        while(current!=null) {
            if (current.element() == o) {
                condition = true;
            }
            current = current.next();
        }
        return condition;
    }
    //assuming that the return value indicates whether all the elements are in the linked list
    public boolean containsAll(Collection<?> c) {

        int counter = c.size();

        for (Object o : c) {
            if(this.contains(o)) {
                counter = counter - 1;
            }
        }
        return counter == 0;
    }
    //assuming that the boolean indicates whether object o matches to another object in the list
    public boolean equals(Object o) {

        if (getClass() != o.getClass()) {
            return false;
        }

        return this.contains(o);
    }
    //assuming that the E indicates the value at index
    public E get(int index) {
        checkBounds(index);

        int counter = 0;
        URNode<E> current = head;

        while(current!=null) {
            if (index == counter) {
                return current.element();
            }
            counter = counter  + 1;
            current = current.next();
        }
        return null;
    }
    //assuming that the return type indicates where the object o is first found from least to greates
    public int indexOf(Object o) {
        if(head == null) {
            return -1;
        }
        int index = 0;
        URNode<E> current = head;
        while(current!=null) {
            if (o == current.element()) {
                return index;
            }
            index = index + 1;
            current = current.next();
        }
        return -1;
    }
    //assuming that the boolean indicates whether a linked list is empty
    public boolean isEmpty() {
        return head == null;
    }
    //assuming that the return value is an iterator for the linked list meant for user purposes
    public Iterator<E> iterator() {
        return new URLinkIterator<>(head);
    }
    //assuming that the return type is the element removed at index
    public E remove(int index) {

        checkBounds(index);

        URNode<E> current = head;
        int counter = 0;
        E value = null;

        while (current.next()!=null) {
            if (counter == index) {
                value = current.element();
                (current.next()).setPrev(current.prev());
                (current.prev()).setNext(current.next());
            }
            counter = counter + 1;
            current = current.next();
        }
        // edge case last element
        if (counter == index) {
            (current.prev()).setNext(current.next());
        }
        return value;
    }
    //assuming boolean indicates whether object o was successfully found and removed
    public boolean remove(Object o) {

        URNode<E> current = head;
        boolean condition = false;

        while (current.next()!=null) {
            if (current.element() == o) {
                condition = true;
                (current.next()).setPrev(current.prev());
                (current.prev()).setNext(current.next());
            }
            current = current.next();
        }
        // edge case last element
        if (current.element() == o) {
            condition = true;
            (current.prev()).setNext(current.next());
        }
        return condition;
    }
    //assuming return type indicates that all the objects in collection were successfully found and removed
    public boolean removeAll(Collection<?> c) {

        boolean condition = false;

        for (Object o : c) {
            if(this.remove(o)) {
                condition = true;
            }
        }
        return condition;
    }
    //assuming return type int indicates the length of the linked list
    public int size() {
        if(head == null) {
            return 0;
        }
        int size = 0;
        URNode<E> current = head;
        while(current!=null) {
            size = size  + 1;
            current = current.next();
        }
        return size;
    }
    //assuming the return type return which element was at the index by element
    public E set(int index, E element) {
        checkBounds(index);

        URNode<E> newNode = new URNode<>(element, null, null);

        URNode<E>current = head;
        E value = null;
        int counter = 0;

        if (index == 0) {
            add(element);
        } else if (index == this.size() - 1){
            add(element);
        } else {
            while (current.next() != null) {
                if (counter == index) {
                    value = current.element();
                    URNode<E> before = current.prev();
                    URNode<E> after = current.next();

                    after.setPrev(newNode);
                    before.setNext(newNode);

                    newNode.setPrev(before);
                    newNode.setNext(after);
                }
                counter = counter + 1;
                current = current.next();
            }
        }
        return value;
    }
    public URLinkedList<E> subList(int fromIndex, int toIndex) {

        checkBounds(fromIndex);
        checkBounds(toIndex);

        URNode<E> current = head;
        URLinkedList<E> temp = new URLinkedList<>();

        int counter = 0;

        while (current.next()!=null) {
            if (counter >= fromIndex) {
                if (counter < toIndex) {
                    temp.add(current.element());
                }
            }
            counter = counter + 1;
            current = current.next();
        }
        return temp;
    }
    //assuming the return type is an Object[] array with all the elements in the linked list
    public Object[] toArray() {

        Object[] list = new Object[this.size()];
        URNode<E> current = head;

        for (int i = 0; i < this.size(); i++) {
            list[i] = current.element();
            current = current.next();
        }

        return list;
    }
    public String toString() {
        String list = "";

        Object[] keys = this.toArray();

        for (int i = 0; i < keys.length; i++) {
            list = list + keys[i] + ", ";
        }

        return list;
    }
    public boolean addFirst(E e) {
        URNode<E> newNode = new URNode<>(e, null, null);
        if (head != null) {
            head.setPrev(newNode);
            newNode.setNext(head);
        }
        head = newNode;
        return true;
    }
    public boolean addLast(E e) {
        this.add(e);
        return true;
    }
    //assuming E is the first element
    public E peekFirst() {

        return head.element();
    }
    //assuming E is the last element
    public E peekLast() {

        URNode<E> current = head;
        while(current.next()!=null) {
            current = current.next();
        }
        return current.element();
    }
    //assuming the return type is the first element that was removed
    public E pollFirst() {

        E value = head.element();
        head = head.next();

        return value;
    }
    //assuming the return type is the last element that was removed
    public E pollLast() {
        E value = tail.element();

        if (tail.prev() != null) {
            tail = tail.prev();
            tail.setNext(null);
        } else {
            this.clear();
        }

        return value;
    }
    //exception checkers
    private void checkBounds(int index) {
        if (index < 0 || index > this.size()) {
            throw new IndexOutOfBoundsException("The index you have input is out of bounds: " + index + ". Try " +
                    "an element between 0 and " + this.size() + ".");
        }
    }
    private class URNode<E> {
        private E e; // Value for this node
        private URNode<E> n; // Reference to next node in list
        private URNode<E> p; // Reference to previous node

        // Constructors
        URNode(E it, URNode<E> inp, URNode<E> inn) { e = it; p = inp; n = inn; }
        // Get and set methods for the data members
        public E element() { return e; } // Return the value
        public URNode<E> next() { return n; } // Return next link
        public URNode<E> setNext(URNode<E> nextval) { return n = nextval; } // Set next link
        public URNode<E> prev() { return p; } // Return prev link
        public URNode<E> setPrev(URNode<E> prevval) { return p = prevval; } // Set prev link
    }
    private class URLinkIterator<E> implements Iterator<E> {
        URNode<E> current;
        public URLinkIterator(URNode<E> head) {
            this.current = head;
        }

        //assuming the boolean determines whether the next node relative the first exists
        public boolean hasNext() {
            return current != null;
        }

        //assuming that the return type is the element at hand and then it iterators to the next value
        public E next() {
            if (!hasNext()) {
                throw new NoSuchElementException("There exists no element in this list to traverse. Try adding some.");
            }

            E value = current.element();
            current = current.next();
            return value;
        }
        //no return type

        public void remove() {
            (current.next()).setPrev(current.prev());
            (current.prev()).setNext(current.next());
        }
    }
}