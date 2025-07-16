import org.example.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FishingJournalFilterTest {
    private FishingJournal journal;
    private FishingLocation lake;
    private FishingLocation river;
    private FishingGear gear;
    private Bait bait;

    @BeforeEach
    void setUp() {
        Fisherman fisherman = new Fisherman("Иван", "Сидоров", "ivan_angler");
        journal = new FishingJournal(fisherman);

        lake = new FishingLocation("Озеро Глубокое", "Пресное озеро");
        river = new FishingLocation("Река Быстрая", "Река с течением");
        gear = new FishingGear("Удочка", "Бренд", "Характеристики");
        bait = new Bait("Червь", "Натуральная", "Красный");

        // Добавим тестовые поездки
        journal.addTrip("2023-07-15", lake, "Солнечно", 25.0, 5, 760, 4.5, gear, bait);
        journal.addTrip("2023-07-22", lake, "Пасмурно", 20.0, 3, 755, 6.0, gear, bait);
        journal.addTrip("2023-08-05", river, "Дождь", 18.0, 7, 750, 3.5, gear, bait);
    }

    @Test
    void testFilterByLocation() {
        List<FishingTrip> lakeTrips = journal.filterByLocation("Озеро");
        assertEquals(2, lakeTrips.size());
        assertTrue(lakeTrips.stream().allMatch(trip ->
                trip.getLocation().getName().contains("Озеро")));

        List<FishingTrip> riverTrips = journal.filterByLocation("Река");
        assertEquals(1, riverTrips.size());
        assertEquals("Река Быстрая", riverTrips.get(0).getLocation().getName());
    }

    @Test
    void testFilterByDateRange() {
        List<FishingTrip> julyTrips = journal.filterByDateRange("2023-07-01", "2023-07-31");
        assertEquals(2, julyTrips.size());
        assertTrue(julyTrips.stream().allMatch(trip ->
                trip.getDate().startsWith("2023-07")));

        List<FishingTrip> augustTrips = journal.filterByDateRange("2023-08-01", "2023-08-31");
        assertEquals(1, augustTrips.size());
        assertEquals("2023-08-05", augustTrips.get(0).getDate());
    }

    @Test
    void testFilterByLocationAndDate() {
        List<FishingTrip> julyLakeTrips = journal.filterByLocationAndDate("Озеро", "2023-07-01", "2023-07-31");
        assertEquals(2, julyLakeTrips.size());

        List<FishingTrip> augustLakeTrips = journal.filterByLocationAndDate("Озеро", "2023-08-01", "2023-08-31");
        assertEquals(0, augustLakeTrips.size());
    }

    @Test
    void testInvalidDateRange() {
        List<FishingTrip> invalidRange = journal.filterByDateRange("2023-07-31", "2023-07-01");
        assertTrue(invalidRange.isEmpty());

        List<FishingTrip> invalidFormat = journal.filterByDateRange("2023/07/01", "2023/07/31");
        assertTrue(invalidFormat.isEmpty());
    }

    @Test
    void testEmptyLocationFilter() {
        List<FishingTrip> emptyFilter = journal.filterByLocation("");
        assertTrue(emptyFilter.isEmpty());
    }

    @Test
    void testEdgeDateCases() {
        // Фильтр с граничными датами
        List<FishingTrip> edgeCase = journal.filterByDateRange("2023-07-22", "2023-07-22");
        assertEquals(1, edgeCase.size());
        assertEquals("2023-07-22", edgeCase.get(0).getDate());
    }


    @Test
    void testNullParameters() {
        List<FishingTrip> nullLocation = journal.filterByLocation(null);
        assertTrue(nullLocation.isEmpty());

        List<FishingTrip> nullDate = journal.filterByDateRange(null, "2023-07-31");
        assertTrue(nullDate.isEmpty());
    }

    @Test
    void testNonExistentLocation() {
        List<FishingTrip> noMatch = journal.filterByLocation("Море");
        assertTrue(noMatch.isEmpty());
    }
    @Test
    void testCatchRecordCreation() {
        FishSpecies species = new FishSpecies("Щука", "Хищная", 0.5, 15.0);
        CatchRecord record = new CatchRecord(species, 3.2, 65.0);

        assertEquals(species, record.getSpecies());
        assertEquals(3.2, record.getWeight(), 0.001);
        assertEquals(65.0, record.getLength(), 0.001);
    }

    @Test
    void testFishermanProperties() {
        Fisherman fisherman = new Fisherman("Иван", "Сидоров", "ivan_angler");

        assertEquals("Иван", fisherman.getFirstName());
        assertEquals("Сидоров", fisherman.getLastName());
        assertEquals("ivan_angler", fisherman.getUsername());
    }


    @Test
    void testFishSpeciesProperties() {
        FishSpecies species = new FishSpecies("Щука", "Хищная", 0.5, 15.0);

        assertEquals("Щука", species.getName());
        assertEquals("Хищная", species.getDescription());
        assertEquals(0.5, species.getMinWeight(), 0.001);
        assertEquals(15.0, species.getMaxWeight(), 0.001);
    }

    @Test
    void testInvalidWeights() {
        assertThrows(IllegalArgumentException.class, () ->
                new FishSpecies("Щука", "Хищная", -1.0, 15.0));
    }
}
