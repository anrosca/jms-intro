package com.endava.jms;

import java.io.IOException;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

public class JmsAsyncReceiver implements MessageListener {

    private final Connection connection;

    public JmsAsyncReceiver() throws JMSException {
        ConnectionFactory factory = new ActiveMQConnectionFactory("tcp://localhost:61616");
        connection = factory.createConnection();
        final Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        final Queue queue = session.createQueue("XML_ORDERS.Q");
        final MessageConsumer consumer = session.createConsumer(queue);
        consumer.setMessageListener(this);
    }

    public void start() throws JMSException {
        connection.start();
    }

    public void stop() throws JMSException {
        connection.stop();
    }

    @Override
    public void onMessage(final Message receivedMessage) {

        try {
            TextMessage message = (TextMessage) receivedMessage;
            System.out.println(message.getText());
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) throws JMSException, IOException {
        final JmsAsyncReceiver receiver = new JmsAsyncReceiver();
        receiver.start();
        System.out.println("Press any key to stop...");
        System.in.read();
        receiver.stop();
    }
}
