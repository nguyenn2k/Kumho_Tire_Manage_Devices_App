package com.example.managedevices;

import androidx.annotation.NonNull;

/**
 * Gồm ID và Tên nhà máy:
 * id: Factory_ID;
 * name: Name_Factory;
 */

public class Factory {
    private String id = "";
    private String Name = "";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    @NonNull
    @Override
    public String toString() {
        return this.getName();
    }
}