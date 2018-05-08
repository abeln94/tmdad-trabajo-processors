package es.unizar.tmdad.lab0.processors;

import es.unizar.tmdad.lab0.settings.Preferences;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.stereotype.Service;

/**
 * Leet Processor
 * - Converts some letters to l33t equivalent
 */
@Service
@Profile("leet")
public class LeetProcessor implements Processor {

    /**
     * Retrieves preferences
     */
    @Autowired
    private Preferences prefs;

    private final Random random = new Random();

    @Override
    public List<Tweet> parseTweet(Tweet tweet) {
        String originalText = tweet.getText();

        String newText = Pattern.compile("\\b").splitAsStream(originalText)
                .map(t -> leetionate(t))
                .collect(Collectors.joining());

        return Collections.singletonList(TweetModified.modifyTweet(tweet, newText));

    }

    private String leetionate(String t) {

        if (t.length() <= 0) {
            return t;
        }

        char[] chars = t.toCharArray();

        int c;

        switch (prefs.getProcessorLevel()) {
            case LOW:
                c = random.nextInt(chars.length);
                chars[c] = convertChar(chars[c]);
                break;
            case MEDIUM:
                for (int i = 0; i < chars.length; i++) {
                    c = random.nextInt(chars.length);
                    chars[c] = convertChar(chars[c]);
                }
                break;
            case HIGH:
                for (c = 0; c < chars.length; c++) {
                    chars[c] = convertChar(chars[c]);
                }
                break;
            case NONE:
            default:
                break;
        }

        return new String(chars);

    }

    //from https://stackoverflow.com/questions/33416987/java-simple-english-to-leet-converter
    private final char english[] = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
    private final char leet[] = {'4', '8', '(', ')', '3', '}', '6', '#', '!', ']', 'X', '|', 'M', 'N', '0', '9', 'Q', '2', 'Z', '7', 'M', 'V', 'W', 'X', 'J', 'Z'};

    private char convertChar(char c) {
        for (int j = 0; j < english.length; j++) {
            if (Character.toUpperCase(c) == english[j]) {
                return leet[j];
            }
        }
        return c;
    }

}
