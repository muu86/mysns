package com.mj.mysns.batch.mockdata;

import com.mj.mysns.user.entity.User;
import java.nio.file.Path;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.core.io.FileSystemResource;

public class MockUserReader extends FlatFileItemReader<User> {

    public MockUserReader(Path path) {
        setResource(new FileSystemResource(path));
        setLinesToSkip(1);

        DefaultLineMapper<User> lineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setDelimiter(",");
        tokenizer.setNames("username", "first", "last", "email");
        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(fieldSet -> User.builder()
            .username(fieldSet.readString("username"))
            .first(fieldSet.readString("first"))
            .last(fieldSet.readString("last"))
            .email(fieldSet.readString("email"))
            .build());
        setLineMapper(lineMapper);
    }
}
