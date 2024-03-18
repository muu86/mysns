package com.mj.mysns.batch.address;

import com.mj.mysns.location.entity.LegalAddress;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.core.io.FileSystemResource;

public class LegalAddressReader extends FlatFileItemReader<LegalAddress> {

    public LegalAddressReader(Path path) {
        setResource(new FileSystemResource(path));
        // 컬럼명 1행 제거
        setLinesToSkip(1);

        DefaultLineMapper<LegalAddress> lineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setDelimiter("|");

        // 'code', 'c1', 'c2', 'c3', 'c4', 'order', 'created_at', 'deleted_at', 'prev_code', 'eng_name', 'name', 'geometry',
        tokenizer.setNames("index", "code", "c1", "c2", "c3", "c4", "order", "created_at", "deleted_at",
            "prev_code", "eng_name", "name", "geometry");
        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(fieldSet -> {
            WKTReader wktReader = new WKTReader();
            Geometry geometry = null;
            try {
                geometry = wktReader.read(fieldSet.readString("geometry"));
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            // EPSG:4326 좌표계를 세팅해서 저장해야함
            geometry.setSRID(4326);
            return LegalAddress.builder()
                .code(fieldSet.readString("code"))
                .sido(fieldSet.readString("c1"))
                .gungu(fieldSet.readString("c2"))
                .eupmyundong(fieldSet.readString("c3"))
                .li(fieldSet.readString("c4"))
                .sunwi(fieldSet.readString("order"))
                .createdAt(fieldSet.readString("created_at") == "" ? null
                    : LocalDate.parse(fieldSet.readString("created_at"),
                            DateTimeFormatter.ISO_LOCAL_DATE)
                        .atStartOfDay())
                .deletedAt(fieldSet.readString("deleted_at") == "" ? null
                    : LocalDate.parse(fieldSet.readString("deleted_at"),
                        DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay())
                .prevCode(fieldSet.readString("prev_code"))
                .location(geometry)
                .build();
        });
        setLineMapper(lineMapper);
    }
}
