package fr.sorbonne.gutenberg.core.indexation;

import fr.sorbonne.gutenberg.core.utils.Position;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

class PatriciaTree implements Serializable {

    private Node root;
    private Map<String, List<Position>> list;

    PatriciaTree(Map<String, List<Position>> list) {
        this.list = list;
        root = listToTree(list);
    }

    private Node listToTree(Map<String, List<Position>> list) {
        Node root = new Node();
        return listToTree(new ArrayList<>(list.keySet()), 0, root);
    }

    private Node listToTree(List<String> mots, int index, Node root) {
        if (mots.size() == 1)
            return new Node(mots.get(0), list.get(mots.get(0)));
        Iterator<String> iter = mots.iterator();
        String mot = iter.next();
        while (iter.hasNext()) {
            ArrayList<String> sameBegin = new ArrayList<>();
            sameBegin.add(mot);
            if (mot.length() == index) {
                root.pairs = list.get(mot);
                mots.remove(mot);
                root.addChild(listToTree(mots, index, root));
                return root;
            }
            char beginWhit = mot.charAt(index);
            String mot2 = "";
            while (iter.hasNext()) {
                mot2 = iter.next();
                if (mot2.charAt(index) == beginWhit) sameBegin.add(mot2); else break;
            }
            Node node = new Node(mot.substring(0, index + 1));
            root.addChild(listToTree(sameBegin, index + 1, node));
            if (!iter.hasNext() && mot2.charAt(index) != beginWhit) {
                root.addChild(new Node(mot2, list.get(mot2))); break; }
            mot = mot2;
        }
        return root;
    }

    ArrayList<Position> find(String mot) {
        return new ArrayList<>(root.find(mot.toLowerCase()));
    }

    private class Node implements Serializable {
        String value;
        List<Position> pairs = new ArrayList<>();
        List<Node> child = new ArrayList<>();

        Node() {

        }

        Node(String value) {
            this.value = value;
        }

        Node(String value, List<Position> pairs) {
            this.value = value;
            this.pairs = pairs;
        }

        void addChild(Node node) {
            if (!isChild(node))
                child.add(node);
        }

        boolean isChild(Node node) {
            if (node == this) return true;
            for (Node fils : child)
                if (fils.isChild(node))
                    return true;
            return false;
        }

        ArrayList<Position> getAllPosition() {
            ArrayList<Position> positions = new ArrayList<>(pairs);
            for (Node fils : child)
                positions.addAll(fils.getAllPosition());
            return positions;
        }

        ArrayList<Position> find(String mot) {
            if (value != null && value.startsWith(mot)) return getAllPosition();
            for (Node fils : child)
                if (mot.startsWith(fils.value)) return fils.find(mot);
                else if (fils.value.startsWith(mot)) return fils.getAllPosition();
            return null;
        }
    }
}