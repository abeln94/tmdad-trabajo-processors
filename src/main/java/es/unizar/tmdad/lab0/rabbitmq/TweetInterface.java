package es.unizar.tmdad.lab0.rabbitmq;

import es.unizar.tmdad.lab0.processors.Processor;
import es.unizar.tmdad.lab0.processors.ProcessorsList;
import es.unizar.tmdad.lab0.settings.Preferences;
import javax.annotation.Resource;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.stereotype.Component;

@Component
public class TweetInterface {
    
    static final String topicExchangeName = "tweets-exchange";
    @Bean
    TopicExchange exchange() {
        return new TopicExchange(topicExchangeName);
    }
    
    static final String inputQueueName = "rawTweets-queue";
    static final String inputTopicName = "rawTweets-topic";
    
    static final String outputQueueName = "processedTweets-queue";
    static final String outputTopicName = "processedTweets-topic";
    
    @Bean
    Queue queuePing(){
        return new Queue(inputQueueName, false);
    }

    @Bean
    Binding bindingPing(TopicExchange exchange) {
        return BindingBuilder.bind(queuePing()).to(exchange).with(inputTopicName);
    }

    @Bean
    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory, MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(inputQueueName);
        container.setMessageListener(listenerAdapter);
        return container;
    }

    @Bean
    MessageListenerAdapter listenerAdapter(TweetInterface receiver) {
        return new MessageListenerAdapter(receiver, "receiveMessage");
    }

    
    
    
    
    private final RabbitTemplate rabbitTemplate;
    
    @Autowired
    private ProcessorsList processorsList;
    
    @Autowired
    private Preferences preferences;
    
    public TweetInterface(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void receiveMessage(Tweet tweet) {
        preferences.increaseTweetsProcessed();
        Processor processor = processorsList.getByName(preferences.getProcessorName());
        for (Tweet tweetToSend : processor.parseTweet(tweet)) {
            rabbitTemplate.convertAndSend(topicExchangeName, outputTopicName, tweetToSend);
        }
        System.out.println("Message received, converted and sent");
    }

}
