package es.carlosabel.tmdad.trabajoprocessors.endpoints;

import es.carlosabel.tmdad.trabajoprocessors.processors.Processor;
import es.carlosabel.tmdad.trabajoprocessors.settings.Preferences;
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

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * Get and set configuration
     */
    @Autowired
    private Preferences prefs;

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
    Queue tweetsQueue() {
        return new Queue(inputQueueNamePrefix + prefs.getProfileName(), false);
    }

    @Bean
    Queue settingsQueue() {
        return new Queue(settingsQueueNamePrefix + prefs.getProfileName(), false);
    }

    //-----------------topics-----------------
    static String inputTopicNamePrefix = "rawTweets-topic.";

    static String outputTopicName = "processedTweets-topic";

    static String settingsTopicNamePrefix = "settings-topic.";

    //-----------------bindings-----------------
    @Bean
    Binding tweetsBinding() {
        return BindingBuilder.bind(tweetsQueue()).to(tweetsExchange()).with(inputTopicNamePrefix + prefs.getProfileName());
    }

    @Bean
    Binding settingsBinding() {
        return BindingBuilder.bind(settingsQueue()).to(settingsExchange()).with(settingsTopicNamePrefix + prefs.getProfileName());
    }

    //-----------------redirections-----------------
    @Bean
    SimpleMessageListenerContainer tweetsContainer(ConnectionFactory connectionFactory) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueues(tweetsQueue());
        container.setMessageListener(tweetsListenerAdapter());
        return container;
    }

    @Bean
    SimpleMessageListenerContainer settingsContainer(ConnectionFactory connectionFactory) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueues(settingsQueue());
        container.setMessageListener(settingsListenerAdapter());
        return container;
    }

    //-----------------adapters-----------------
    @Bean
    MessageListenerAdapter tweetsListenerAdapter() {
        return new MessageListenerAdapter(this, "receiveTweet");
    }

    @Bean
    MessageListenerAdapter settingsListenerAdapter() {
        return new MessageListenerAdapter(this, "receiveSettings");
    }

    //-----------------listeners-----------------
    public void receiveTweet(Tweet tweet) {
        prefs.increaseTweetsProcessed();
        for (Tweet tweetToSend : processor.parseTweet(tweet)) {
            rabbitTemplate.convertAndSend(tweetsExchangeName, outputTopicName, tweetToSend);
        }
        System.out.println("Message received, converted and sent");
        //TODO: remove logic from here, send to logic class
    }

    public void receiveSettings(String settings) {
        prefs.setProcessorLevel(settings);
        System.out.println("Settings received: " + settings);
    }
}
