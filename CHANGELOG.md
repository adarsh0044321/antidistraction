# Changelog

All notable changes to the **Anti-Distraction** project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [1.0.0] - 2026-08-27

### Added
- **Focus Session Engine**: Timestamp-derived persistent state machine supporting `DEEP_FOCUS`, `STUDY`, `WORK`, `LIGHT_FOCUS`, and `CUSTOM` profiles. Session state recovers across app process kills and device reboots.
- **Conscious Friction Intervention**: Real-time package interception via `FocusAccessibilityService` featuring a full-screen calm intervention UI with a 10-second countdown delay and intention selection.
- **Dynamic 6-Level Escalation**: Adaptive friction escalation (`LEVEL_0` to `LEVEL_6`) based on attempt frequency and resistance history.
- **2-Minute Temporary Exceptions**: Temporary exception flow granting 2 minutes of un-intercepted usage when requested, launching target app package with a Toast notification.
- **Emergency Overrides**: Emergency applications (Phone, Contacts, Maps, Mobile Banking) bypass restriction across all profiles without friction.
- **Distraction Brain Engine**: Per-app Distraction Score calculation ($S_{app} \in [0, 100]$) with time-of-day impulsivity weighting ($21:00 - 02:00$).
- **Notification Protection**: Suppresses non-priority notifications during focus sessions and logs digests locally for post-focus review.
- **Schedule Background Automation**: WorkManager periodic evaluation (`ScheduleCheckWorker`) and exact alarm triggers (`ScheduleAlarmReceiver`) for weekly routines.
- **Focus Analytics & Scoring**: Transparent 100-point focus score calculator and behavioral insights engine.
- **Security & Privacy**: 100% offline data architecture with zero `INTERNET` permission in manifest. Production release key signing (`release-key.jks`) with v1/v2/v3 signatures active.
