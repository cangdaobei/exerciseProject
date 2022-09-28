package com.zcr.exercise;

import com.zcr.exercise.async.asyncandtransactional.IAService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ExerciseApplicationTests {

    @Autowired
    IAService aService;

    @Test
    public void contextLoads() {
        try {
//            aService.funTemp1();
            aService.funTemp2();
//            aService.funTemp3();
//            aService.funTemp4();
//            aService.funTemp5();
        } catch (Error e) {
            e.printStackTrace();
        }
    }

}
