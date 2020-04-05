package fr.sorbonne.gutenberg.core.indexation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

public class PatriciaTrie<T> implements Serializable {

    private PatriciaTrieNode root = new PatriciaTrieNode();

    private class PatriciaTrieNode implements Serializable{
        private String label;
        private ArrayList<PatriciaTrieNode> children = new ArrayList<>();
        private HashSet<T> items = new HashSet<T>();

        public PatriciaTrieNode() {

        }

        public PatriciaTrieNode(String label, T item) {
            this.label = label;
            this.items.add(item);
        }

        public PatriciaTrieNode(String label, ArrayList<PatriciaTrieNode> childrenListReference, HashSet<T> itemsList) {
            this.label = label;
            this.children = childrenListReference;
            this.items = (HashSet<T>) (itemsList.clone());
        }

        public PatriciaTrieNode search(String s) {
            int l = 0, r = children.size() - 1, pos = 0;
            char tmp_c, c;
            try {
                c = s.charAt(0);
            } catch (StringIndexOutOfBoundsException e) {
                return null;
            }
            while (l <= r) {
                pos = (l + r) / 2;
                PatriciaTrieNode child = children.get(pos);
                String label = child.label;
                int l_len = label.length();
                tmp_c = label.charAt(0);
                if (tmp_c == c) {
                    int i = 1;
                    int s_len = s.length();

                    int n = Math.min(s_len, l_len);
                    for (; i < n; i++) {
                        if (s.charAt(i) != label.charAt(i)) {
                            break;
                        }
                    }
                    if (i == s_len) {
                        return child;
                    } else if (i == l_len) {
                        return child.search(s.substring(i));
                    } else {
                        return null;
                    }
                } else if (tmp_c < c) {
                    l = pos + 1;
                } else {
                    r = pos - 1;
                }
            }
            return null;
        }

        public void insertChild(String s, T item) {
            int size = children.size(), l = 0, r = size - 1, pos = 0;
            int s_len = s.length();
            char tmp_c, c;
            try {

                c = s.charAt(0);
            } catch (StringIndexOutOfBoundsException e) {
                return;
            }

            while (l <= r) {
                pos = (l + r) / 2;
                PatriciaTrieNode child = children.get(pos);
                String label = child.label;

                tmp_c = label.charAt(0);
                if (tmp_c == c) {

                    int l_len = label.length();
                    int n = Math.min(l_len, s_len);
                    int i = 1;

                    for (; i < n; i++) {
                        if (label.charAt(i) != s.charAt(i)) {
                            break;
                        }
                    }

                    if (i < l_len) {
                        String restOfl = label.substring(i);
                        child.label = s.substring(0, i);

                        PatriciaTrieNode new_child = new PatriciaTrieNode(restOfl, child.children, child.items);
                        child.children = new ArrayList<>(2);

                        child.children.add(new_child);
                        child.items.add(item);
                        if (i < s_len) {
                            String restOfs = s.substring(i);
                            new_child = new PatriciaTrieNode(restOfs, item);
                            if (restOfl.compareTo(restOfs) < 0) {
                                child.children.add(new_child);
                            } else {
                                child.children.add(0, new_child);
                            }
                        }
                    } else if (i < s_len) {
                        child.items.add(item);
                        String restOfs = s.substring(i);
                        child.insertChild(restOfs, item);
                    } else {

                        child.items.add(item);
                    }
                    return;
                } else if (tmp_c < c) {
                    l = pos + 1;
                } else {
                    r = pos - 1;
                }
            }
            PatriciaTrieNode node = new PatriciaTrieNode(s, item);
            this.children.add(l, node);
            return;
        }
    }

    public void insertString(String label, T item) {
        root.insertChild(label, item);
    }

    public HashSet<T> search(String s) {
        try {
            return root.search(s).items;
        } catch (NullPointerException e) {
            return null;
        }
    }
}