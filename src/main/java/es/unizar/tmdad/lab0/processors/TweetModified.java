package es.unizar.tmdad.lab0.processors;

import org.springframework.social.twitter.api.Tweet;

/**
 * Allows changing a tweet's text (by creating another tweet)
 */
public class TweetModified {

    static public Tweet modifyTweet(Tweet tweet, String newText) {
        return new Tweet(tweet.getId(), tweet.getIdStr(), newText, tweet.getCreatedAt(), tweet.getFromUser(), tweet.getProfileImageUrl(), tweet.getToUserId(), tweet.getFromUserId(), tweet.getLanguageCode(), tweet.getSource());
    }

}
