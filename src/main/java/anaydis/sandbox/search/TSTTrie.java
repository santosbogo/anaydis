package anaydis.sandbox.search;

import anaydis.search.Map;

import java.util.*;

public class TSTTrie<T> implements Map<String, T> {
    int size = 0;
    TSTNode<T> root = null;

    private TSTNode<T> find(TSTNode<T> node, String key, int level) {
        if (node == null) return null;

        char c = key.charAt(level);

        if (c < node.c) return find(node.left, key, level);
        else if (c > node.c) return find(node.right, key, level);
            //Now we found the next char if the key (c == node.c). We need to ask if it is the las char of the key.
            // key.length() - 1 is because when we are at that level means it is the last character of the key, so we want that node
        else if (level < key.length() - 1) return find(node.middle, key, level + 1);
        return node;
    }

    @Override
    public boolean containsKey(String key) {
        if (key == null) throw new NullPointerException();
        return get(key) != null;
    }

    @Override
    public T get(String key) {
        if (key == null) throw new NullPointerException();
        TSTNode<T> node = find(root, key, 0);
        return ((node != null) && (node.value != null)) ? node.value : null;
    }

    @Override
    public T put(String key, T value) {
        if (key == null) throw new NullPointerException();
        Tuple<T> tuple = put(root, key, value, 0);
        root = tuple.getNode();
        return tuple.getValue();
    }

    private Tuple<T> put(TSTNode<T> node, String key, T value, int level) {
        char c = key.charAt(level);
        T oldValue = null;

        if (node == null) {
            node = new TSTNode<>();
            node.c = c;
        }

        if (c < node.c) node.left = put(node.left, key, value, level).getNode();
        else if (c > node.c) node.right = put(node.right, key, value, level).getNode();
        else if (level < key.length() - 1) node.middle = put(node.middle, key, value, level + 1).getNode();
        else {
            if (node.value != null) {
                oldValue = node.value;
            } else size++;
            node.value = value;
        }

        return new Tuple<>(node, oldValue);
    }

    @Override
    public Iterator<String> keys() {
        List<String> keys = new ArrayList<>(size);
        getKeys(root, "", keys);
        return keys.iterator();
    }

    private void getKeys(TSTNode<T> node, String prefix, List<String> keys) {
        if (node == null) return;

        if (node.value != null) keys.add(prefix + node.c);

        getKeys(node.left, prefix, keys);

        getKeys(node.right, prefix, keys);

        getKeys(node.middle, prefix + node.c, keys);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        size = 0;
        root = null;
    }

    public List<String> autocomplete(String pattern){
        final List<String> keys = new ArrayList<>();
        autocomplete(root, keys, pattern, 0, "");
        return keys;
    }
    private void autocomplete(TSTNode<T> node, List<String> keys, String pattern, int level, String match){
        if(node == null) return;

        if(level < pattern.length()){
            char c = pattern.charAt(level);

            if(c < node.c) autocomplete(node.left, keys, pattern, level, match);
            else if(c > node.c) autocomplete(node.right, keys, pattern, level, match);
            else autocomplete(node.middle, keys, pattern, level + 1, match + node.c);
        }
        else{
            if(node.value != null)
                keys.add(match + node.c);
            autocomplete(node.left, keys, pattern, level, match);
            autocomplete(node.right, keys, pattern, level, match);
            autocomplete(node.middle, keys, pattern, level + 1, match + node.c);
        }
    }
    private void keysWithPrefix(TSTNode<T> node, List<String> keys, String pattern, int level, String match){
        if(node == null) return;

        if(level < pattern.length()){
            char c = pattern.charAt(level);

            if(c < node.c) {
                keysWithPrefix(node.left, keys, pattern, level, match);
            }
            else if(c > node.c) {
                keysWithPrefix(node.right, keys, pattern, level, match);
            } else {
                keysWithPrefix(node.middle, keys, pattern, level + 1, match + node.c);
            }
        }
        else{
            if(node.value != null)
                keys.add(match + node.c);

            keysWithPrefix(node.left, keys, pattern, level, match);
            keysWithPrefix(node.right, keys, pattern, level, match);
            keysWithPrefix(node.middle, keys, pattern, level + 1, match + node.c);
        }
    }


