package org.example;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class FishingTrip {
    private String date;
    private FishingLocation location;
    private String weatherConditions;
    private double duration;
    private FishingGear gear;
    private Bait bait;
    private List<CatchRecord> catches = new ArrayList<>();

    public void addCatch(CatchRecord catchRecord) {
        catches.add(catchRecord);
    }
    public double getTotalWeight() {
        return catches.stream().mapToDouble(CatchRecord::getWeight).sum();
    }

    public int getTotalFish() {
        return catches.size();
    }

}

