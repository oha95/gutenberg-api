package fr.sorbonne.gutenberg.ressources;

import fr.sorbonne.gutenberg.core.Book;
import fr.sorbonne.gutenberg.core.indexation.IndexGenerator;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/search")
public class Research {
    
    @GetMapping("/{r}")
    public String search(@PathVariable("r") String r){
        try {
            return Files.list(Paths.get(IndexGenerator.booksPath))
                    .parallel()
                    .map(path -> path.toFile().getName())
                    .filter(bookId -> new Book(bookId).find(r).size() > 0)
                    .collect(Collectors.toList())
                    .toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "no result";
    }
}
