package com.acme.salary.seed;

import com.acme.salary.employee.EmployeeDetails;
import com.acme.salary.seed.SeedCatalog.CountryProfile;
import com.acme.salary.seed.SeedCatalog.JobProfile;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.function.ToIntFunction;

/**
 * Generates realistic, deterministic employee data: the same seed always produces the same employees.
 *
 * <p>Determinism relies on {@link Random}, whose algorithm is fixed by the Java specification.
 */
public class EmployeeGenerator {

    static final LocalDate EARLIEST_HIRE_DATE = LocalDate.of(2012, 1, 1);
    // Fixed rather than "today", so the output never changes and hire dates are never in the future.
    static final LocalDate LATEST_HIRE_DATE = LocalDate.of(2025, 12, 31);

    private static final String EMAIL_DOMAIN = "acme.example";
    private static final BigDecimal ROUND_TO = new BigDecimal("100");

    private final long seed;

    public EmployeeGenerator(long seed) {
        this.seed = seed;
    }

    public List<EmployeeDetails> generate(int count) {
        Random random = new Random(seed);
        Map<String, Integer> emailsInUse = new HashMap<>();
        List<EmployeeDetails> employees = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            employees.add(nextEmployee(random, emailsInUse));
        }
        return employees;
    }

    private static EmployeeDetails nextEmployee(Random random, Map<String, Integer> emailsInUse) {
        CountryProfile country = pickWeighted(random, SeedCatalog.COUNTRIES, CountryProfile::weight);
        JobProfile job = pickWeighted(random, SeedCatalog.JOBS, JobProfile::weight);
        String firstName = pick(random, country.firstNames());
        String lastName = pick(random, country.lastNames());
        return new EmployeeDetails(
                firstName + " " + lastName,
                uniqueEmail(firstName, lastName, emailsInUse),
                job.title(),
                job.department(),
                country.country(),
                salary(random, job, country),
                hireDate(random));
    }

    private static BigDecimal salary(Random random, JobProfile job, CountryProfile country) {
        int band = job.minBand() + random.nextInt(job.maxBand() - job.minBand() + 1);
        return BigDecimal.valueOf(band)
                .multiply(country.marketFactor())
                .divide(ROUND_TO, 0, RoundingMode.HALF_EVEN)
                .multiply(ROUND_TO)
                .setScale(2);
    }

    private static LocalDate hireDate(Random random) {
        long days = ChronoUnit.DAYS.between(EARLIEST_HIRE_DATE, LATEST_HIRE_DATE);
        return EARLIEST_HIRE_DATE.plusDays(random.nextLong(days + 1));
    }

    /** first.last@domain, with a number appended when the name is already taken (james.smith2@...). */
    private static String uniqueEmail(String firstName, String lastName, Map<String, Integer> emailsInUse) {
        String localPart = toEmailPart(firstName) + "." + toEmailPart(lastName);
        int occurrence = emailsInUse.merge(localPart, 1, Integer::sum);
        String suffix = occurrence == 1 ? "" : String.valueOf(occurrence);
        return localPart + suffix + "@" + EMAIL_DOMAIN;
    }

    /** "Schröder" becomes "schroder", "Wei Ling" becomes "wei.ling". */
    private static String toEmailPart(String name) {
        String withoutAccents = Normalizer.normalize(name, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return withoutAccents.toLowerCase(Locale.ROOT).replaceAll("[^a-z]+", ".");
    }

    private static <T> T pick(Random random, List<T> options) {
        return options.get(random.nextInt(options.size()));
    }

    private static <T> T pickWeighted(Random random, List<T> options, ToIntFunction<T> weight) {
        int total = options.stream().mapToInt(weight).sum();
        int ticket = random.nextInt(total);
        for (T option : options) {
            ticket -= weight.applyAsInt(option);
            if (ticket < 0) {
                return option;
            }
        }
        throw new IllegalStateException("Weights must be positive");
    }
}
