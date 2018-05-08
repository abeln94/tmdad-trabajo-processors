package es.unizar.tmdad.lab0.rabbitmq;

import es.unizar.tmdad.lab0.processors.Processor;
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

/**
 * Endpoint of RabbitMQ
 * - Configures exchanges, topics and queues.
 * - Receives messages from subscribed queues
 * - Sends messages to topics
 */
@Component
public class RabbitMQEndpoint {

    private final RabbitTemplate rabbitTemplate;

    public RabbitMQEndpoint(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Get and set configuration
     */
    @Autowired
    private Preferences pref;

    /**
     * Use active processor
     */
    @Autowired
    Processor processor;

    //-------------exchanges-----------------
    static final String tweetsExchangeName = "tweets-exchange";
    static final String settingsExchangeName = "settings-exchange";

    @Bean
    TopicExchange tweetsExchange() {
        return new TopicExchange(tweetsExchangeName);
    }

    @Bean
    TopicExchange settingsExchange() {
        return new TopicExchange(settingsExchangeName);
    }

    //-----------------queues-----------------
    static String inputQueueNamePrefix = "rawTweets-queue.";

    static String outputQueueName = "processedTweets-queue";

    static String settingsQueueNamePrefix = "settings-queue.";

    @Bean
    Queue queueTweets() {
        return new Queue(inputQueueNamePrefix + pref.getProfileName(), false);
    }

    @Bean
    Queue queueSettings() {
        return new Queue(settingsQueueNamePrefix + pref.getProfileName(), false);
    }

    //-----------------topics-----------------
    static String inputTopicNamePrefix = "rawTweets-topic.";

    static String outputTopicName = "processedTweets-topic";

    static String settingsTopicNamePrefix = "settings-topic.";

    //-----------------bindings-----------------
    @Bean
    Binding bindingTweets() {
        return BindingBuilder.bind(queueTweets()).to(tweetsExchange()).with(inputTopicNamePrefix + pref.getProfileName());
    }

    @Bean
    Binding bindingSettings() {
        return BindingBuilder.bind(queueSettings()).to(settingsExchange()).with(settingsTopicNamePrefix + pref.getProfileName());
    }

    //-----------------redirections-----------------
    @Bean
    SimpleMessageListenerContainer tweetsContainer(ConnectionFactory connectionFactory) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueues(queueTweets());
        container.setMessageListener(tweetsListenerAdapter(this));
        return container;
    }

    @Bean
    SimpleMessageListenerContainer settingsContainer(ConnectionFactory connectionFactory) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueues(queueSettings());
        container.setMessageListener(settingsListenerAdapter(this));
        return container;
    }

    //-----------------adapters-----------------
    @Bean
    MessageListenerAdapter tweetsListenerAdapter(RabbitMQEndpoint receiver) {
        return new MessageListenerAdapter(receiver, "receiveTweet");
    }

    @Bean
    MessageListenerAdapter settingsListenerAdapter(RabbitMQEndpoint receiver) {
        return new MessageListenerAdapter(receiver, "receiveSettings");
    }

    //-----------------listeners-----------------
    public void receiveTweet(Tweet tweet) {
        pref.increaseTweetsProcessed();
        for (Tweet tweetToSend : processor.parseTweet(tweet)) {
            rabbitTemplate.convertAndSend(tweetsExchangeName, outputTopicName, tweetToSend);
        }
        System.out.println("Message received, converted and sent");
        //TODO: remove logic from here, send to logic class
    }

    public void receiveSettings(String settings) {
        pref.setConfiguration(settings);
        System.out.println("Settings received: " + settings);
    }
}
