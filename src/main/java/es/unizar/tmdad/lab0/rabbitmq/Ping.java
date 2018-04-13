package es.unizar.tmdad.lab0.rabbitmq;

import javax.annotation.Resource;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class Ping {
    
    static final String topicExchangeName = "pingpongs-exchange";
    @Bean
    TopicExchange exchange() {
        return new TopicExchange(topicExchangeName);
    }
    
    static final String queueName = "ping-queue";
    static final String topicName = "ping-topic";
    
    @Bean
    Queue queuePing(){
        return new Queue(queueName, false);
    }

    @Bean
    Binding bindingPing(TopicExchange exchange) {
        return BindingBuilder.bind(queuePing()).to(exchange).with(topicName);
    }

    @Bean
    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory, MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(queueName);
        container.setMessageListener(listenerAdapter);
        return container;
    }

    @Bean
    MessageListenerAdapter listenerAdapter(Ping receiver) {
        return new MessageListenerAdapter(receiver, "receiveMessage");
    }

    private final RabbitTemplate rabbitTemplate;
    
    public Ping(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void receiveMessage(ObjectSent message) {
        System.out.println("Received PING: " + message.name+" - "+message.id);
        
        message.id++;
        
        if(message.id<10){
            rabbitTemplate.convertAndSend(Ping.topicExchangeName, Pong.topicName, message);
        }
    }

}
