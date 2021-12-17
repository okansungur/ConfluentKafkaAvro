# Kafka, Apache Avro and Confluent Platform with springboot
## Kafka :
We need streaming because we  need to backup data, monitoring or detecting abnormalities,creating new streams from the original streams ,drip feed events into columnar or nosql databases.
There are a variety of schema technologies and they are known as data serialization systems. We have malready mentioned some populer ones with our tutorials.
Today we will talk about Avro, one of the most mature and experienced serialization systems.
## Apache Avro :
- It is eveloped as part of the Apache Hadoop project
- It’s schema language is json based.
- Avro (IDL) Interface Definition Language syntax is like  C 
- Avro has 2 representations, a human-readable JSON encoding and  binary encoding format
- Avro uses a .avsc file known as Avro Schema
- It has a poor compatibility for programming languagesi when compared witk other data serialization systems

## Confluent Platform
Confluent is a data streaming platform based on Apache Kafka capable of publish-and-subscribe, storage and  processing the data stream.
We define confluent as a data streaming  platform. İt is based on Apache Kafka. It can be used as publish-subscribe based messaging , storage and processing the streams.
We will need a confluent platform to make our springboot application work. Make sure that you have docker installed. We will be using a docker compose file.
You can download the docker compose file from  [Github](https://github.com/confluentinc/cp-all-in-one/tree/7.0.1-post/cp-all-in-one).
More information is available at  [Confluent](https://docs.confluent.io/5.5.1/quickstart/ce-docker-quickstart.html) 

According to avro.apache.org Avro Schema Decleration primitive types are listed as 
```
null: no value
boolean: a binary value
int: 32-bit signed integer
long: 64-bit signed integer
float: single precision (32-bit) IEEE 754 floating-point number
double: double precision (64-bit) IEEE 754 floating-point number
bytes: sequence of 8-bit unsigned bytes
string: unicode character sequence
```

First we will create a **student.avsc**  file. The package namespace here is important.We will be using the generated student class within our applications.
```
{
  "namespace": "com.example.student",
  "type": "record",
  "name": "Student",
  "fields": [
    {
          "name": "id",
          "type": "int"

        },
      {
        "name": "name",
        "type": "string",
        "avro.java.string": "String"
      },
      {
        "name": "studentid",
        "type": "int"
      }
    ]
  }
```

We will now use the command **docker compose up** from the command prompt to make our containers up and running. Make sure that all the containers are running. Some of them can fail at first so please restart those containers.,
We will be creating two springboot applications. One of them is the Producer application and the other one will be the Consumer. For the Producer application we will be creating a directory myavro and we will place student.avsc to that folder.

<p align="center">
  <img  src="https://github.com/okansungur/ConfluentKafkaAvro/blob/main/avrodirectory.png"><br/>
  student.avsc directory
</p>
We need avro-maven-plugin to generate the java classes. So please add the necessary dependencies. We will create a KafkaProducerService to send our messages to kafka topic.
The topic name will be ***topic.name=students***   and the group id will be  ***spring.kafka.consumer.group-id=groupid***
The producer key&value serializers are also defined at application.properties file.
And a  ProducerController for generating some random values from the web address http://localhost:9393/mystudent/init

```
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
```


```

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
```

The 




