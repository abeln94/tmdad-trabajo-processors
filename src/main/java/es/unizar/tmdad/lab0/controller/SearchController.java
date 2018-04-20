package es.unizar.tmdad.lab0.controller;

import es.unizar.tmdad.lab0.processors.Processor;
import es.unizar.tmdad.lab0.settings.Preferences;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import es.unizar.tmdad.lab0.repo.ConfigProcessors;
import es.unizar.tmdad.lab0.repo.TweetAccess;

@Controller
public class SearchController {
    
    
    @Autowired
    private Preferences preferences;
    
    @Autowired
    private TweetAccess twac;

    @RequestMapping("/")
    @ResponseBody
    public String greeting() {
        return new StringBuilder()
                .append("Current config { ")
                .append("ProcessorName=").append(preferences.getProcessorName()).append(" ; ")
                .append("ProcesorLevel=").append(preferences.getProcessorLevel()).append(" ; ")
                .append(" }")
                .append("TweetsProcessed=").append(preferences.getTweetsProcessed())
                .toString();
    }
    
    //loader
    @EventListener
    public void handleContextRefresh(ContextRefreshedEvent event) {
        ConfigProcessors settings = twac.getSettings();
        preferences.setProcessorName(settings.getProcessor());
        preferences.setProcessorLevel(Processor.level.valueOf(settings.getLevel()));
        System.out.println("Loaded preferences");
    }

}
