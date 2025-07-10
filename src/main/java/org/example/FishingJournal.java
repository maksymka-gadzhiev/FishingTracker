package org.example;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
public class FishingJournal {
    private Fisherman fisherman;
    private List<FishingTrip> trips;
    private Map<Bait, Map<FishSpecies, Integer>> baitEffectiveness;

    public FishingJournal(Fisherman fisherman) {
        this.trips = new ArrayList<>();
        this.fisherman = fisherman;
        this.baitEffectiveness = new HashMap<>();
        System.out.println("Конструктор инициализирован, trips = " + trips);
    }

    public FishingTrip addTrip(String date, FishingLocation location, String weatherConditions, double duration,
                               FishingGear gear, Bait bait) {
        FishingTrip trip = new FishingTrip(date, location, weatherConditions, duration, gear, bait, new ArrayList<>());
        trips.add(trip);
        return trip;
    }

    public void addCatch(FishingTrip trip, FishSpecies species, double weight, double length) {
        CatchRecord catchRecord = new CatchRecord(species, weight, length);
        trip.addCatch(catchRecord);

        Bait bait = trip.getBait();
        baitEffectiveness.putIfAbsent(bait, new HashMap<>());
        Map<FishSpecies, Integer> speciesCount = baitEffectiveness.get(bait);
        speciesCount.put(species, speciesCount.getOrDefault(species, 0) + 1);
    }

    public void printTotalStats() {
        int totalFish = trips.stream().mapToInt(FishingTrip::getTotalFish).sum();
        double totalWeight = trips.stream().mapToDouble(FishingTrip::getTotalWeight).sum();

        System.out.println("Рыбак: " + fisherman);
        System.out.println("Всего рыбалок: " + trips.size());
        System.out.println("Общий улов: " + totalFish + " рыб");
        System.out.println("Общий вес: " + String.format("%.2f", totalWeight) + " кг");
        System.out.println("Средний улов за рыбалку: " + String.format("%.1f", (double)totalFish/trips.size()));
    }

    public void printSpeciesStats() {
        Map<FishSpecies, Integer> speciesCount = new HashMap<>();
        Map<FishSpecies, Double> speciesWeight = new HashMap<>();

        for (FishingTrip trip : trips) {
            for (CatchRecord cr : trip.getCatches()) {
                FishSpecies species = cr.getSpecies();
                speciesCount.put(species, speciesCount.getOrDefault(species, 0) + 1);
                speciesWeight.put(species, speciesWeight.getOrDefault(species, 0.0) + cr.getWeight());
            }
        }

        speciesCount.forEach((species, count) -> {
            double avgWeight = speciesWeight.get(species) / count;
            System.out.printf("%s: %d шт, общий вес %.2f кг, средний вес %.2f кг%n",
                    species.getName(), count, speciesWeight.get(species), avgWeight);
        });
    }

    public void printTripReport(FishingTrip trip) {
        System.out.println("Дата: " + trip.getDate());
        System.out.println("Место: " + trip.getLocation());
        System.out.println("Погода: " + trip.getWeatherConditions());
        System.out.println("Длительность: " + trip.getDuration() + " часов");
        System.out.println("Снасть: " + trip.getGear());
        System.out.println("Приманка: " + trip.getBait());
        System.out.println("\nУлов (" + trip.getTotalFish() + " рыб, " +
                String.format("%.2f", trip.getTotalWeight()) + " кг):");

        trip.getCatches().forEach(catchRecord ->
                System.out.println(" - " + catchRecord));
    }

    public void printBaitEffectiveness() {
        baitEffectiveness.forEach((bait, speciesMap) -> {
            System.out.println("Приманка: " + bait);
            speciesMap.forEach((species, count) ->
                    System.out.println(" - " + species.getName() + ": " + count + " рыб"));
        });
    }

    public List<FishingTrip> getTrips() {
        return new ArrayList<>(trips);
    }


}
