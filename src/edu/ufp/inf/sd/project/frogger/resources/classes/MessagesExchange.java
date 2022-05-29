package edu.ufp.inf.sd.project.frogger.resources.classes;

import com.rabbitmq.client.*;
import edu.ufp.inf.sd.project.frogger.util.RabbitUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

public class MessagesExchange implements Runnable {
    private final String exchangeName;
    private final String hostIp;
    private final String rmqPort;
    private Connection connectionRMQ;
    private Channel channelRMQ;
    private String queueName;

    public MessagesExchange(HashMap<String, String> argsRMQ) {
        hostIp = argsRMQ.get("hostIp");
        rmqPort = argsRMQ.get("rmqPort");
        exchangeName = argsRMQ.get("exchangeName");
    }

    @Override
    public void run() {
        try {
            connectionRMQ = RabbitUtils.newConnection2Server(hostIp, Integer.parseInt(rmqPort), "guest", "guest");
            channelRMQ = RabbitUtils.createChannel2Server(connectionRMQ);
            channelRMQ.exchangeDeclare(exchangeName, BuiltinExchangeType.TOPIC);
            queueName = channelRMQ.queueDeclare().getQueue();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    public void consumeRMQ(String routingKey) {
        try {
            channelRMQ.queueBind(queueName, exchangeName, routingKey);
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = (new String(delivery.getBody(), StandardCharsets.UTF_8));

                System.out.println("[x] Consumer Tag[" + consumerTag + "]Received '" + message + "'" + routingKey + exchangeName);

            };
            CancelCallback cancelCallback = (consumerTag) -> System.out.println("[x] Consumer Tag[" + consumerTag + "]CancelCallback");

            channelRMQ.basicConsume(queueName, true, deliverCallback, cancelCallback);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void publishRMQ(String routingKey, String message) {

        try {
            channelRMQ.basicPublish(exchangeName, routingKey, null, message.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
