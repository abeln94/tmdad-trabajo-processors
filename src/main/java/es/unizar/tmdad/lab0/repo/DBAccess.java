package es.unizar.tmdad.lab0.repo;

import es.unizar.tmdad.lab0.settings.Preferences.level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DBAccess {

    @Autowired
    private DBRepository config;

    public DBTableRow getSettings() {
        for (DBTableRow settings : config.findAll()) {
            return settings;
        }

        DBTableRow data = new DBTableRow();
        data.setName("disabled");
        data.setLevel(level.NONE.toString());
        return data;
    }

    public void setSettings(DBTableRow settings) {
        config.deleteAll();
        config.save(settings);
    }
}
