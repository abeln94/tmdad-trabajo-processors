package es.unizar.tmdad.lab0.rabbitmq;

import es.unizar.tmdad.lab0.processors.Processor;
import es.unizar.tmdad.lab0.processors.ProcessorsList;
import es.unizar.tmdad.lab0.settings.Preferences;
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
public class RabbitMQEndpoint {

    static final String topicExchangeName = "tweets-exchange";

    @Bean
    TopicExchange exchange() {
        return new TopicExchange(topicExchangeName);
    }

    static final String inputQueueName = "rawTweets-queue";
    static final String inputTopicName = "rawTweets-topic";

    static final String outputQueueName = "processedTweets-queue";
    static final String outputTopicName = "processedTweets-topic";

    static final String settingsQueueName = "settings-queue";
    static final String settingsTopicName = "settings-topic";

    @Bean
    Queue queueTweets() {
        return new Queue(inputQueueName, false);
    }

    @Bean
    Queue queueSettings() {
        return new Queue(settingsQueueName, false);
    }

    @Bean
    Binding bindingTweets(TopicExchange exchange) {
        return BindingBuilder.bind(queueTweets()).to(exchange).with(inputTopicName);
    }

    @Bean
    Binding bindingSettings(TopicExchange exchange) {
        return BindingBuilder.bind(queueSettings()).to(exchange).with(settingsTopicName);
    }

    @Bean
    SimpleMessageListenerContainer tweetsContainer(ConnectionFactory connectionFactory) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(inputQueueName);
        container.setMessageListener(tweetsListenerAdapter(this));
        return container;
    }

    @Bean
    SimpleMessageListenerContainer settingsContainer(ConnectionFactory connectionFactory) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(settingsQueueName);
        container.setMessageListener(settingsListenerAdapter(this));
        return container;
    }

    @Bean
    MessageListenerAdapter tweetsListenerAdapter(RabbitMQEndpoint receiver) {
        return new MessageListenerAdapter(receiver, "receiveTweet");
    }

    @Bean
    MessageListenerAdapter settingsListenerAdapter(RabbitMQEndpoint receiver) {
        return new MessageListenerAdapter(receiver, "receiveSettings");
    }

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    private ProcessorsList processorsList;

    @Autowired
    private Preferences preferences;

    public RabbitMQEndpoint(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void receiveTweet(Tweet tweet) {
        preferences.increaseTweetsProcessed();
        Processor processor = processorsList.getByName(preferences.getProcessorName());
        for (Tweet tweetToSend : processor.parseTweet(tweet)) {
            rabbitTemplate.convertAndSend(topicExchangeName, outputTopicName, tweetToSend);
        }
        System.out.println("Message received, converted and sent");
    }

    public void receiveSettings(String settings) {
        String[] settings_split = settings.split("\n");
        preferences.setConfiguration(settings_split[0], settings_split[1]);
        System.out.println("Settings received");
    }
}
