# Contributing to Anti-Distraction

Thank you for your interest in contributing to **Anti-Distraction**! We welcome bug fixes, documentation improvements, and performance optimizations that align with our core values of local-first privacy, clean architecture, and conscious attention management.

---

## Development Setup

### Requirements
- **JDK**: OpenJDK 17
- **Android Studio**: Android Studio Jellyfish (2023.3.1) or newer
- **Android SDK**: Target SDK 34, Min SDK 26

### Building & Testing
1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/anti-distraction.git
   cd anti-distraction
   ```
2. Run unit test compilation:
   ```bash
   ./gradlew compileDebugUnitTestSources
   ```
3. Build the debug APK:
   ```bash
   ./gradlew assembleDebug
   ```

---

## Code Standards & Guidelines

1. **Clean Architecture Layering**:
   - `UI` $\to$ `ViewModel` $\to$ `Domain Engine` $\to$ `Repository` $\to$ `Room DB`
   - Keep ViewModels free of Android framework context references.
   - Domain logic must remain testable without Android OS dependencies.

2. **Kotlin Style**:
   - Use explicit function and variable names explaining intent.
   - Avoid deep control flow nesting; use early guard clauses.
   - Avoid magic numbers; define named constants.

3. **Privacy & Security**:
   - **Zero Network Access**: Do not add network permissions (`android.permission.INTERNET`) or remote telemetry libraries.
   - Never commit private keys, signing keystores (`*.jks`), credentials, or tokens.

---

## Pull Request Guidelines

1. Create a descriptive feature branch:
   ```bash
   git checkout -b fix/accessibility-debounce-handling
   ```
2. Verify clean build and unit tests pass before submitting.
3. Ensure your PR description clearly details:
   - What problem is addressed
   - Technical approach taken
   - Test results & evidence
