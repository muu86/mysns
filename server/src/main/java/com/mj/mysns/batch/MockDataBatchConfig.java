package com.mj.mysns.batch;

import com.mj.mysns.batch.mockdata.MockPostProcessor;
import com.mj.mysns.batch.mockdata.MockPostReader;
import com.mj.mysns.batch.mockdata.MockPostWriter;
import com.mj.mysns.batch.mockdata.MockUserReader;
import com.mj.mysns.batch.mockdata.MockUserWriter;
import com.mj.mysns.location.repository.AddressRepository;
import com.mj.mysns.post.entity.Post;
import com.mj.mysns.post.repository.PostRepository;
import com.mj.mysns.user.UserRepository;
import com.mj.mysns.user.entity.User;
import java.nio.file.Path;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class MockDataBatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final Step createLegalAddressStep;
    private final PostRepository postRepository;

    @Bean
    public ItemReader<User> mockUserReader() {
        return new MockUserReader(Path.of("/Users/mj/work/sns/data/mock", "mock_user.csv"));
    }

    @Bean
    public ItemWriter<User> mockUserWriter() {
        return new MockUserWriter(userRepository);
    }

    @Bean
    public Step createMockUserStep() {
        return new StepBuilder("createMockUserStep", jobRepository)
            .<User, User>chunk(1, platformTransactionManager)
            .reader(mockUserReader())
            .writer(mockUserWriter())
            .allowStartIfComplete(true)
            .build();
    }
//
//    @Bean
//    public ItemReader<LegalAddress> legalAddressItemReader() {
//        return new LegalAddressReader(Path.of("/Users/mj/work/sns/data", "seoul_emd_geometry.csv"));
//    }
//
//    @Bean
//    public ItemProcessor<LegalAddress, LegalAddress> legalAddressItemProcessor() {
//        return new LegalAddressProcessor();
//    }
//
//    @Bean
//    public ItemWriter<LegalAddress> legalAddressItemWriter() {
//        return new LegalAddressWriter(addressRepository);
//    }

//    @Bean
//    public Step createLegalAddressStep() {
//        return new StepBuilder("createLegalAddressDataStep", jobRepository)
//            .<LegalAddress, LegalAddress>chunk(100, platformTransactionManager)
//            .reader(legalAddressItemReader())
//            .processor(legalAddressItemProcessor())
//            .writer(legalAddressItemWriter())
//            .allowStartIfComplete(true)
//            .build();
//    }

    @Bean
    public ItemReader<String> mockPostReader() {
        return new MockPostReader(Path.of("/Users/mj/work/sns/data/mock", "mock_post.csv"));
    }

    @Bean
    public ItemProcessor<String, Post> mockPostProcessor() {
        return new MockPostProcessor(userRepository, addressRepository);
    }

    @Bean
    public ItemWriter<Post> mockPostWriter() {
        return new MockPostWriter(postRepository);
    }

    @Bean
    public Step createMockPostStep() {
        return new StepBuilder("createMockPostStep", jobRepository)
            .<String, Post>chunk(100, platformTransactionManager)
            .reader(mockPostReader())
            .processor(mockPostProcessor())
            .writer(mockPostWriter())
            .allowStartIfComplete(true)
            .build();
    }

    @Bean
    public Job createAllJob() {
        return new JobBuilder("createAllJob", jobRepository)
            .start(createMockUserStep())
            .next(createLegalAddressStep)
            .next(createMockPostStep())
            .build();
    }

}
