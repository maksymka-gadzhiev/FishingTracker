package org.example;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FishSpecies {
    private String name;
    private String description;
    private double minWeight;
    private double maxWeight;
}
