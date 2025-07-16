package org.example;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

public class FishingJournal {
    private static final Logger logger = LoggerFactory.getLogger(FishingJournal.class);
    private Fisherman fisherman;
    private List<FishingTrip> trips;
    private Map<Bait, Map<FishSpecies, Integer>> baitEffectiveness;

    public FishingJournal(Fisherman fisherman) {
        this.trips = new ArrayList<>();
        this.fisherman = fisherman;
        this.baitEffectiveness = new HashMap<>();
        logger.debug("Конструктор инициализирован, trips = {}", trips);
    }

    public FishingTrip addTrip(String date, FishingLocation location, String weatherConditions, double temperature,
                               int windSpeed, int pressure, double duration,
                               FishingGear gear, Bait bait) {
        logger.debug("Добавление поездки: date={}, location={}, weather={}, temp={}, wind={}, pressure={}, " +
                        "duration={}, gear={}, bait={}",
                date, location, weatherConditions, temperature, windSpeed, pressure, duration, gear, bait);
        FishingTrip trip = new FishingTrip(date, location, weatherConditions,
                temperature, windSpeed, pressure,
                duration, gear, bait);
        trips.add(trip);
        logger.info("Добавлена новая поездка ID: {}", trip.hashCode());
        logger.debug("Всего поездок: {}", trips.size());
        return trip;
    }

    public void addCatch(FishingTrip trip, FishSpecies species, double weight, double length) {
        logger.debug("Добавление улова: trip={}, species={}, weight={}, length={}",
                trip != null ? trip.hashCode() : "null", species, weight, length);
        if (trip == null) {
            logger.error("Попытка добавить улов в несуществующую поездку");
            return;
        }

        if (species == null) {
            logger.error("Не указан вид рыбы");
            return;
        }

        if (weight <= 0 || length <= 0) {
            logger.warn("Некорректные параметры рыбы: вес={} длина={}", weight, length);
        }

        CatchRecord catchRecord = new CatchRecord(species, weight, length);
        trip.addCatch(catchRecord);

        Bait bait = trip.getBait();
        baitEffectiveness.putIfAbsent(bait, new HashMap<>());
        Map<FishSpecies, Integer> speciesCount = baitEffectiveness.get(bait);
        speciesCount.put(species, speciesCount.getOrDefault(species, 0) + 1);
    }

    public void printTotalStats() {
        if (trips.isEmpty()) {
            logger.warn("Нет данных о поездках");
            return;
        }

        int totalFish = trips.stream().mapToInt(FishingTrip::getTotalFish).sum();
        double totalWeight = trips.stream().mapToDouble(FishingTrip::getTotalWeight).sum();

        String message = "Рыбак: " + fisherman + "\n" +
                "Всего рыбалок: " + trips.size() + "\n" +
                "Общий улов: " + totalFish + " рыб\n" +
                "Общий вес: " + String.format("%.2f", totalWeight) + " кг\n" +
                "Средний улов за рыбалку: " + String.format("%.1f", (double) totalFish / trips.size());

        logger.info(message);
    }

    public void printSpeciesStats() {
        if (trips.isEmpty()) {
            logger.warn("Нет данных для статистики по видам рыб");
            return;
        }

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
        if (trip == null) {
            logger.error("Попытка сформировать отчет для несуществующей поездки");
            return;
        }

        logger.debug("Формирование отчета для поездки ID: {}", trip.hashCode());

        StringBuilder report = new StringBuilder();
        report.append("Дата: ").append(trip.getDate()).append("\n");
        report.append("Место: ").append(trip.getLocation()).append("\n");
        report.append("Погода: ").append(trip.getWeatherConditions()).append("\n");
        report.append("Длительность: ").append(trip.getDuration()).append(" часов\n");
        report.append("Снасть: ").append(trip.getGear()).append("\n");
        report.append("Приманка: ").append(trip.getBait()).append("\n");

        report.append("\nУлов (").append(trip.getTotalFish()).append(" рыб, ")
                .append(String.format("%.2f", trip.getTotalWeight())).append(" кг):\n");

        for (CatchRecord cr : trip.getCatches()) {
            report.append(" - ").append(cr).append("\n");
        }

        logger.info("Отчет по поездке:\n{}", report);
    }

    public void printBaitEffectiveness() {
        if (baitEffectiveness.isEmpty()) {
            logger.warn("Нет данных об эффективности приманок");
            return;
        }
        baitEffectiveness.forEach((bait, speciesMap) -> {
            System.out.println("Приманка: " + bait);
            speciesMap.forEach((species, count) ->
                    System.out.println(" - " + species.getName() + ": " + count + " рыб"));
        });
    }

    public List<FishingTrip> filterByLocation(String locationName) {
        if (locationName == null || locationName.isBlank()) {
            logger.warn("Пустой параметр locationName");
            return new ArrayList<>();
        }

        String searchTerm = locationName.toLowerCase();
        return trips.stream()
                .filter(trip -> trip.getLocation().getName().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());
    }

    public List<FishingTrip> filterByDateRange(String startDate, String endDate) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate start = LocalDate.parse(startDate, formatter);
            LocalDate end = LocalDate.parse(endDate, formatter);

            if (start.isAfter(end)) {
                logger.warn("Начальная дата {} позже конечной {}", startDate, endDate);
                return new ArrayList<>();
            }

            return trips.stream()
                    .filter(trip -> {
                        LocalDate tripDate = LocalDate.parse(trip.getDate(), formatter);
                        return !tripDate.isBefore(start) && !tripDate.isAfter(end);
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Ошибка парсинга даты: {}", e.getMessage());
            return new ArrayList<>();
        }
    }
    public List<FishingTrip> filterByLocationAndDate(String locationName, String startDate, String endDate) {
        List<FishingTrip> locationFiltered = filterByLocation(locationName);
        List<FishingTrip> dateFiltered = filterByDateRange(startDate, endDate);

        return locationFiltered.stream()
                .filter(dateFiltered::contains)
                .collect(Collectors.toList());
    }
    public List<FishingTrip> getTrips() {
        return new ArrayList<>(trips);
    }


}
