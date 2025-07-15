package org.example;

public class Main {
    public static void main(String[] args) {
        // Описание задачи:
        // В этом примере создается система для учета рыбалок. Мы регистрируем рыбака,
        // создаем виды рыб, снасти, места рыбалки, а также записываем рыбалки и улов.
        // После этого выводится статистика по улову, по видам рыб, отчет по первой рыбалке
        // и информация о результативности приманок.
        // Создание рыбака
        Fisherman fisherman = new Fisherman("Иван", "Сидоров", "ivan_angler");

        // Инициализация журнала
        FishingJournal journal = new FishingJournal(fisherman);

        // Создание видов рыб
        FishSpecies perch = new FishSpecies("Окунь", "Пресноводная", 0.1, 2.0);
        FishSpecies pike = new FishSpecies("Щука", "Хищная", 0.5, 15.0);
        FishSpecies bream = new FishSpecies("Лещ", "Пресноводная", 0.3, 5.0);

        // Создание снастей
        FishingGear spinningRod = new FishingGear("Спиннинг", "Shimano", "2.1 м, тест 10-30 г");
        FishingGear floatRod = new FishingGear("Поплавочная удочка", "Daiwa", "5 м");
        Bait lure = new Bait("Воблер", "Rapala", "Красный");
        Bait worm = new Bait("Червь", "Натуральная", "Красный");

        // Создание мест рыбалки
        FishingLocation deepLake = new FishingLocation("Озеро Глубокое", "Пресное озеро");
        FishingLocation river = new FishingLocation("Река Быстрая", "Река с течением");

        // Регистрация рыбалок
        FishingTrip trip1 = journal.addTrip("2023-07-15", deepLake, "Солнечно",
                25, 2, 755, 4.5, spinningRod, lure);

        // Добавление улова
        journal.addCatch(trip1, perch, 0.45, 22.0);
        journal.addCatch(trip1, pike, 3.2, 65.0);
        journal.addCatch(trip1, perch, 0.38, 20.5);

        // Статистика
        System.out.println("=== Общая статистика ===");
        journal.printTotalStats();

        System.out.println("\n=== Статистика по видам рыб ===");
        journal.printSpeciesStats();

        System.out.println("\n=== Отчет по первой рыбалке ===");
        journal.printTripReport(trip1);

        System.out.println("\n=== Эффективность приманок ===");
        journal.printBaitEffectiveness();
    }

}