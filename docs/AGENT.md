# Documentation Agent Guide

## Goal

Keep documentation in this repo accurate, short, and operational.

## Canonical Files

- `README.md`
  - product overview
  - feature summary
  - architecture diagram
  - setup steps
  - current app navigation and capabilities
- `AGENT.md`
  - contributor workflow
  - repo guardrails
  - validation expectations
- `docs/GOOGLE_SETUP.md`
  - local OAuth setup only

## Documentation Rules

- Reflect the current codebase, not aspirational milestones.
- Use exact local paths or package names when setup depends on them.
- If a feature is partial, say so clearly instead of implying it is complete.
- When auth, widget, planner, onboarding, or sync behavior changes, update the relevant docs in the same change.
- Prefer diagrams when explaining flow boundaries between onboarding, auth, sync, persistence, and widget refresh.

## Avoid

- Milestone transcript files
- Long code dumps copied into markdown
- Secret values or machine-specific credentials
- Stale screenshots unless they are refreshed intentionally
