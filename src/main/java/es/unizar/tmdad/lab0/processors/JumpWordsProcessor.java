package es.unizar.tmdad.lab0.processors;

import es.unizar.tmdad.lab0.settings.Preferences;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.stereotype.Service;

/**
 * JumpWords Processor
 * - Swap words
 */
@Service
@Profile("jumpWords")
public class JumpWordsProcessor implements Processor {

    /**
     * Retrieves preferences
     */
    @Autowired
    private Preferences preferences;

    private final Random random = new Random();

    @Override
    public List<Tweet> parseTweet(Tweet tweet) {
        String originalText = tweet.getText();

        return Collections.singletonList(TweetModified.modifyTweet(tweet, String.join(" ", dislexionate(originalText.split(" ")))));

    }

    private String[] dislexionate(String[] words) {

        if (words.length <= 1) {
            return words;
        }

        int repeat;

        switch (preferences.getProcessorLevel()) {
            case LOW:
                repeat = 1;
                break;
            case MEDIUM:
                repeat = words.length / 2;
                break;
            case HIGH:
                repeat = words.length;
                break;
            case NONE:
            default:
                repeat = 0;
        }

        for (int i = 0; i < repeat; i++) {
            int a = random.nextInt(words.length);
            int b = random.nextInt(words.length);

            String temp = words[a];
            words[a] = words[b];
            words[b] = temp;
        }

        return words;

    }

}
