# Metrics Feature

The Metrics feature powers goal tracking and generic data tracking (e.g., workouts, protein, mood, sleep).
It is designed around a simple tiered system where Objectives define a timeframe and Milestones define thresholds.

## Data Model
- Metric: the thing being tracked (name, type, unit, current resolution).
- Entry: a recorded value for a specific time bucket (periodStart/periodEnd).
- Objective: a goal for a metric within an evaluation window.
- Milestone: tier thresholds within an Objective (Platinum/Gold/Silver/Bronze).

## Notes
- Boolean vs. Int entries: store either `valueBool` or `valueInt`, never both.
- Money and decimals should use scaled integers (e.g., cents, deci-kg).
- Objectives can aggregate by TOTAL, AVERAGE, or LATEST.
