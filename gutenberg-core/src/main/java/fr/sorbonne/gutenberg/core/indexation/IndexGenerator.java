package fr.sorbonne.gutenberg.core.indexation;


import fr.sorbonne.gutenberg.core.Book;
import fr.sorbonne.gutenberg.core.utils.KMP;
import fr.sorbonne.gutenberg.core.utils.Position;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IndexGenerator {

    public static final String booksPath = "books/books/";
    public static final String indexPath = "books/index/";
    public static final String liteIndexPath = "books/liteIndex/";

    private String bookId;
    private Map<String, List<Position>> index = new TreeMap<>();
    private int lineNumber = 0;
    private Stream<String> lines;

    public IndexGenerator(String fileName) {
        bookId = fileName;
        try {
            lines = Files.lines(Paths.get(bookId));
        } catch (IOException e) {
            e.printStackTrace();
        }
        index = getListWords();
    }

    public IndexGenerator(InputStream file) {
        lines = new BufferedReader(new InputStreamReader(file)).lines();
        index = getListWords();
    }

    private Map<String, List<Position>> getListWords() {
        lines.map(String::toLowerCase).forEach(this::processLine);
        return index;
    }

    private void processLine(String line) {
        lineNumber++;
        Arrays.stream(line.split("[^a-zA-Z_']"))
                .distinct()
                .map(String::toLowerCase)
                .filter(word -> word.length() > 3)
                .forEach(word -> addWord(word, processWord(line, word, lineNumber)));
    }

    private List<Position> processWord(String line, String word, int lineNumber) {
        return KMP.matchAll(line, word)
                .stream()
                .map(i -> new Position(lineNumber, i))
                .collect(Collectors.toList());
    }

    private synchronized void addWord(String word, List<Position> pairs) {
        index.computeIfAbsent(word, key -> new ArrayList<>()).addAll(pairs);
    }

    public ByteArrayOutputStream getOutput() {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        index.entrySet()
                .stream()
                .map(e -> e.getKey() + ":" + e.getValue() + "\n")
                .forEach(e -> {
                    try {
                        os.write(e.getBytes());
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                });
        return os;
    }

    private static void writeIndex(String filePath, Map<String, ?> index) {
        try {
            Files.write(Paths.get(filePath), (Iterable<String>) index.entrySet()
                    .stream()
                    .map(e -> e.getKey() + ":" + e.getValue())::iterator);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void generateIndex(String bookId) {
        writeIndex(indexPath + bookId, new IndexGenerator(booksPath + bookId).index);
    }

}
