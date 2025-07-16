package org.example;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@AllArgsConstructor
public class FishingTrip {
    private String date;
    private FishingLocation location;
    private String weatherConditions;
    private double duration;
    private FishingGear gear;
    private Bait bait;

    private double temperature;
    private int windSpeed;
    private int pressure;
    private List<CatchRecord> catches = new ArrayList<>();

    public FishingTrip(String date, FishingLocation location, String weatherConditions, double temperature,
                       int windSpeed, int pressure, double duration, FishingGear gear, Bait bait) {
        this.date = date;
        this.location = location;
        this.weatherConditions = weatherConditions;
        this.temperature =temperature;
        this.windSpeed = windSpeed;
        this.pressure = pressure;
        this.duration = duration;
        this.gear = gear;
        this.bait = bait;
    }

    public void addCatch(CatchRecord catchRecord) {
        catches.add(catchRecord);
    }
    public double getTotalWeight() {
        return catches.stream().mapToDouble(CatchRecord::getWeight).sum();
    }

    public int getTotalFish() {
        return catches.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FishingTrip that = (FishingTrip) o;
        return Objects.equals(date, that.date) &&
                Objects.equals(location, that.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, location);
    }

}

