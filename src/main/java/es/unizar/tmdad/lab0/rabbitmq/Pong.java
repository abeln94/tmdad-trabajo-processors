package es.unizar.tmdad.lab0.rabbitmq;

import static es.unizar.tmdad.lab0.rabbitmq.Ping.queueName;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class Pong {
    
    static final String queueName = "pong-queue";
    static final String topicName = "pong-topic";

    
    @Bean
    Queue queuePong(){
        return new Queue(queueName, false);
    }

    @Bean
    Binding bindingPong(TopicExchange exchange) {
        return BindingBuilder.bind(queuePong()).to(exchange).with(topicName);
    }

    @Bean
    SimpleMessageListenerContainer containerPong(ConnectionFactory connectionFactory, Pong receiver) {
        MessageListenerAdapter listenerAdapter = new MessageListenerAdapter(receiver, "receiveMessage");
                
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(queueName);
        container.setMessageListener(listenerAdapter);
        return container;
    }

    private final RabbitTemplate rabbitTemplate;
    
    public Pong(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void receiveMessage(ObjectSent message) {
        System.out.println("Received PONG: " + message.name+" - "+message.id);
        
        rabbitTemplate.convertAndSend(Ping.topicExchangeName, Ping.topicName, message);
    }

}
