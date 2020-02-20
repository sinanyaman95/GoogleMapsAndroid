package com.humber.saynn.sinanmapsapp;

public class Weather {
    private double temperature;
    private double feelsLike;
    private String description;
    private String iconURL;

    public Weather(double temperature, double feelsLike, String description, String iconURL) {
        this.temperature = temperature;
        this.feelsLike = feelsLike;
        this.description = description;
        this.iconURL = iconURL;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getFeelsLike() {
        return feelsLike;
    }

    public void setFeelsLike(double feelsLike) {
        this.feelsLike = feelsLike;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIconURL() {
        return iconURL;
    }

    public void setIconURL(String iconURL) {
        this.iconURL = iconURL;
    }

    @Override
    public String toString() {
        return "Weather{" +
                "temperature=" + temperature +
                ", feelsLike=" + feelsLike +
                ", description='" + description + '\'' +
                ", iconURL='" + iconURL + '\'' +
                '}';
    }
}
