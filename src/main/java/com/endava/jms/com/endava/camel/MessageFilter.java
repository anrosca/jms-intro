package com.endava.jms.com.endava.camel;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;

public class MessageFilter {

    public static void main(String[] args) throws Exception {
        CamelContext context = new DefaultCamelContext();
        ConnectionFactory factory = new ActiveMQConnectionFactory("tcp://localhost:61616");
        context.addComponent("jms",
                JmsComponent.jmsComponentAutoAcknowledge(factory));
        context.addRoutes(new MessageFilterRoute());
        context.addRoutes(new OrderLoggingRoute());
        context.addRoutes(new OrderAuditLoggingRoute());
        context.start();
        System.out.println("Press any key to stop...");
        System.in.read();
        context.stop();
    }


    public static class MessageFilterRoute extends RouteBuilder {

        @Override
        public void configure() throws Exception {
            from("jms:XML_ORDERS.Q")
                    // .filter(xpath("/order[not(@test)]"))
                    .multicast().to("jms:ORDERS.Q", "jms:ORDER_AUDIT.Q");
        }
    }

    public static class OrderLoggingRoute extends RouteBuilder {

        @Override
        public void configure() throws Exception {
            from("jms:ORDERS.Q").process(exchange -> {
                final Message message = exchange.getIn();
                System.out.println("Order: " + message.getBody(String.class));
            });
        }
    }

    public static class OrderAuditLoggingRoute extends RouteBuilder {

        @Override
        public void configure() throws Exception {
            from("jms:ORDER_AUDIT.Q").process(exchange -> {
                final Message message = exchange.getIn();
                System.out.println("Auditing Order: " + message.getBody(String.class));
            });
        }
    }
}
