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

public class JmsReceiver {

    public static void main(String[] args) throws JMSException {
        ConnectionFactory factory = new ActiveMQConnectionFactory("tcp://localhost:61616");
        final Connection connection = factory.createConnection();
        connection.start();
        final Session session = connection.createSession(false, Session.DUPS_OK_ACKNOWLEDGE);
        final Queue queue = session.createQueue("XML_ORDERS.Q");
        final MessageConsumer consumer = session.createConsumer(queue);

        final Message firstMessage = consumer.receive();
        TextMessage request = (TextMessage) firstMessage;

        final MessageProducer producer = session.createProducer(request.getJMSReplyTo());
        final TextMessage response = session.createTextMessage("RESPonse: OK");
        response.setJMSCorrelationID(request.getJMSMessageID());

        producer.send(response);

        connection.stop();
    }
}
