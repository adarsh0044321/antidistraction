# CLEAN CODE & MAINTAINABILITY PROTOCOL

The codebase must remain clean, intentional, readable, and maintainable even as the application becomes technically advanced.

The goal is:

> **Advanced engineering, simple-to-understand code.**

Complexity should exist only where the problem itself is complex.

---

## 1. READABILITY OVER CLEVERNESS

Write code that another experienced developer can understand quickly.

Prefer:
- clear names
- small functions
- explicit control flow
- single responsibility
- predictable structure

Avoid:
- clever one-liners
- unnecessary abstractions
- deep nesting
- cryptic names
- magic behavior
- implicit side effects

Do not write code merely to demonstrate technical sophistication.

---

## 2. SINGLE RESPONSIBILITY

A class/function should have one clear reason to change.

Avoid giant classes such as:
- GodActivity
- GodViewModel
- GodRepository
- GodManager
- GodService

If a class starts handling unrelated responsibilities, split it.

For example:
- `FocusSessionEngine`
- `RestrictionEngine`
- `AnalyticsEngine`

is preferable to `FocusManager` containing everything.

---

## 3. KEEP FUNCTIONS SMALL

Functions should perform one understandable operation.

Avoid functions that:
- Validate input
- Modify database state
- Update UI state
- Log diagnostics
- Trigger notifications
- Perform network operations

all at once.

Separate responsibilities into explicit operations.
A developer should be able to read a function and understand its purpose without mentally executing 200 lines of code.

---

## 4. NAMING

Names must explain intent.

Prefer:
- `calculateFocusScore()`
- `shouldRestrictApp()`
- `recordDistractionAttempt()`
- `restoreActiveSession()`

over:
- `calc()`
- `check()`
- `process()`
- `handle()`
- `doThing()`
- `manager()`
- `data()`
- `temp()`

Avoid abbreviations unless they are universally understood within the project.

---

## 5. NO MAGIC NUMBERS OR STRINGS

Do not scatter unexplained values throughout the code.

Bad:
```kotlin
if (score > 72)
delay(10000)
```

Prefer named domain constants/configuration:
```kotlin
HIGH_DISTRACTION_THRESHOLD
INTERVENTION_DELAY_MS
```

If a value has product meaning, document why it exists.

---

## 6. AVOID DEEP NESTING

Prefer early returns and clear branching.

Avoid deep nested `if` blocks. Keep control flow easy to follow. If logic becomes difficult to read, extract a well-named function.

---

## 7. NO DUPLICATED LOGIC

Do not copy/paste business logic.
If the same logic appears repeatedly:
1. Determine whether it genuinely represents the same concept.
2. Extract it into an appropriate domain utility/component.
3. Give that component a meaningful name.
4. Add tests.

Do not create abstractions solely because two pieces of code look superficially similar.

---

## 8. ARCHITECTURAL BOUNDARIES

Respect project layers.
A typical flow should remain conceptually clear:
```text
UI
 ↓
ViewModel / Presentation
 ↓
Use Case / Domain
 ↓
Repository
 ↓
Data Source
```

Do not allow UI code to directly manipulate databases when the architecture expects a repository/domain boundary. Do not put UI logic inside domain engines. Do not put business rules inside database classes. Do not put persistence logic inside Compose screens.

---

## 9. DEPENDENCY DIRECTION

Dependencies should point in intentional directions.
Avoid circular dependencies. Avoid allowing unrelated modules/packages to know about each other's internal implementation. Expose the smallest API necessary (`private`, `internal`).

---

## 10. COMMENTS

Comments should explain **WHY**, not **WHAT**.

Bad:
```kotlin
// Increment counter
counter++
```

Good:
```kotlin
// Keep the attempt count across process recreation because
// escalation decisions depend on cumulative attempts.
```

---

## 11. TODO DISCIPLINE

Do not leave random `TODO`, `FIXME`, `HACK`, `TEMP`, or `REMOVE LATER` throughout the repository. Every legitimate TODO should contain context on what is missing, why it is missing, and what should happen. Add real future work to `ROADMAP.md`. Remove obsolete TODOs.

---

## 12. NO DEAD CODE

Do not leave unused classes, unused functions, unused imports, commented-out code, or abandoned experiments. If code is no longer needed, remove it. Git is the history.

---

## 13. NO "MANAGER" GRAVEYARD

Avoid creating classes with vague names such as `AppManager`, `DataManager`, `Utils`, `Helper`, `Misc`, or `Processor` unless the responsibility is genuinely clear.

---

## 14. FILE ORGANIZATION

Keep related code together by domain/feature boundaries rather than arbitrary file categories. Avoid enormous directories containing dozens of unrelated classes.

---

## 15. ONE SOURCE OF TRUTH

Every important concept (focus session state, restriction rules, focus score, database schema) should have one authoritative implementation. Establish a clear source of truth.

---

## 16. STATE MUST BE EXPLICIT

Avoid hidden mutable global state. State transitions should be observable, predictable, testable, and recoverable.

---

## 17. ERROR HANDLING MUST BE CONSISTENT

Do not swallow failures. Distinguish between expected failures, recoverable failures, invalid inputs, system failures, and programming errors. Handle errors at the appropriate layer.

---

## 18. KOTLIN QUALITY

Use idiomatic Kotlin without abusing language features. Prefer readability over clever scope-function chains or deep sealed hierarchies.

---

## 19. COMPOSE QUALITY

Compose UI must remain modular. Extract meaningful components (`FocusTimer`, `SessionControls`, `RestrictionStatus`, `InterventionPrompt`). Keep business logic, database operations, and state machines out of UI functions.

---

## 20. TESTABLE DESIGN

Write code so important domain logic can be tested without requiring the entire Android framework.

---

## 21. NO PREMATURE ABSTRACTION

Introduce abstractions only when they provide a real benefit (multiple implementations, testability, dependency inversion, platform isolation).

---

## 22. NO PREMATURE OPTIMIZATION

First establish correctness, then measurement, then optimization. Document why an optimization exists.

---

## 23. SECURITY AND CLEAN CODE

Never sacrifice security for convenience. Security-sensitive code must be explicit, small, auditable, well-tested, and minimally privileged.

---

## 24. BEFORE ADDING A FILE

Ask: *"Does this genuinely need to be a new file?"* Avoid creating unnecessary files while keeping file sizes reasonable.

---

## 25. BEFORE MERGING A CHANGE

Review diffs with a critical eye:
* Can I understand this change quickly?
* Are names clear and responsibilities obvious?
* Are errors handled and tests present?
* Would I be comfortable maintaining this code a year from now?

---

# GOLDEN RULE

> **Complex problems are acceptable. Unnecessary complexity is not.**
