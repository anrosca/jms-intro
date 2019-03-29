package com.endava.jms.com.endava.camel;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;

public class ContentBasedRouter {

    public static void main(String[] args) throws Exception {
        CamelContext context = new DefaultCamelContext();
        ConnectionFactory factory = new ActiveMQConnectionFactory("tcp://localhost:61616");
        context.addComponent("jms",
                JmsComponent.jmsComponentAutoAcknowledge(factory));
        context.addRoutes(new RouteBuilder() {
            public void configure() throws Exception {
               from("jms:ORDERS.Q")
                       .choice()
                       .when(header("Content-Type").endsWith("xml")).to("jms:XML_ORDERS.Q")
                       .when(header("Content-Type").endsWith("csv")).to("jms:CSV_ORDERS.Q")
               .otherwise().to("jms:BAD_ORDERS.Q");

               from("jms:XML_ORDERS.Q").process(exchange -> {
                   final Message in = exchange.getIn();
                   System.out.println("Received xml message: " + in.getBody(String.class));
               });

                from("jms:CSV_ORDERS.Q").process(exchange -> {
                    final Message in = exchange.getIn();
                    System.out.println("Received csv message: " + in.getBody(String.class));
                });

                from("jms:BAD_ORDERS.Q").process(exchange -> {
                    final Message in = exchange.getIn();
                    System.out.println("Received bad message: " + in.getBody(String.class));
                });
            }
        });
        context.start();
        System.out.println("Press any key to stop");
        System.in.read();
        context.stop();
    }
}