    public String longestPrefixOf(String pattern){
        return longestPrefixOf(root, pattern, "", "", 0);
    }
    private String longestPrefixOf(TSTNode<T> node, String pattern, String match, String lastKey, int level){
        if (node == null) return lastKey;

        char c = pattern.charAt(level);

        if(c < node.c) return longestPrefixOf(node.left, pattern, match, lastKey, level);
        else if (c > node.c) return longestPrefixOf(node.right, pattern, match, lastKey, level);
        else{
            if (node.value != null) lastKey = match + node.c;
            if (level < pattern.length() - 1)
                return longestPrefixOf(node.middle, pattern, match + node.c, lastKey, level + 1);
        }
        return lastKey;
    }

    public List<String> wildcard(String pattern){
        List<String> keys = new ArrayList<>();
        wildcard(root, pattern, keys, "", 0);
        return keys;
    }
    private void wildcard(TSTNode<T> node, String pattern, List<String> keys, String match, int level){
        if (node == null || level >= pattern.length()) return;

        char c = pattern.charAt(level);

        if (level == pattern.length() - 1 && node.value != null)
            keys.add(match + node.c);

        if (c == '.'){
            wildcard(node.left, pattern, keys, match, level);
            wildcard(node.right, pattern, keys, match, level);
            wildcard(node.middle, pattern, keys, match + node.c, level + 1);
        }else{

            if (c < node.c)
                wildcard(node.left, pattern, keys, match, level);
            else if (c > node.c) wildcard(node.right, pattern, keys, match, level);
            else wildcard(node.middle, pattern, keys, match + node.c, level + 1);
        }
    }
    private void keysThatMatch(TSTNode<T> node, String pattern,List<String> keys, String match, int level){
        if (node == null || match.length() == pattern.length()) return;

        char c = pattern.charAt(level);

        if(pattern.length() - 1 == level && node.value != null){
            keys.add(match + node.c);
        }

        if(c == '.'){
            keysThatMatch(node.left, pattern, keys, match, level);
            keysThatMatch(node.right, pattern, keys, match, level);
            keysThatMatch(node.middle, pattern, keys, match + node.c, level + 1);
        }
        else{
            if (c < node.c) keysThatMatch(node.left, pattern, keys, match, level);
            else if (c > node.c) keysThatMatch(node.right, pattern, keys, match, level);
            else keysThatMatch(node.middle, pattern, keys, match + node.c, level + 1);
        }
    }


    public static void main(String[] args) {

        //AUTOCOMPLETE
        System.out.println("\nAutocomplete:\nExpected: lupani, lupin");
        TSTTrie<Integer> map = new TSTTrie<>();
        final String[] keys = {"lucas", "lupani", "lupin", "luz", "luana", "tractor", "laura"};
        Iterator<String> wordsIter = Arrays.stream(keys).iterator();
        int value = 0;
        while (wordsIter.hasNext()){
            map.put(wordsIter.next(), value++);
        }
        System.out.println(map.autocomplete("lup"));

        //LONGEST PREFIX OF
        System.out.println("\nLongest prefix of:\nExpected: she, shells");
        map = new TSTTrie<>();
        final String[] keys2 = {"she", "sells", "sea", "shells", "by", "the", "sea", "shore"};
        wordsIter = Arrays.stream(keys2).iterator();
        value = 0;
        while (wordsIter.hasNext()){
            map.put(wordsIter.next(), value++);
        }
        System.out.println(map.longestPrefixOf("shell"));
        System.out.println(map.longestPrefixOf("shellsort"));

        //KEYS THAT MATCH
        System.out.println("\nKeys that match:\nExpected: she, the");
        map = new TSTTrie<>();
        final String[] keys3 = {"she", "sells", "sea", "shells", "by", "the", "sea", "shore"};
        wordsIter = Arrays.stream(keys3).iterator();
        value = 0;
        while (wordsIter.hasNext()){
            map.put(wordsIter.next(), value++);
        }
        System.out.println(map.wildcard(".he") + "\nExpected: sea, she");
        System.out.println(map.wildcard("s..") + "\nExpected: sells, shore");
        System.out.println(map.wildcard("....."));


    }
}
