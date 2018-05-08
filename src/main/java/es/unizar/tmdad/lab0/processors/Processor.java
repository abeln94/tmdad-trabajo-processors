package es.unizar.tmdad.lab0.processors;

import java.util.List;
import org.springframework.social.twitter.api.Tweet;

/**
 * Processor Interface
 */
public interface Processor {

    public List<Tweet> parseTweet(Tweet tweet);
}
