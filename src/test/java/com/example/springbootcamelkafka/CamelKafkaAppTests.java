package com.example.springbootcamelkafka;

import com.example.springbootcamelkafka.repository.RequestRepository;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.MockEndpoints;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@MockEndpoints
@CamelSpringBootTest
@EnableAutoConfiguration
@AutoConfigureTestDatabase
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"}, topics = {"requests", "results", "status_topic"})
@SpringBootTest(properties = {"kafka.broker1.host=localhost:9092", "kafka.broker2.host=localhost:9092", "kafka.broker1.camel-request-topic-path=direct:requests"})
public class CamelKafkaAppTests {
    @Autowired
    private ProducerTemplate producerTemplate;

    @Autowired
    private RequestRepository repository;

    @EndpointInject("mock:direct:statusRoute")
    public MockEndpoint statusRouteMock;

    @EndpointInject("mock:direct:requestProcessingRoute")
    public MockEndpoint requestProcessingRouteMock;

    @Test
    void canProcessingIfCorrectRequest() throws InterruptedException {
        statusRouteMock.setExpectedMessageCount(1);
        requestProcessingRouteMock.setExpectedMessageCount(1);
        producerTemplate.sendBody("direct:requests", """
                <?xml version="1.0" encoding="UTF-8" ?>
                                
                <gen:Request xmlns:gen="/jaxb/gen">
                  <gen:requestID>05afef68-4059-4611-abdd-f0e21c6ac779</gen:requestID>
                  <gen:code>33242</gen:code>
                </gen:Request>
                """);
        statusRouteMock.assertIsSatisfied();
        requestProcessingRouteMock.assertIsSatisfied();
        assertEquals(repository.count(), 1);
    }

    @Test
    void cantProcessingIfXMLBroken() throws InterruptedException {
        statusRouteMock.setExpectedMessageCount(1);
        requestProcessingRouteMock.setExpectedMessageCount(0);
        producerTemplate.sendBody("direct:requests", """
                <?xml version="1.0" encoding="UTF-8" ?>
                                
                <gen:uest xmlns:gen="/jaxb/gen">
                  <gen:reqafef68-4059-4611-abdd-f0e21c6ac779</gen:requestID>
                  <gen:code>33242</gen:code>
                </gen:Request>
                """);
        statusRouteMock.assertIsSatisfied();
        requestProcessingRouteMock.assertIsSatisfied();
        assertEquals(repository.count(), 0);
    }

}




