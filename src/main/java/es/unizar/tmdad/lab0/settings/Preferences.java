package es.unizar.tmdad.lab0.settings;

import es.unizar.tmdad.lab0.repo.DBTableRow;
import es.unizar.tmdad.lab0.repo.DBAccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Clase Preferences. 
 */
@Service
public class Preferences {

    @Autowired
    DBAccess twac;

    public enum level {

        NONE, LOW, MEDIUM, HIGH,
    };

    private String processorName;
    private String processorLevel;

    public String getProcessorName() {
        return processorName;
    }

    public level getProcessorLevel() {
        try {
            return level.valueOf(processorLevel);
        } catch (IllegalArgumentException e) {
            return level.NONE;
        }
    }

    public void setConfiguration(String processorName, String processorLevel) {
        this.processorName = processorName;
        this.processorLevel = processorLevel;
        
        DBTableRow data = new DBTableRow();
        data.setName(processorName);
        data.setLevel(processorLevel);
        twac.setSettings(data);
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
        DBTableRow settings = twac.getSettings();
        this.processorName = settings.getName();
        this.processorLevel = settings.getLevel();
        System.out.println("Loaded preferences {"+processorName+";"+processorLevel);
    }
}
