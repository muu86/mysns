package com.mj.mysns.batch.mockdata;

import com.mj.mysns.user.UserRepository;
import com.mj.mysns.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

@RequiredArgsConstructor
public class MockUserWriter implements ItemWriter<User> {

//    @Autowired
    private final UserRepository userRepository;

    @Override
    public void write(Chunk<? extends User> chunk) throws Exception {
        userRepository.saveAll(chunk);
    }
}
