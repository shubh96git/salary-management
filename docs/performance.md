# Performance Notes

Measurements and decisions about speed, recorded as they were made. Numbers are from a Windows development laptop with a local MySQL 8.0 server.

## Seeding 10,000 employees

**Approach:** generate in memory (pure Java, under 0.5 s), then insert with JDBC batches of 1,000 rows in one transaction. JPA is bypassed so 10,000 entities never sit in the persistence context. The MySQL driver's `rewriteBatchedStatements` option is enabled so batches can be sent as multi-row inserts.

**Measured:** about 8–10 s end to end on the development machine.

**Investigation:** this looked slow for 10 batches, so the time was measured rather than guessed:

| Experiment | Time |
|---|---|
| Seeder (JDBC batches, `rewriteBatchedStatements` as driver property) | 7.6 s |
| Seeder (`rewriteBatchedStatements` in the JDBC URL instead) | 12.1 s |
| **One raw SQL statement inserting 10,000 rows, no Java involved** | **7.3 s** |
| `SELECT 1` round trip | 0.09 s |

**Conclusion:** the seeder already runs at the speed of the database itself. The bottleneck is the local MySQL server's write path (binary logging with `sync_binlog=1`, durable log flushing, disk speed), not the application. No application change would make a meaningful difference, so none was made.

**If seeding ever mattered more** (much larger data or frequent reseeding): `LOAD DATA INFILE`, temporarily dropping secondary indexes during the load, or relaxing durability settings on a non-production database.

## Reading data

- **Employee list:** paginated in the database (default 20 rows, maximum 100), so response size stays constant regardless of headcount.
- **Search:** `LIKE '%text%'` on name and email cannot use an index, so it scans the table. At 10,000 rows this is a few milliseconds; at millions of rows a full-text index would be the next step.
- **Filters:** country, job title and department filters use the composite indexes `(country_code, job_title)` and `(country_code, department)`.

## Insights

All statistics are computed in one SQL query per screen: `GROUP BY` for minimum, maximum, average and total, and `ROW_NUMBER()` / `COUNT()` window functions for the median. The application never loads employees to aggregate them.

Measured over the 10,000 seeded employees (warm application, local MySQL):

| Request | Time |
|---|---|
| `/api/insights/countries` (all countries, including median) | 69 ms |
| `/api/insights/countries/IN/job-titles` | 53 ms |
| `/api/insights/countries/US/departments` | 43 ms |
| `/api/employees?search=rao&country=IN&sort=annualSalary,desc` | 149 ms |
| `/api/employees/filter-options` | 22 ms |

All are well within the 500 ms target. At 10,000 rows no caching is needed; if headcount grew by orders of magnitude, insight results could be cached or precomputed, because salary data changes rarely compared with how often it is read.
