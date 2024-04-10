package com.example.managedevices;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Device implements Serializable {
    private String id = "";
    private String idFactory = "";
    private String idArea = "";
    private String name = "";
    private String manageCode = " ";
    private String installationDay = "";
    private String location = "";
    private String inspector = "";
    private String maintenancePerson = "";
    private String description = "";
    private String imgUpload = "";

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

    public String getIdArea() {
        return idArea;
    }

    public void setIdArea(String idArea) {
        this.idArea = idArea;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getManageCode() {
        return manageCode;
    }

    public void setManageCode(String manageCode) {
        this.manageCode = manageCode;
    }

    public String getInstallationDay() {
        return installationDay;
    }

    public void setInstallationDay(String installationDay) {
        this.installationDay = installationDay;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getInspector() {
        return inspector;
    }

    public void setInspector(String inspector) {
        this.inspector = inspector;
    }

    public String getMaintenancePerson() {
        return maintenancePerson;
    }

    public void setMaintenancePerson(String maintenancePerson) {
        this.maintenancePerson = maintenancePerson;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImgUpload() {
        return imgUpload;
    }

    public void setImgUpload(String imgUpload) {
        this.imgUpload = imgUpload;
    }

    @NonNull
    @Override
    public String toString() {
        return this.name;
    }
}
