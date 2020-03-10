package fr.sorbonne.gutenberg.core.utils;

public class Position {

    private int line;
    private int colonne;

    public Position(int line, int colonne) {
        this.line = line;
        this.colonne = colonne;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public int getColonne() {
        return colonne;
    }

    public void setColonne(int colonne) {
        this.colonne = colonne;
    }

    @Override
    public String toString() {
        return line + "-" + colonne ;
    }
}
