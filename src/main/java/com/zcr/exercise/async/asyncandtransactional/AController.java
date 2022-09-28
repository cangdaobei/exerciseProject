package com.zcr.exercise.async.asyncandtransactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping
@ResponseBody
public class AController {

    @Autowired
    private IAService aInterface;

    @PostMapping("testAsync")
    public void testAsync(){
        aInterface.funTemp1();
    }


}
