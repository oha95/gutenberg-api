package fr.sorbonne.gutenberg.core.utils;


import fr.sorbonne.gutenberg.core.indexation.IndexGenerator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class KMP {

    private char[] text;
    private char[] facteur;
    private int[] retenue;

    KMP(){

    }

    KMP(String text, String facteur) {
        this.text = text.toCharArray();
        this.facteur = facteur.toCharArray();
        this.retenue = calculRetenue(this.facteur);
    }

    KMP(String facteur){
        this.facteur = facteur.toCharArray();
        this.retenue = calculRetenue(this.facteur);
    }

    public List<Position> findInFile(String fileName){
        ArrayList<Position> positions = new ArrayList<>();
        AtomicReference<Integer> lineNumber = new AtomicReference<>(0);
        try {
            Files.readAllLines(Paths.get(fileName))
                    .stream()
                    .map(String::toLowerCase)
                    .forEach(line -> {
                        lineNumber.set(lineNumber.get() + 1);
                        matchAll(line).forEach(col -> positions.add(new Position(col, lineNumber.get())));
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return positions;
    }

    private int[] calculRetenue(char[] facteur){
        int[] retenue = new int[facteur.length + 1];
        int i = 0, j = 0;
        retenue[0] = -1;
        while (++i < facteur.length )
            if (facteur[i] == facteur[0]) { retenue[i] = -1; j++; }
            else
            if (facteur[i] != facteur[j]){ retenue[i] = j; j = 0; }
            else j++;
        return retenue;
    }

    int match(){
        return match(0);
    }

    private int match(int pos){
        int i = pos; int j = 0;
        while(i < text.length){
            if (j == facteur.length) return i - facteur.length;
            if (facteur[j] == text[i]){ i++; j++; }
            else
            if (retenue[j] == -1) { i++; j = 0; }
            else j = retenue[j];
        }
        if (j == facteur.length) return i - j; else return -1;
    }

    List<Integer> matchAll(String text){
        setText(text);
        return matchAll();
    }

    List<Integer> matchAll(){
        int index, p;
        List<Integer> pos = new ArrayList<>();
        p = match();
        while (p != -1){
            pos.add(p);
            index = p + 1;
            p = match(index);
        }
        return pos;
    }

    public char[] getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text.toCharArray();
    }

    public char[] getFacteur() {
        return facteur;
    }

    public void setFacteur(String facteur) {
        this.facteur = facteur.toCharArray();
        this.retenue = calculRetenue(this.facteur);
    }

    public static List<Position> findInFile(String bookId, String found) {
        return new KMP(found).findInFile(IndexGenerator.booksPath + bookId);
    }

    public static int match(String text, String found){
        return new KMP(text, found).match();
    }

    public static List<Integer> matchAll(String text, String found){
        return new KMP(text, found).matchAll();
    }
}
