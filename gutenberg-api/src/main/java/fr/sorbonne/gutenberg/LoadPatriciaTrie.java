package fr.sorbonne.gutenberg;

import fr.sorbonne.gutenberg.core.indexation.PatriciaTrie;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.*;

@Configuration
@Slf4j
public class LoadPatriciaTrie {

    @Autowired
    public static PatriciaTrie<java.util.List<String>> result;


    @org.springframework.context.annotation.Bean
    CommandLineRunner initPatriciaTrie() throws IOException, ClassNotFoundException {
        log.info("Request init PatriciaTree");
        InputStream fis = new ClassPathResource("patriciaTree.dat").getInputStream();
        ObjectInputStream ois = new ObjectInputStream(fis);
        result = (PatriciaTrie<java.util.List<String>>) ois.readObject();
        ois.close();
        return args ->  log.info("PatriciaTreeCreated");
    }

}
