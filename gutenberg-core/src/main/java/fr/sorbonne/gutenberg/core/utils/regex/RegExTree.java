package fr.sorbonne.gutenberg.core.utils.regex;


import java.util.ArrayList;

//UTILITARY CLASS
class RegExTree {

    int root;
    ArrayList<RegExTree> subTrees;

    RegExTree(int root, ArrayList<RegExTree> subTrees) {
        this.root = root;
        this.subTrees = subTrees;
    }

    //FROM TREE TO PARENTHESIS
    public String toString() {
        if (subTrees.isEmpty()) return rootToString();
        StringBuilder result = new StringBuilder(rootToString() + "(" + subTrees.get(0).toString());
        for (int i = 1; i < subTrees.size(); i++)
            result.append(",").append(subTrees.get(i).toString());
        return result + ")";
    }

    private String rootToString() {
        if (root == RegEx.CONCAT) return ".";
        if (root == RegEx.ETOILE) return "*";
        if (root == RegEx.ALTERN) return "|";
        if (root == RegEx.DOT) return ".";
        return Character.toString((char) root);
    }
}

