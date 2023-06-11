package com.example.springbootcamelkafka.route;

import com.example.springbootcamelkafka.model.Request;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.kafka.KafkaConstants;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;

@Component
public class RequestProcessingRoute extends RouteBuilder {
    @Override
    public void configure() {
        onException(Exception.class)
                .handled(true)
                .log("Request Processing Route Error: " + exceptionMessage())
                .setHeader("Status", simple("Error"))
                .setBody(exceptionMessage())
                .to("micrometer:counter:simple.errors.count")
                .to("direct:statusRoute")
                .markRollbackOnly();

        from("direct:requestProcessingRoute")
                .routeId("Request Processing Route")
                .transacted()
                .to("direct:databaseRoute")
                .to("direct:resultsRoute")
                .setHeader("Status", simple("Success"))
                .setBody(simple("Success processing"))
                .to("micrometer:counter:simple.success.count")
                .to("direct:statusRoute");

        from("direct:databaseRoute")
                .routeId("Database Route")
                .process(exchange -> {
                    com.example.springbootcamelkafka.gen.Request in = exchange.getIn().getBody(com.example.springbootcamelkafka.gen.Request.class);
                    Request request = new Request();
                    request.setRequestID(in.getRequestID());
                    request.setCode(in.getCode());
                    exchange.getMessage().setBody(request, Request.class);
                })
                .to("jpa:com.example.springbootcamelkafka.model.Request");

        from("direct:resultsRoute")
                .routeId("Results Route")
                .process(exchange -> {
                    Request request = exchange.getIn().getBody(Request.class);
                    exchange.getMessage().setBody(request, Request.class);
                })
                .marshal().json(JsonLibrary.Jackson)
                .setHeader(KafkaConstants.KEY, simple("Camel"))
                .to("kafka:results?brokers={{kafka.broker2.host}}");
    }
}
