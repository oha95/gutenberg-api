package fr.sorbonne.gutenberg.ressources;


import fr.sorbonne.gutenberg.GutenbergApplication;
import fr.sorbonne.gutenberg.LoadBooks;
import fr.sorbonne.gutenberg.LoadPatriciaTrie;
import fr.sorbonne.gutenberg.core.Book;
import fr.sorbonne.gutenberg.core.indexation.Index;
import fr.sorbonne.gutenberg.core.indexation.IndexGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@CrossOrigin(origins = "http://gutenberg.com.s3-website.eu-west-3.amazonaws.com")
@RequestMapping("/api/search")
public class Research {

    @GetMapping("/{r}")
    public List<Book> search(@PathVariable("r") String r){
        List<Book> bookList = new ArrayList<>();
        log.info("Request for : "+r);
        try {
            if (r.matches("[a-zA-Z_']+")){
                LoadPatriciaTrie.result.search(r.toLowerCase())
                        .stream()
                        .flatMap(Collection::stream)
                        .collect(Collectors.toSet())
                        .forEach(s -> bookList.add(new Book(s, LoadBooks.result.get(s))));
                return bookList;
            }else{
                Files.list(Paths.get(IndexGenerator.booksPath))
                        .parallel()
                        .map(path -> path.toFile().getName())
                        .filter(bookId -> new Book(bookId).find(r).size() > 0)
                        .forEach(s -> bookList.add(new Book(s, LoadBooks.result.get(s))));
                return bookList;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}

