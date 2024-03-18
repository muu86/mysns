package com.mj.mysns.any;

import java.util.Random;
import org.junit.jupiter.api.Test;

public class RandomTest {


    @Test
    void t1() {
        long min = 0;
        long max = 10;
        Random random = new Random();
        for (int i = 0; i < 100; i++) {
        long l = random.nextLong(min, max + 1);
            System.out.print(l + " ");
        }
        System.out.println();
    }
}
