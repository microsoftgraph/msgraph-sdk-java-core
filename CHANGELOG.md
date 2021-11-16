# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added

- Added support for cancelling requests #361

### Changed

- Bumps Azure Core from 1.20.0 to 1.22.0 #359, #360, #341, #342
- Bumps gson from 2.8.8 to 2.8.9 #356, #355
- Bumps actions/checkout from 2.3.5 to 2.4.0 #358, #349
- Upgrades CI pipeline to Java 17 #348, #330
- Bumps mockito-inline from 3.12.4 to 4.0.0 #346, #345
- Bumps action-download-artifact from 2.14.1 to 2.15.0 #344
- Bumps okhttp from 4.9.1 to 4.9.2 #339, #340
- Bumps guava from 30.1.1 to 31.0.1 #338, #337, #335, #336
- Bumps junit-jupiter-api from 5.8.0 to 5.8.1 #332, #334
- Bumps junit-jupiter-egine from 5.8.0 to 5.8.1 #333

## [2.0.9] - 2021-09-17

### Added

- Workflow for Maven preview and GitHub Release
- Workflow for build validation
- Proper handling for InterruptedException

### Changed

- Casing fixed for Odata type parsing
- Removed .azure-pipelines/**

