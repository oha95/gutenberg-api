package fr.sorbonne.gutenberg.core.indexation;

import fr.sorbonne.gutenberg.core.utils.Position;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Index {

    private String bookId;
    private Map<String, List<Position>> index = new TreeMap<>();
    private PatriciaTree tree;

    public Index(String fileName) {
        bookId = fileName;
        loadIndex();
    }

    private void loadIndex(){
        try {
            Files.lines(Paths.get(IndexGenerator.indexPath + bookId))
                    .forEach(this::processLine);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processLine(String line){
        String[] split = line.split(":");
        index.put(split[0], processValues(split[1]));
    }

    private List<Position> processValues(String values){
        values = values.replaceAll("\\[", "").replaceAll("]", "");
        return Arrays.stream(values.split(", ")).map(this::processValue).collect(Collectors.toList());
    }

    private Position processValue(String value){
        String[] split = value.split("-");
        return new Position(Integer.valueOf(split[0]), Integer.valueOf(split[1]));
    }

    public Map<String, List<Position>> getIndex(){
        return index;
    }

    private PatriciaTree getTree(){
        if (tree == null)
            tree = new PatriciaTree(index);
        return tree;
    }

    public ArrayList<Position> find(String mot){
        return getTree().find(mot);
    }
}

