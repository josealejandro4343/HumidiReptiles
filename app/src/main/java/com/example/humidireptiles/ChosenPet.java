package com.example.humidireptiles;

public class ChosenPet {
    String petname, petSpecie, petImage, isChosen;
    Integer required_watertemp, required_humidity, required_temp, required_watertempmax, required_humidity_max, required_tempmax;
    //Float ;

    ChosenPet(){

    }
    ChosenPet(String petname, String petSpecie, Integer required_temp, Integer required_tempmax, Integer required_watertemp, Integer required_watertempmax, Integer required_humidity, Integer required_humidity_max, String petImage){

    }

    public ChosenPet(String petname, String petSpecie, String petImage, String isChosen, Integer required_watertemp, Integer required_humidity, Integer required_temp, Integer required_watertempmax, Integer required_humidity_max, Integer required_tempmax) {
        this.petname = petname;
        this.petSpecie = petSpecie;
        this.petImage = petImage;
        this.isChosen = isChosen;
        this.required_watertemp = required_watertemp;
        this.required_humidity = required_humidity;
        this.required_temp = required_temp;
        this.required_watertempmax = required_watertempmax;
        this.required_humidity_max = required_humidity_max;
        this.required_tempmax = required_tempmax;
    }

    public String getPetname() {
        return petname;
    }

    public void setPetname(String petname) {
        this.petname = petname;
    }

    public String getPetSpecie() {
        return petSpecie;
    }

    public void setPetSpecie(String petSpecie) {
        this.petSpecie = petSpecie;
    }

    public String getPetImage() {
        return petImage;
    }

    public void setPetImage(String petImage) {
        this.petImage = petImage;
    }

    public String getIsChosen() {
        return isChosen;
    }

    public void setIsChosen(String isChosen) {
        this.isChosen = isChosen;
    }

    public Integer getRequired_watertemp() {
        return required_watertemp;
    }

    public void setRequired_watertemp(Integer required_watertemp) {
        this.required_watertemp = required_watertemp;
    }

    public Integer getRequired_humidity() {
        return required_humidity;
    }

    public void setRequired_humidity(Integer required_humidity) {
        this.required_humidity = required_humidity;
    }

    public Integer getRequired_temp() {
        return required_temp;
    }

    public void setRequired_temp(Integer required_temp) {
        this.required_temp = required_temp;
    }

    public Integer getRequired_watertempmax() {
        return required_watertempmax;
    }

    public void setRequired_watertempmax(Integer required_watertempmax) {
        this.required_watertempmax = required_watertempmax;
    }

    public Integer getRequired_humidity_max() {
        return required_humidity_max;
    }

    public void setRequired_humidity_max(Integer required_humidity_max) {
        this.required_humidity_max = required_humidity_max;
    }

    public Integer getRequired_tempmax() {
        return required_tempmax;
    }

    public void setRequired_tempmax(Integer required_tempmax) {
        this.required_tempmax = required_tempmax;
    }
}
