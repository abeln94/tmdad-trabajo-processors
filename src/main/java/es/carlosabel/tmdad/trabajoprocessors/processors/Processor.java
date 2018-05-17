package es.carlosabel.tmdad.trabajoprocessors.processors;

import es.carlosabel.tmdad.trabajoprocessors.settings.Preferences;
import java.util.List;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.twitter.api.Tweet;

/**
 * Processor Interface
 */
public abstract class Processor {

    /**
     * Retrieves preferences
     */
    @Autowired
    Preferences prefs;

    final Random random = new Random();

    public abstract List<Tweet> parseTweet(Tweet tweet);

    /**
     * Utility:
     * Allows changing a tweet's text (by creating another tweet)
     */
    public Tweet modifyTweet(Tweet tweet, String newText) {
        return new Tweet(tweet.getId(), tweet.getIdStr(), newText, tweet.getCreatedAt(), tweet.getFromUser(), tweet.getProfileImageUrl(), tweet.getToUserId(), tweet.getFromUserId(), tweet.getLanguageCode(), tweet.getSource());
    }

}
