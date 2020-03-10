package fr.sorbonne.gutenberg.core;

import fr.sorbonne.gutenberg.core.indexation.Index;
import fr.sorbonne.gutenberg.core.utils.KMP;
import fr.sorbonne.gutenberg.core.utils.Position;
import fr.sorbonne.gutenberg.core.utils.regex.RegEx;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class Book {

    private String bookId;
    private String bookName;
    private String bookAuthor;
    private Stream<String> bookTextStream;

    public Book(String bookId) {
        this.bookId = bookId;
    }

    public Book(InputStream bookStream){
        Stream<String> lines = new BufferedReader(new InputStreamReader(bookStream)).lines();
    }

    public String getBookName(){
        return bookName;
    }

    public String getBookAuthor(){
        return bookAuthor;
    }

    public Index getIndex() {
        return new Index(bookId);
    }

    public String getBookId() {
        return bookId;
    }

    public List<Position> find(String str) {
        if (str.matches("[^a-zA-Z_']"))
            return getIndex().find(str);
        else if (str.matches("[^a-zA-Z0-9_\\-' ]"))
            return KMP.findInFile(bookId, str);
        else
            return RegEx.findInFile(bookId, str);
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
}
