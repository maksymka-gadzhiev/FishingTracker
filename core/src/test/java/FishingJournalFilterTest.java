import org.example.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
}
