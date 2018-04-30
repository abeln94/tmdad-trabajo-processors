package es.unizar.tmdad.lab0.settings;

import es.unizar.tmdad.lab0.repo.DBAccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

/**
 * Clase Preferences. 
 */
@Service
public class Preferences {
    
    
    String profile_name;
    
    public String getProfileName(){
        return profile_name;
    }
    
    
    Preferences(@Value("${SPRING_PROFILES_ACTIVE}") String profileName){
        profile_name = profileName;
    }
    
    

    @Autowired
    DBAccess twac;

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

    private int tweetsProcessed = 0;

    public void increaseTweetsProcessed() {
        tweetsProcessed++;
    }

    public int getTweetsProcessed() {
        return tweetsProcessed;
    }

    //loader
    @EventListener
    public void handleContextRefresh(ContextRefreshedEvent event) {
        processorLevel = twac.getSettings(profile_name);
        if(processorLevel==null){
            processorLevel = level.NONE.toString();
        }
        System.out.println("Loaded preferences LEVEL="+this.processorLevel);
    }
}
