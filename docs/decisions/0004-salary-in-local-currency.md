# 0004 — Salaries stored and compared in local currency

**Status:** Accepted

## Context

Employees are spread across several countries, each paid in its own currency. Comparing salaries across countries requires converting them.

## Decision

- Store **annual gross salary** as `numeric(12,2)` with its **ISO 4217 currency**, derived from the employee's country.
- Compute and show statistics **per country, in that country's currency**. No cross-country conversion.

## Consequences

- ✅ Numbers are exact and never depend on a changing exchange rate.
- ✅ Within-country comparisons answer the most common HR questions (pay by role, pay ranges).
- ❌ No single org-wide average salary; headcount is the only org-wide figure.
- 🔁 Cross-country comparison would need an exchange-rate source and a policy for which rate applies. Listed as a future improvement.
