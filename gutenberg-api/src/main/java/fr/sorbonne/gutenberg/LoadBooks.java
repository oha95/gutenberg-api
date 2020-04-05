package fr.sorbonne.gutenberg;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.ObjectInputStream;

@Configuration
@Slf4j
public class LoadBooks {

    @Autowired
    public static java.util.Map<String, String> result;


    @org.springframework.context.annotation.Bean
    CommandLineRunner initBooks() throws IOException, ClassNotFoundException {
        log.info("Request init Books");
        long a = System.currentTimeMillis();
        java.io.InputStream fis = new ClassPathResource("books.dat").getInputStream();
        ObjectInputStream ois = new ObjectInputStream(fis);
        result = (java.util.Map<String, String>) ois.readObject();
        ois.close();
        return args ->  log.info("books loaded");
    }

}

