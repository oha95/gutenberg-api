package fr.sorbonne.gutenberg.core.SEO;

import fr.sorbonne.gutenberg.core.Book;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class JaccardGraph {
    private int size = 0;
    private Map<Integer, JaccardNode> nodes;
    private JaccardMat mat;
    private final double threshold = 0.8;

    public JaccardGraph() {
        this.nodes = new HashMap<>();
        mat = new JaccardMat();
    }

    public Map<Integer, JaccardNode> getNodes() {
        return nodes;
    }

    public void init(List<String> books) {
        books.stream()
                .parallel()
                .map(this::createNode)
                .sorted(Comparator.comparingInt(JaccardNode::getId))
                .collect(Collectors.toList())
                .forEach(this::addNewNode);
    }

    private void addNewNode(JaccardNode node) {
        IntStream.range(0, node.getId())
                .parallel()
                .forEach(i ->
                        mat.set(i, node.getId(), (byte) (nodes.get(i).calculateDistance(node) < threshold ? 1 : 0)));
    }

    public void addNewBook(String bookId) {
        addNewNode(createNode(bookId));
    }

    private JaccardNode createNode(String bookId) {
        JaccardNode node = new JaccardNode(bookId);
        nodes.put(node.getId(), node);
        return node;
    }

    public byte[][] getMat() {
        mat.trimToSize(size);
        return mat.getMat();
    }

    public void saveMat() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("books/jaccardMatrix.dat"))) {
            oos.writeObject(getMat());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadMat() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("books/jaccardMatrix.dat"))) {
            mat.mat = (byte[][]) ois.readObject();
            size = mat.mat.length;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    public class JaccardNode {
        private int id;
        private Book book;
        private Map<String, Integer> jaccardIndex;

        JaccardNode(String bookPath) {
            this.book = new Book(bookPath);
            this.id = size++;
            this.jaccardIndex = calculateIndex();
        }

        private Map<String, Integer> calculateIndex() {
            return book.getIndex().getIndex()
                    .entrySet()
                    .stream()
                    .parallel()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().size()));
        }

        private double calculateDistance(JaccardNode other) {
            AtomicReference<Integer> numerator = new AtomicReference<>(0);
            AtomicReference<Integer> denumerator = new AtomicReference<>(0);
            Stream.of(jaccardIndex.keySet(), other.jaccardIndex.keySet())
                    .flatMap(Collection::stream)
                    .collect(Collectors.toSet())
                    .stream()
                    .parallel()
                    .forEach(key -> {
                        int r = Math.abs(jaccardIndex.getOrDefault(key, 0) - other.jaccardIndex.getOrDefault(key, 0));
                        numerator.updateAndGet(v -> v + r);
                        int d = Math.max(jaccardIndex.getOrDefault(key, 0), other.jaccardIndex.getOrDefault(key, 0));
                        denumerator.updateAndGet(v -> v + d);
                    });
            return (double) numerator.get() / denumerator.get();
        }

        public int getId() {
            return id;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            JaccardNode that = (JaccardNode) o;
            return id == that.id;
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }

    private class JaccardMat {
        private byte[][] mat;
        private int size = 2000;

        private JaccardMat() {
            mat = new byte[size][size];
        }

        private void set(int i, int j, byte v) {
            if (i >= size || j >= size)
                mat = copy(Math.max(i, j));
            mat[i][j] = mat[j][i] = v;
        }

        private byte get(int i, int j) {
            return mat[i][j];
        }

        private void trimToSize(int newLength) {
            if (newLength < mat.length) {
                mat = (newLength == 0)
                        ? null
                        : copy(newLength);
            }
        }

        private byte[][] copy(int newLength) {
            byte[][] copy = new byte[newLength][newLength];
            for (int i = 0; i < newLength; i++)
                System.arraycopy(mat[i], 0, copy[i], 0, newLength);
            return copy;
        }

        public byte[][] getMat() {
            return mat;
        }

    }
}
