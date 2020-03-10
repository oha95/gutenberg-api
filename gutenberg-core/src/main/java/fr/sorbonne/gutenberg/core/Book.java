package fr.sorbonne.gutenberg.core;

import fr.sorbonne.gutenberg.core.indexation.Index;
import fr.sorbonne.gutenberg.core.utils.KMP;
import fr.sorbonne.gutenberg.core.utils.Position;
import fr.sorbonne.gutenberg.core.utils.regex.RegEx;

import java.util.List;
import java.util.Objects;

public class Book {

    private String bookId;

    public Book(String bookId) {
        this.bookId = bookId;
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
