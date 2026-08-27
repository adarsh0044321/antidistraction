# Security Policy

## Security Model

**Anti-Distraction** is designed as a local-first, privacy-focused application.
- **Zero Network Permission**: `android.permission.INTERNET` is not requested. The application cannot transmit data externally.
- **Local Persistence**: All database entities (focus sessions, attempt logs, notification digests) are stored strictly on-device in Room DB.

---

## Supported Versions

Only the latest release version receives security updates.

| Version | Supported          |
| ------- | ------------------ |
| 1.0.x   | :white_check_mark: |

---

## Reporting a Vulnerability

If you discover a security vulnerability or potential privilege escalation issue, please report it responsibly:

1. **Do NOT open a public GitHub issue.**
2. Send a private report detailing:
   - Component / File affected
   - Reproduction steps
   - Potential impact
3. We will acknowledge receipt within 48 hours and work on a fix promptly.
