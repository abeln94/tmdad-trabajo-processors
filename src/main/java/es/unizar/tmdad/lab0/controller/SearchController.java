package es.unizar.tmdad.lab0.controller;

import es.unizar.tmdad.lab0.settings.Preferences;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller:
 * - Definition of uris
 * - Client-related functions
 */
@Controller
public class SearchController {

    /**
     * Retrieves preferences
     */
    @Autowired
    private Preferences preferences;

    @RequestMapping("/")
    @ResponseBody
    public String greeting() {
        return new StringBuilder()
                .append(preferences.getProfileName()).append(" --- ")
                .append("Current config { ")
                .append("ProcesorLevel=").append(preferences.getProcessorLevel().toString()).append(" ; ")
                .append(" }")
                .append("TweetsProcessed=").append(preferences.getTweetsProcessed())
                .toString();
    }

}
