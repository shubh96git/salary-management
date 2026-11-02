# Requirements — Salary Management

_Written before any code. Last updated: 2026-11-02._

## Goal

ACME's HR team manages salary data for **10,000 employees across multiple countries** in Excel. This is tedious, error-prone and makes it hard to answer basic questions about pay. We are building a web application that lets the **HR Manager** (single persona) maintain salary records and answer **"how does the org pay people?"** without spreadsheets.

**Success looks like:** the HR Manager can find any employee in seconds, update their salary with validation, and see pay statistics per country and per job title on one screen.

## Users

| Persona | Needs |
|---|---|
| HR Manager | Maintain accurate salary records; answer pay questions from leadership quickly; trust the numbers. |

## In scope

### 1. Manage employees
- **List** employees with server-side pagination and sorting (10,000 rows never go to the browser at once).
- **Search** by name and **filter** by country, job title and department.
- **View, add, edit, delete** an employee.
- **Validate** input: required fields, salary must be positive, known country, valid email, hire date not in the future. Clear error messages in the UI.

**Employee fields:** full name, email (unique), job title, department, country, annual gross salary, currency (derived from country), hire date.

### 2. Salary insights
- **Org overview:** total headcount, number of countries, headcount per country.
- **By country:** headcount, minimum, maximum, average and median salary, total payroll — in that country's local currency.
- **By job title within a country:** headcount, minimum, maximum and average salary.
- **By department within a country:** headcount and average salary.

### 3. Data and delivery
- Seed script that creates **10,000 realistic, deterministic** employees (same data on every run).
- Deployed, publicly reachable application, plus a short video demo.

## Deliberately out of scope

| Left out | Why |
|---|---|
| Authentication and roles | Single persona, synthetic data. Adds setup cost without showing anything new about the problem. **First item for a real rollout.** |
| Currency conversion / cross-country comparison in one currency | Needs an exchange-rate source and a policy (which rate, which date). Comparing within a country is accurate and answers most pay questions. |
| Payroll processing, tax, deductions, payslips | Different product; the brief is about managing and understanding salary data. |
| Salary history and audit trail | Valuable, but doubles the data model. Records store current salary only. |
| Excel/CSV bulk import and export | Real migration need, but the seed script covers the demo. Strong next candidate. |
| Approval workflows, employee self-service, notifications | More personas than the brief describes. |
| Internationalisation, accessibility audit beyond component-library defaults | Time-boxed exercise; Angular Material gives a reasonable baseline. |

## Non-functional requirements

- **Performance:** list and insight requests respond in < 500 ms at 10,000 employees. Aggregations run in the database, not in application memory.
- **Correctness:** salaries stored as exact decimals, never floating point.
- **Quality:** core behaviour covered by fast, deterministic unit tests, built test-first.
- **Simplicity:** one deployable application and one database.

## Assumptions

Where the brief is open, these are the choices made and why.

1. **Modern Angular**, read from "AngularJS for Java": AngularJS 1.x reached end of life in 2021.
2. **Salaries compared within a country, in local currency:** exact numbers with no exchange-rate policy needed ([ADR 0004](decisions/0004-salary-in-local-currency.md)).
3. **No authentication:** one trusted persona and synthetic data.
4. **The pay questions are ours to choose:** picked from what an HR Manager is most often asked (pay ranges by country, by role and by department).
5. **A free hosting tier counts as "deployed":** cold starts are acceptable for a demo.
6. **One currency per country; salary is annual gross.**
