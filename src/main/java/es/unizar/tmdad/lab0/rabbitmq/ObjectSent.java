package es.unizar.tmdad.lab0.rabbitmq;

import java.io.Serializable;

public class ObjectSent implements Serializable{
    String name;
    int id;

    public ObjectSent(String name, int id) {
        this.name = name;
        this.id = id;
    }
    
}
