package com.example.student;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    @Value("students")
    private String TOPIC;

    @Autowired
    private KafkaTemplate<String, Student> kafkaTemplate;

    public void sendMessage(Student student) {
        System.out.println("student"+student);
        kafkaTemplate.send(TOPIC, student.getId()+"", student);
    }
}
