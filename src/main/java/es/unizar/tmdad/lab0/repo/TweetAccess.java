package es.unizar.tmdad.lab0.repo;


import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;


@Service
public class TweetAccess {

    @Autowired
    private ConfigPRepository config;
    
    
    public ConfigProcessors getSettings(){
    	Iterable<ConfigProcessors> it = config.findAll();
    	for(ConfigProcessors settings: it){
    		return settings;
    	}
    	
    	return null;
    }
}
