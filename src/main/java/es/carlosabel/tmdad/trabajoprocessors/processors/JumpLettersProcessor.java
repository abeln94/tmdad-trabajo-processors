package es.carlosabel.tmdad.trabajoprocessors.processors;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.springframework.context.annotation.Profile;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.stereotype.Service;

/**
 * JumpLetters Processor
 * - Swap random letters on each word
 */
@Service
@Profile("jumpLetters")
public class JumpLettersProcessor extends Processor {

    @Override
    public List<Tweet> parseTweet(Tweet tweet) {
        String originalText = tweet.getText();

        String newText = Pattern.compile("\\b").splitAsStream(originalText)
                .map(t -> dislexionateWord(t))
                .collect(Collectors.joining());

        return Collections.singletonList(modifyTweet(tweet, newText));
    }

    private String dislexionateWord(String t) {
        if (t.length() <= 2) {
            return t;
        }

        char[] chars = t.toCharArray();

        int repeat;

        switch (prefs.getProcessorLevel()) {
            case LOW:
                repeat = 1;
                break;
            case MEDIUM:
                repeat = chars.length / 2;
                break;
            case HIGH:
                repeat = chars.length;
                break;
            case NONE:
            default:
                repeat = 0;
        }

        for (int i = 0; i < repeat; i++) {
            int a = 1 + random.nextInt(chars.length - 2);
            int b = 1 + random.nextInt(chars.length - 2);

            char temp = chars[a];
            chars[a] = chars[b];
            chars[b] = temp;
        }

        return new String(chars);

    }

}
