package com.endava.jms;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

public class JmsSender {

    public static void main(String[] args) throws JMSException {
        ConnectionFactory factory =
                new ActiveMQConnectionFactory("tcp://localhost:61616");
        final Connection connection = factory.createConnection();
        connection.start();
        final Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        final Queue queue = session.createQueue("ORDERS.Q");
        // final Queue response = session.createQueue("RESPONSE.Q");
        final MessageProducer producer = session.createProducer(queue);
        final TextMessage textMessage = session.createTextMessage();
        // textMessage.setJMSReplyTo(response);
        textMessage.setText("<?xml version='1.0' ?><order><id>1</id></order>");
        textMessage.setStringProperty("Content-Type", "text/csv");
        producer.send(textMessage);
        // final MessageConsumer consumer = session.createConsumer(response,
        //         "JMSCorrelationID = '" + textMessage.getJMSMessageID() + "'");
        //
        // final Message receivedMessage = consumer.receive();
        // System.out.println(receivedMessage);

        connection.close();
    }
}
