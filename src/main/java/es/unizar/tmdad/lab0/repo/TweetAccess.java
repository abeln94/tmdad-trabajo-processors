package es.unizar.tmdad.lab0.repo;

import es.unizar.tmdad.lab0.settings.Preferences.level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TweetAccess {

    @Autowired
    private ConfigPRepository config;

    public ConfigProcessors getSettings() {
        for (ConfigProcessors settings : config.findAll()) {
            return settings;
        }

        ConfigProcessors data = new ConfigProcessors();
        data.setName("disabled");
        data.setLevel(level.NONE.toString());
        return data;
    }

    public void setSettings(ConfigProcessors settings) {
        config.deleteAll();
        config.save(settings);
    }
}
