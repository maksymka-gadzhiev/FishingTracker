import static org.mockito.Mockito.*;

import org.example.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.*;
@ExtendWith(MockitoExtension.class)
public class FishingJournalMockTest {

    @Mock
    private Logger logger;

    private FishingJournal journal;

    // Для тестирования printTripReport
    private FishingTrip trip1;
    private FishingTrip trip2;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        journal = new FishingJournal();
        trip1 = new FishingTrip("2023-07-15", new FishingLocation("Озеро", ""), "Погода", 3.0, 3, 760, 2.0, null, null);
        trip2 = new FishingTrip("2023-07-20", new FishingLocation("Река", ""), "Погода", 4.0, 4, 755, 3.0, null, null);
        journal.getTrips().addAll(Arrays.asList(trip1, trip2));
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    void testAddCatchCallsTripAddCatch() {
        FishingTrip trip = mock(FishingTrip.class);
        FishSpecies species = new FishSpecies("Щука", "Хищная", 0.5, 15.0);
        double weight = 3.0;
        double length = 70.0;

        journal.getTrips().add(trip);
        journal.addCatch(trip, species, weight, length);

        verify(trip, times(1)).addCatch(any(CatchRecord.class));
        verify(trip).addCatch(argThat(cr ->
                cr.getSpecies().equals(species) &&
                        cr.getWeight() == weight &&
                        cr.getLength() == length
        ));
    }

    @Test
    void testPrintTripReportLogsCorrectly() {

        FishingLocation location = new FishingLocation("Озеро Глубокое", "Пресное озеро");
        FishingGear gear = new FishingGear("Удочка", "Бренд", "Характеристики");
        Bait bait = new Bait("Червь", "Натуральная", "Красный");
        FishingTrip trip = new FishingTrip("2023-07-15", location, "Солнечно", 25.0, 5, 760, 4.5, gear, bait);
        trip.addCatch(new CatchRecord(new FishSpecies("Щука", "Хищная", 0.5, 15.0), 2.5, 60));
        trip.addCatch(new CatchRecord(new FishSpecies("Карп", "Пресноводная", 1.0, 20.0), 4.0, 70));

        journal.printTripReport(trip);

        verify(logger).info(contains("Отчет по поездке:"));
        verify(logger).info(contains("Дата: 2023-07-15"));
        verify(logger).info(contains("Место: Озеро Глубокое"));
        verify(logger).info(contains("Погода: Солнечно"));
    }
    @Test
    void testPrintBaitEffectivenessOutputsData() {
        Bait bait1 = new Bait("Червь", "Натуральная", "Красный");
        Bait bait2 = new Bait("Мормышка", "Искусственная", "Черная");
        Map<FishSpecies, Integer> speciesMap1 = new HashMap<>();
        speciesMap1.put(new FishSpecies("Щука", "", 0.5, 15.0), 3);
        Map<FishSpecies, Integer> speciesMap2 = new HashMap<>();
        speciesMap2.put(new FishSpecies("Карп", "", 1.0, 20.0), 2);

        journal.getBaitEffectiveness().put(bait1, speciesMap1);
        journal.getBaitEffectiveness().put(bait2, speciesMap2);

        journal.printBaitEffectiveness();

        String output = outContent.toString();

        Assertions.assertTrue(output.contains("Приманка: Байт(type=Червь, brand=Натуральная, color=Красный)")); // адаптируйте вывод
        Assertions.assertTrue(output.contains("Щука: 3 рыб"));
        Assertions.assertTrue(output.contains("Карп: 2 рыб"));
    }

    @Test
    void testFilterByLocationAndDate() {
        List<FishingTrip> result = journal.filterByLocationAndDate("Озеро", "2023-07-14", "2023-07-16");
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(trip1, result.get(0));
    }

    @Test
    void testEdgeDateCases() {
        List<FishingTrip> result = journal.filterByDateRange("2023-07-20", "2023-07-20");
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(trip2, result.get(0));
    }

    @Test
    void testNullParameters() {
        List<FishingTrip> nullLocation = journal.filterByLocation(null);
        Assertions.assertTrue(nullLocation.isEmpty());

        List<FishingTrip> nullDate = journal.filterByDateRange(null, null);
        Assertions.assertTrue(nullDate.isEmpty());
    }

    @Test
    void testInvalidDateRange() {
        List<FishingTrip> invalidRange = journal.filterByDateRange("2023-07-31", "2023-07-01");
        Assertions.assertTrue(invalidRange.isEmpty());

        List<FishingTrip> invalidFormat = journal.filterByDateRange("2023/07/01", "2023/07/31");
        Assertions.assertTrue(invalidFormat.isEmpty());
    }
}