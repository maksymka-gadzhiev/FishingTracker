package org.example;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CatchRecord {
    private FishSpecies species;
    private double weight;
    private double length;
}
