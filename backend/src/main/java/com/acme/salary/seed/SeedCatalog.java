package com.acme.salary.seed;

import com.acme.salary.common.Country;
import java.math.BigDecimal;
import java.util.List;

/**
 * Reference data behind the seed: who works where, in which roles, and roughly what each role pays.
 *
 * <p>Salary bands are in US-dollar equivalents. Each country's {@code marketFactor} turns a band into a
 * plausible local salary in local currency (for example, 22 rupees of salary per US dollar of band),
 * reflecting both exchange rates and local pay levels. Figures are illustrative, not market data.
 */
final class SeedCatalog {

    private SeedCatalog() {
    }

    record JobProfile(String title, String department, int minBand, int maxBand, int weight) {
    }

    record CountryProfile(Country country, int weight, BigDecimal marketFactor,
                          List<String> firstNames, List<String> lastNames) {
    }

    static final List<JobProfile> JOBS = List.of(
            new JobProfile("Software Engineer", "Engineering", 90_000, 130_000, 25),
            new JobProfile("Senior Software Engineer", "Engineering", 130_000, 180_000, 15),
            new JobProfile("Engineering Manager", "Engineering", 170_000, 230_000, 4),
            new JobProfile("QA Engineer", "Engineering", 75_000, 105_000, 8),
            new JobProfile("Data Analyst", "Data", 80_000, 115_000, 6),
            new JobProfile("Product Manager", "Product", 120_000, 170_000, 6),
            new JobProfile("Product Designer", "Design", 95_000, 140_000, 5),
            new JobProfile("Account Executive", "Sales", 70_000, 120_000, 10),
            new JobProfile("Sales Manager", "Sales", 110_000, 160_000, 3),
            new JobProfile("Marketing Specialist", "Marketing", 65_000, 95_000, 6),
            new JobProfile("HR Generalist", "People", 60_000, 85_000, 4),
            new JobProfile("Financial Analyst", "Finance", 75_000, 110_000, 5),
            new JobProfile("Customer Support Specialist", "Customer Success", 45_000, 65_000, 8));

    static final List<CountryProfile> COUNTRIES = List.of(
            new CountryProfile(Country.INDIA, 30, new BigDecimal("22.0"),
                    List.of("Aarav", "Vivaan", "Aditya", "Arjun", "Rohan", "Ishaan", "Priya", "Ananya",
                            "Diya", "Kavya", "Meera", "Sneha", "Rahul", "Neha", "Vikram", "Pooja"),
                    List.of("Sharma", "Verma", "Patel", "Iyer", "Reddy", "Nair", "Gupta", "Rao",
                            "Mehta", "Joshi", "Kulkarni", "Desai", "Singh", "Das", "Menon", "Chopra")),
            new CountryProfile(Country.UNITED_STATES, 25, new BigDecimal("1.00"),
                    List.of("James", "Michael", "Robert", "David", "John", "Emily", "Sarah", "Jessica",
                            "Ashley", "Olivia", "Daniel", "Matthew", "Lauren", "Megan", "Chris", "Rachel"),
                    List.of("Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller", "Davis",
                            "Martinez", "Wilson", "Anderson", "Taylor", "Thomas", "Moore", "Jackson", "White")),
            new CountryProfile(Country.UNITED_KINGDOM, 10, new BigDecimal("0.62"),
                    List.of("Oliver", "George", "Harry", "Jack", "Charlie", "Thomas", "Amelia", "Olivia",
                            "Isla", "Emily", "Poppy", "Sophie", "William", "Lucy", "Alfie", "Grace"),
                    List.of("Smith", "Jones", "Taylor", "Brown", "Williams", "Wilson", "Evans", "Thomas",
                            "Roberts", "Walker", "Wright", "Hughes", "Edwards", "Green", "Hall", "Wood")),
            new CountryProfile(Country.GERMANY, 10, new BigDecimal("0.68"),
                    List.of("Lukas", "Leon", "Finn", "Jonas", "Paul", "Felix", "Maximilian", "Emma",
                            "Mia", "Hannah", "Lea", "Lena", "Anna", "Sophie", "Laura", "Julia"),
                    List.of("Müller", "Schmidt", "Schneider", "Fischer", "Weber", "Meyer", "Wagner", "Becker",
                            "Schulz", "Hoffmann", "Koch", "Richter", "Klein", "Wolf", "Schröder", "Neumann")),
            new CountryProfile(Country.CANADA, 8, new BigDecimal("1.00"),
                    List.of("Liam", "Noah", "William", "Benjamin", "Lucas", "Ethan", "Emma", "Olivia",
                            "Charlotte", "Chloé", "Ava", "Sophie", "Nathan", "Gabrielle", "Owen", "Zoe"),
                    List.of("Tremblay", "Gagnon", "Roy", "Côté", "Bouchard", "Smith", "Brown", "Wilson",
                            "Martin", "Lee", "MacDonald", "Campbell", "Gauthier", "Morin", "Taylor", "Leblanc")),
            new CountryProfile(Country.FRANCE, 7, new BigDecimal("0.58"),
                    List.of("Lucas", "Hugo", "Louis", "Gabriel", "Arthur", "Jules", "Léa", "Chloé",
                            "Manon", "Camille", "Inès", "Sarah", "Julien", "Mathilde", "Nicolas", "Claire"),
                    List.of("Martin", "Bernard", "Dubois", "Thomas", "Robert", "Richard", "Petit", "Durand",
                            "Leroy", "Moreau", "Simon", "Laurent", "Lefebvre", "Michel", "Garcia", "Fournier")),
            new CountryProfile(Country.AUSTRALIA, 5, new BigDecimal("1.05"),
                    List.of("Jack", "Oliver", "William", "Noah", "Thomas", "Charlotte", "Amelia", "Olivia",
                            "Isla", "Mia", "Ruby", "Lachlan", "Cooper", "Matilda", "Harper", "Hayden"),
                    List.of("Smith", "Jones", "Williams", "Brown", "Wilson", "Taylor", "Johnson", "White",
                            "Martin", "Anderson", "Thompson", "Nguyen", "Ryan", "Kelly", "Walker", "Harris")),
            new CountryProfile(Country.SINGAPORE, 5, new BigDecimal("1.00"),
                    List.of("Wei Ling", "Jun Jie", "Hui Min", "Kai Xiang", "Siti", "Nur Aisyah", "Muhammad", "Arjun",
                            "Priya", "Ethan", "Chloe", "Ryan", "Mei Ling", "Zhi Hao", "Farah", "Darren"),
                    List.of("Tan", "Lim", "Lee", "Ng", "Ong", "Wong", "Goh", "Chua",
                            "Chan", "Koh", "Teo", "Ang", "Yeo", "Rahman", "Kumar", "Lau")));
}
