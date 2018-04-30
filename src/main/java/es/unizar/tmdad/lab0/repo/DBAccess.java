package es.unizar.tmdad.lab0.repo;

import es.unizar.tmdad.lab0.settings.Preferences.level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DBAccess {

    @Autowired
    private DBRepository config;

    public String getSettings(String key) {
        for (DBTableRow settings : config.findAll()) {
            if (settings.getName().equals(key)){
                return settings.getLevel();
            }
        }
        return null;
    }

    public void setSettings(String key, String value) {
        DBTableRow row = new DBTableRow();
        row.setName(key);
        row.setLevel(value);
        config.save(row);
    }
}
