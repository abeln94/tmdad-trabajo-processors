package es.unizar.tmdad.lab0.settings;

import es.unizar.tmdad.lab0.repo.DBAccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Keeps the preferences of the application
 * - Getters to retrieve
 * - Setters to save
 */
@Service
public class Preferences {

    /**
     * Saves and gets settings from database
     */
    @Autowired
    DBAccess twac;

    //-----------------profile name--------------
    String profile_name;

    Preferences(@Value("${SPRING_PROFILES_ACTIVE}") String profileName) {
        profile_name = profileName;
    }

    public String getProfileName() {
        return profile_name;
    }

    //-----------------level---------------
    public enum level {

        NONE, LOW, MEDIUM, HIGH,
    };

    private String processorLevel;

    public level getProcessorLevel() {
        try {
            return level.valueOf(processorLevel);
        } catch (IllegalArgumentException e) {
            return level.NONE;
        }
    }

    public void setConfiguration(String processorLevel) {
        this.processorLevel = processorLevel;
        twac.setSettings(profile_name, processorLevel);
    }

    //----------------tweets processed----------------
    private int tweetsProcessed = 0;

    public void increaseTweetsProcessed() {
        tweetsProcessed++;
    }

    public int getTweetsProcessed() {
        return tweetsProcessed;
    }

    //------------startup loader---------------
    @EventListener
    public void handleContextRefresh(ContextRefreshedEvent event) {
        processorLevel = twac.getSettings(profile_name);
        if (processorLevel == null) {
            processorLevel = level.NONE.toString();
        }
        System.out.println("Loaded preferences LEVEL=" + this.processorLevel);
    }
}
