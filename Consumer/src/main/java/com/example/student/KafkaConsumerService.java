package com.example.student;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    @KafkaListener(topics = "students", groupId = "groupid")
    public void KafkaConsumer(@Payload Student stu,
                              @Header(name = KafkaHeaders.RECEIVED_MESSAGE_KEY) String key) {

        System.out.println("Received key is: "+ key + " & value is "+ stu.getName()+" studentid is "+stu.getStudentid());
    }
}

