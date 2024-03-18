package com.mj.mysns.any;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;

public class FilesTest {

    @Test
    void files() {
        Path path = Path.of("/Users/mj/work/sns", "data", "mock", "mock_post.csv");
        try {
            List<String> line = Files.readAllLines(path);
            String first = line.get(1);
//            System.out.println(first);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void tokenizer() {
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setDelimiter(",");
        tokenizer.setNames("username", "first", "last", "email");

        Path path = Path.of("/Users/mj/work/sns", "data", "mock", "mock_user.csv");
        try {
            List<String> lines = Files.readAllLines(path);
            for (String line : lines) {
                FieldSet fieldSet = tokenizer.tokenize(line);
//                System.out.println(fieldSet.readString("username"));
//                System.out.println(fieldSet.readString("first"));
//                System.out.println(fieldSet.readString("last"));
//                System.out.println(fieldSet.readString("email"));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
