package es.carlosabel.tmdad.trabajoprocessors.dashboard;

import es.carlosabel.tmdad.trabajoprocessors.settings.Preferences;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller:
 * - Definition of uris
 * - Client-related functions
 */
@Controller
@RefreshScope
public class SearchController {

    /**
     * Retrieves preferences
     */
    @Autowired
    private Preferences prefs;

    @RequestMapping("/")
    @ResponseBody
    public String main() {
        return new StringBuilder()
                .append(prefs.getProfileName()).append(" --- ")
                .append("Current config { ")
                .append("ProcesorLevel=").append(prefs.getProcessorLevel().toString()).append(" ; ")
                .append(" }")
                .append("TweetsProcessed=").append(prefs.getTweetsProcessed())
                .toString();
    }

    @Value("${test}")
    String test;

    @RequestMapping(value = "/test")
    @ResponseBody
    public String test() {
        return test;
    }

}
