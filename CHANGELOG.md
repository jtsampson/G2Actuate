# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/)
and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).

## [Unreleased]
### Added 
 - Added documentation on Writing custom HealthIndicators
 - Added ``management.health.defaults.enabled`` to allow disabling all ``HealthIndicators``
 - Added (ISSUE-6) Bintray Badge with latest download link
 - Added TOC to readme with [doctoc](https://github.com/thlorenz/doctoc)
### Changed
 - Changed the /health endpoint, it now reports custom health indicators using bean names less the 'HealthIndicator' suffix (if it exists)
### Deprecated 
### Removed
### Fixed
 - Fixed ISSUE-2: Add management.health.defaults.enabled property
 - Fixed ISSUE-1: HealthIndicator names in /health
### Security

## [1.0.1] - 2017-11-16 (Initial release)
### Added
- CHANGELOG.md
- Caching for /health using health.timeToLive


## [Release Candidate]: 2017-11-15
### Added
- Added initial Santizer implementation (not used yet)
- Added configuration to publishing plugin to BinTray
- Added ``/beans`` endpoint and tests
- Added ``/env`` endpoint and tests
- Added ``/health`` endpoint and tests
- Added ``/heapdump`` endpoint and tests
- Added ``/info`` endpoint and tests
- Added ``/loggers`` endpoint and tests
- Added ``/mappings`` endpoint and tests
- Added ``/metrics`` endpoint and tests
- Added ``/shutdown`` endpoint and tests
- Added ``/trace`` endpoint and tests
- Added basic 'management.context-path' support Adding basic endpoint 'path' rename capabilities.
        
- Added Functional Testing with GEB
- Added sensitive/enabled tests.
- Added codenarc.
- Added scmInfo (experimental) and CLI tests

### Changed
- Updated Documentation
- Changed endpoint configuration 'path' to 'id' to match Actuator usage
