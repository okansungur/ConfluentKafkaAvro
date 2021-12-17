package com.example.student;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
@RequestMapping(value = "/mystudent")
public class ProducerController {

    @Autowired
    private KafkaProducerService producerService;

    @GetMapping(value = "/init")
    public void sendMessageKafkaTopic() {
        Student student=new Student();
        int value=(int)Math.ceil(Math.random()*10000);
        student.setId(value);
        student.setName("Kate"+value);
        student.setStudentid(371+value);
        producerService.sendMessage(student);
    }

}


