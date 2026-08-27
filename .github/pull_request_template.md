## Description
Provide a concise summary of the changes introduced by this Pull Request.

## Related Issue
Fixes #(issue number)

## Type of Change
- [ ] Bug fix (non-breaking change fixing an issue)
- [ ] New feature (non-breaking change adding functionality)
- [ ] Code cleanliness / Refactoring
- [ ] Documentation update

## Testing Performed
- [ ] Unit tests pass (`./gradlew test`)
- [ ] Debug build succeeds (`./gradlew assembleDebug`)
- [ ] Verified on real physical Android device

## Security & Architecture Checklist
- [ ] **No Network Permissions Added**: Verified zero `INTERNET` permission in manifest.
- [ ] **No Secrets**: Confirmed zero API keys, tokens, or private signing keys committed.
- [ ] **Clean Architecture**: Preserved `UI` -> `ViewModel` -> `Domain` -> `Repository` -> `Room` layering.
- [ ] **Clean Code**: Followed small functions, explicit names, and early guard clauses.
