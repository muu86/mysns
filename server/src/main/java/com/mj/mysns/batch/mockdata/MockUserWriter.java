package com.mj.mysns.batch.mockdata;

import com.mj.mysns.user.UserRepository;
import com.mj.mysns.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

@RequiredArgsConstructor
public class MockUserWriter implements ItemWriter<User> {

    private final UserRepository userRepository;

    @Override
    public void write(Chunk<? extends User> chunk) throws Exception {
        User user = chunk.getItems().get(0);

        int suffix = 0;
        while (userRepository.findByUsername(user.getUsername()).isPresent()) {
            user.setUsername(user.getUsername() + "_" + ++suffix);
        }

        userRepository.saveAll(chunk);
    }
}
