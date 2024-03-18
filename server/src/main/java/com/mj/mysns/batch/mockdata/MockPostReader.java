package com.mj.mysns.batch.mockdata;

import java.nio.file.Path;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.PassThroughLineMapper;
import org.springframework.batch.item.file.separator.RecordSeparatorPolicy;
import org.springframework.core.io.FileSystemResource;

public class MockPostReader extends FlatFileItemReader<String> {

    public MockPostReader(Path path) {
        setResource(new FileSystemResource(path));
        setLinesToSkip(1);

        setLineMapper(new PassThroughLineMapper());

        setRecordSeparatorPolicy(new ParagraphSeparatorPolicy());
    }

    private class ParagraphSeparatorPolicy implements RecordSeparatorPolicy {

        @Override
        public boolean isEndOfRecord(String record) {
            if (record.isEmpty()) {
                return false;
            }
            return true;
        }

        @Override
        public String postProcess(String record) {
            return record.replaceAll("\"", "");
        }

        @Override
        public String preProcess(String record) {
            if (record.equals("content")) {
                return "";
            }
            return record;
        }
    }
}
