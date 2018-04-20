package es.unizar.tmdad.lab0.controller;

import es.unizar.tmdad.lab0.settings.Preferences;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SearchController {

    @Autowired
    private Preferences preferences;

    @RequestMapping("/")
    @ResponseBody
    public String greeting() {
        return new StringBuilder()
                .append("Current config { ")
                .append("ProcessorName=").append(preferences.getProcessorName()).append(" ; ")
                .append("ProcesorLevel=").append(preferences.getProcessorLevel().toString()).append(" ; ")
                .append(" }")
                .append("TweetsProcessed=").append(preferences.getTweetsProcessed())
                .toString();
    }

}
