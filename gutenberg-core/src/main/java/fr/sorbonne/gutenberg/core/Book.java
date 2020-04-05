package fr.sorbonne.gutenberg.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.sorbonne.gutenberg.core.indexation.Index;
import fr.sorbonne.gutenberg.core.indexation.IndexGenerator;
import fr.sorbonne.gutenberg.core.utils.KMP;
import fr.sorbonne.gutenberg.core.utils.Position;
import fr.sorbonne.gutenberg.core.utils.regex.RegEx;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Book {

    private String bookId;
    private String title;

    public String getTitle() {
        return title;
    }

    public Book(String bookId) {
        this.bookId = bookId;
        this.title = makeBookTitle();
    }

    public Book(String bookId, String title) {
        this.bookId = bookId;
        this.title = title;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    @JsonIgnore
    public Index getIndex() {
        return new Index(bookId);
    }

    public List<Position> find(String str) {
        if (str.matches("[a-zA-Z0-9_\\-' ]+"))
            return KMP.findInFile(bookId, str);
        else
            return RegEx.findInFile(bookId, str);
    }

    public String makeBookTitle() {
        if (this.title != null) return this.title;

        String title = null;
        try {
            title = Files.lines(Paths.get(IndexGenerator.booksPath + bookId))
                    .filter(s -> s.startsWith("Title: "))
                    .collect(Collectors.toList()).get(0);
            List<String> list = Files.readAllLines(Paths.get(IndexGenerator.booksPath + bookId));
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).equals(title)) {
                    title = title + " " + list.get(i + 1).trim();
                    break;
                }
            }
            return title;
        } catch (Exception e) {
            return null;
        }

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equals(bookId, book.bookId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookId);
    }

    @Override
    public String toString() {
        return "Book{" +
                "bookId='" + bookId + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
