//Erik Enriquez - eenrique@u.rochester.edu
public class URStack <E> {
    private URLinkedList<E> stack = new URLinkedList<>();
    public boolean push(E element) {
        return stack.add(element);
    }
    public E pop() {
        return stack.pollLast();
    }
    public E peek() {
        if (stack.isEmpty()) {return null;}
        return stack.peekLast();
    }
    public boolean isEmpty() {
        return stack.isEmpty();
    }
    public int search(E element) {
        return stack.indexOf(element);
    }
    public int size() {return stack.size();}
    public String toString() {
        return stack.toString();
    }
}