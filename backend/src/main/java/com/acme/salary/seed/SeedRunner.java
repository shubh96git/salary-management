package com.acme.salary.seed;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Seeds the database on startup when {@code salary.seed.enabled=true}. Off by default so tests and
 * production never seed by accident; does nothing if employees already exist.
 */
@Component
@ConditionalOnProperty(name = "salary.seed.enabled", havingValue = "true")
class SeedRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(SeedRunner.class);

    private final EmployeeSeeder seeder;
    private final int employees;
    private final long randomSeed;

    SeedRunner(EmployeeSeeder seeder,
               @Value("${salary.seed.employees}") int employees,
               @Value("${salary.seed.random-seed}") long randomSeed) {
        this.seeder = seeder;
        this.employees = employees;
        this.randomSeed = randomSeed;
    }

    @Override
    public void run(ApplicationArguments args) {
        long started = System.currentTimeMillis();
        int inserted = seeder.seedIfEmpty(() -> new EmployeeGenerator(randomSeed).generate(employees));
        if (inserted == 0) {
            log.info("Seed skipped: employees already exist");
        } else {
            log.info("Seeded {} employees in {} ms", inserted, System.currentTimeMillis() - started);
        }
    }
}
