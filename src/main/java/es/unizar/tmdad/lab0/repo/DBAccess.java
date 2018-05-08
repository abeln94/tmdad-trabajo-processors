package es.unizar.tmdad.lab0.repo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Acces to database
 * - Retrieves data
 * - Saves data
 */
@Service
public class DBAccess {

    /**
     * Settings repository
     */
    @Autowired
    private DBRepository repoSettings;

    public String getSettings(String key) {
        for (DBTableRow settings : repoSettings.findAll()) {
            if (settings.getKey().equals(key)) {
                return settings.getValue();
            }
        }
        return null;
    }

    public void setSettings(String key, String value) {
        DBTableRow row = new DBTableRow();
        row.setKey(key);
        row.setValue(value);
        repoSettings.save(row);
    }
}
