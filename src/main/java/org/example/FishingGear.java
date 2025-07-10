package org.example;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FishingGear {
    private String type;
    private String brand;
    private String specifications;
}
