package com.example.managedevices;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Place implements Serializable {
    private String id;
    private String idFactory;

    private String idPlace;
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdFactory() {
        return idFactory;
    }

    public void setIdFactory(String idFactory) {
        this.idFactory = idFactory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdPlace() {
        return idPlace;
    }

    public void setIdPlace(String idPlace) {
        this.idPlace = idPlace;
    }

    @NonNull
    @Override
    public String toString() {
        return this.getName();
    }
}
