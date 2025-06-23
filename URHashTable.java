//Erik Enriquez - eenrique@u.rochester.edu

import java.util.Iterator;

class URHashTable<Key,Value> implements Iterable<Key> {
    private static final int INIT_CAPACITY = 5 ;
    public int length; // size of the data set
    public int allocationSize; // size of the hash table
    private double loadFactor;
    private Key[] keys;
    private Value[] values;
    private int deletions;
    //creates a hashtable with the base attribution size of 5
    public URHashTable() {
        keys = (Key[]) new Object[INIT_CAPACITY];
        values = (Value[]) new Object[INIT_CAPACITY];
        this.length = 0;
        this.allocationSize = INIT_CAPACITY;
        this.loadFactor = 0.75;
    }
    //creates a hashtable with the base attribution size of cap
    public URHashTable(int cap) {
        keys = (Key[]) new Object[cap];
        values = (Value[]) new Object[cap];
        this.length = 0;
        this.allocationSize = cap;
        this.loadFactor = 0.75;
    }
    //returns a hashcode int that will be used with linear probing
    public int hash(Key key) {
        return Math.abs(key.hashCode()) % allocationSize;
    }
    //adds a key and value to the hash table using linear probing
    public void put(Key key, Value val) {
        if ( ((double) (length - deletions)/allocationSize) >= loadFactor) {
            resize();
        }
        int index = hash(key);
        if (keys[index] == null || keys[index] == "emptyAfterRemoval") {
            values[index] = val;
            keys[index] = key;
            length = length + 1;
        } else {
            while(keys[index] != null && keys[index] != "emptyAfterRemoval") {
                //duplicate, override
                if(keys[index].equals(key)) {
                    values[index] = val;
                    break;
                }
                //reached end, go to start
                if (index == allocationSize - 1) {
                    index = -1;
                }
                index = index + 1;
            }
            values[index] = val;
            keys[index] = key;
            length = length + 1;
        }
    }
    // returns the value at a key if it exists, if not it will return null
    public Value get(Key key) {
        if (search(key) != -1) {
            return values[search(key)];
        } else {
            return null;
        }
    }
    //removes a key and value from the hashtable and turns the values int "emptyafterremoval"
    public void delete(Key key) {
        if (search(key) >= 0) {
            values[search(key)] = (Value) "emptyAfterRemoval";
            keys[search(key)] = (Key) "emptyAfterRemoval";
            deletions = deletions + 1;
        } else {
            System.out.println(key + " was not found");
        }
    }
    //returns the items in use in the hashtable
    public int size() {
        return length - deletions;
    }
    //returns true or false if the hashtable is empty
    public boolean isEmpty() {
        return (this.size() == 0);
    }
    //returns true or false if the key is contained in the hashtable
    public boolean contains(Key key) {
        return (this.get(key) != null);
    }
    //returns and iterator for keys
    public Iterator<Key> iterator() {
        return new URHashIterator<>(keys);
    }

    //toString function used to view the contents of the hashmap
    public String toString() {
        String table = "";
        int i = 0;
        while (i < keys.length) {
            if (keys[i] != null && keys[i] != "emptyAfterRemoval") {
                table = table + keys[i] + ":" + values[i] + "\n";
                i++;
            } else {i++;}
        }
        return table;
    }
    public URLinkedList<Key> keys() {
        URLinkedList<Key> linkedList = new URLinkedList<>();

        for (Key key : this) {
            if (key != null) {
                linkedList.add(key);
            }
        }
        return linkedList;
    }

    public URLinkedList<Value> values() {
        URLinkedList<Value> linkedList = new URLinkedList<>();

        for (Key key : this) {
            if (key != null) {
                linkedList.add(this.get(key));
            }
        }
        return linkedList;
    }
    // helper function used for creating more space in the hashtable
    private void resize() {
        URHashTable<Key, Value> temp = new URHashTable<>(allocationSize*20);

        int i = 0;
        while (i < keys.length) {
            if (keys[i] != null || keys[i] == "emptyAfterRemoval") {
                temp.put(keys[i], values[i]);
                i++;
            } else {i++;}
        }

        //apply the changes
        allocationSize = allocationSize * 20;
        keys = temp.keys;
        values = temp.values;
    }
    //helper function used to locate a key
    private int search(Key key) {
        int index = hash(key);

        while(true) {
            if (keys[index] == null) {
                return -1;
            } else if (key.equals(keys[index])) {
                return index;
            } else {
                index = (index + 1) % allocationSize;
            }
        }
    }
}
//iterator class
class URHashIterator<Key> implements Iterator<Key> {
    int i = 0;
    Key[] keys;
    public URHashIterator(Key[] keys) {
        this.keys = keys;
    }

    @Override
    public boolean hasNext() {
        return i < keys.length;
    }

    @Override
    public Key next() {
        Key value = keys[i];
        while (hasNext()) {
            if (keys[i] != null && keys[i] != "emptyAfterRemoval") {
                value = keys[i];
                i++;
                return value;
            } else {
                i++;
            }
        }
        return null;
    }
}