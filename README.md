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

First we will create a **student.avsc**  file. The package namespace here is important. We will be using the generated student class within our applications.
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

We will now use the command **docker compose up** from the command prompt to make our containers up and running. Make sure that all the containers are running. Some of them can fail at first so please restart those containers.

<p align="center">
  <img  src="https://github.com/okansungur/ConfluentKafkaAvro/blob/main/containerlist.png"><br/>
  Docker container list
</p>



We will be creating two springboot applications. One of them is the Producer application and the other one will be the Consumer. For the Producer application we will be creating a directory myavro and we will place student.avsc to that folder.

<p align="center">
  <img  src="https://github.com/okansungur/ConfluentKafkaAvro/blob/main/avrodirectory.png"><br/>
  student.avsc  directory(myavro)
</p>
We need avro-maven-plugin to generate the java classes. We should tell maven the place of our source file student.avsc

```

   <plugin>
                <groupId>org.apache.avro</groupId>
                <artifactId>avro-maven-plugin</artifactId>
                <version>1.8.2</version>
                <executions>
                    <execution>
                        <phase>generate-sources</phase>
                        <goals>
                            <goal>schema</goal>
                        </goals>
                        <configuration>
                            <sourceDirectory>src/main/myavro</sourceDirectory>
                            <outputDirectory>${project.build.directory}/generated-sources</outputDirectory>
                            <stringType>String</stringType>
                        </configuration>
                    </execution>
                </executions>
            </plugin>

```

So please add the necessary dependencies to pom.xml. Run maven-clean and compile sequentialy.This will generate the
Student class.
<p align="center">
  <img  src="https://github.com/okansungur/ConfluentKafkaAvro/blob/main/generatedclass.png"><br/>
  Generated Student class by maven plugin
</p>

Next we will create a KafkaProducerService to send our messages to kafka .This will send the message to our topic with an id and value as student. For a better understanding we hardcoded the student value but you can assign the value from the application.properties file.

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
We will define some properties to application.properties file. The topic name will be students and the group id will be groupid.
The producer key&value serializers are also defined.The dependencies are definet at pom.xml.
```

server.port=9393
topic.name=students
topic.partitions-num=3
topic.replication-factor= 1
spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.properties.schema.registry.url=http://localhost:8081
spring.kafka.consumer.group-id=groupid
spring.kafka.consumer.auto-offset-reset=latest
spring.kafka.producer.key-serializer= org.apache.kafka.common.serialization.StringSerializer
spring.kafka.producer.value-serializer=  io.confluent.kafka.serializers.KafkaAvroSerializer

```


The ProducerController is created for generating some random values from the url address http://localhost:9393/mystudent/init

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

Now we are ready to run our application. When we click the url(localhost:9393/mystudent/init) we will send the message and we can track the message from the Confluent control center. You can open the control center from http://localhost:9021/clusters


<p align="center">
  <img  src="https://github.com/okansungur/ConfluentKafkaAvro/blob/main/controlcenter1.png"><br/>
  Control Center generated students topic
</p>

You can check the schema file form the students schema menu. You can delete the schema or change the compatibility modes from here.
<p align="center">
  <img  src="https://github.com/okansungur/ConfluentKafkaAvro/blob/main/controlcenter2.png"><br/>
  Control Center generated avro schema file
</p>


<p align="center">
  <img  src="https://github.com/okansungur/ConfluentKafkaAvro/blob/main/controlcenter3.png"><br/>
  Control Center incoming message
</p>


Now we created the producer application successfully. And we are ready for the Consumer part. 
For Consumer application we will place the myavro directory to  src\main.We will  add the avro-maven-plugin and neccessary dependencies to our pom.xml.
We will generate our Student.class just like we did in Producer application. Just to make a difference we will use application.yaml. 
As this application will deserialize our data we will use the key&value deserializer classes. Also our application will run at port 9392.

```

server:
  port: 9392
spring:
  kafka:
    bootstrap-server: localhost:9092
    consumer:
      auto-offset-reset: latest
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: io.confluent.kafka.serializers.KafkaAvroDeserializer
    properties:
      schema.registry.url: http://localhost:8081
      specific.avro.reader: true

```

Next we will define KafkaConsumerService class. This class will consume the messages. With the KafkaListener annotation we passs the topic and groupid parameters.

```

@Service
public class KafkaConsumerService {

    @KafkaListener(topics = "students", groupId = "groupid")
    public void KafkaConsumer(@Payload Student stu,
                              @Header(name = KafkaHeaders.RECEIVED_MESSAGE_KEY) String key) {

        System.out.println("Received key is: "+ key + " & value is "+ stu.getName()+" studentid is "+stu.getStudentid());
    }
}

```
When we run our application and send several messages from the Producer rest application we will get the output.

<p align="center">
  <img  src="https://github.com/okansungur/ConfluentKafkaAvro/blob/main/consumer.png"><br/>
  Consumer messages
</p>





