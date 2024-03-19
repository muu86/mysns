package com.mj.mysns.location.repository;

import static org.geolatte.geom.builder.DSL.g;
import static org.geolatte.geom.builder.DSL.point;
import static org.geolatte.geom.crs.CoordinateReferenceSystems.WGS84;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.mj.mysns.location.entity.LegalAddress;
import java.util.List;
import org.geolatte.geom.G2D;
import org.geolatte.geom.Point;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@SpringBootTest
class CustomizedAddressRepositoryImplTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgis/postgis:16-master").asCompatibleSubstituteFor("postgres"));

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    @Qualifier("createLegalAddressJob")
    private Job job;

    @BeforeEach
    void batchjob()
        throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        // 주소 데이터 테스트 컨테이너에 넣음
        JobExecution run = jobLauncher.run(job, new JobParameters());
        String jobStatus = run.getExitStatus().toString();
    }

    @Autowired
    AddressRepository addressRepository;

    @Test
    void findLegalAddressNear_부암동() {
        // 종로구 부암동
        String latitude = "37.59520617773402";
        String longitude = "126.96518955304883";
        Point<G2D> point = point(WGS84,
            g(Double.parseDouble(longitude), Double.parseDouble(latitude)));

        List<LegalAddress> legalAddressNear = addressRepository.findLegalAddressNear(latitude,
            longitude, 1, 0);
        assertEquals(legalAddressNear.size(), 1);
        assertNotNull(legalAddressNear.getFirst());
        LegalAddress address = legalAddressNear.getFirst();
        assertEquals(address.getGungu(), "종로구");
        assertEquals(address.getEupmyundong(), "부암동");
    }

    @Test
    void findLegalAddressNear_금호동() {
        // 성동구 금호동1가
        String latitude = "37.55723481192904";
        String longitude = "127.02882302332466";
        Point<G2D> point = point(WGS84,
            g(Double.parseDouble(longitude), Double.parseDouble(latitude)));

        List<LegalAddress> legalAddressNear = addressRepository.findLegalAddressNear(latitude,
            longitude, 1, 0);
        assertEquals(legalAddressNear.size(), 1);
        assertNotNull(legalAddressNear.getFirst());
        LegalAddress address = legalAddressNear.getFirst();
        assertEquals(address.getGungu(), "성동구");
        assertEquals(address.getEupmyundong(), "금호동1가");
    }
}