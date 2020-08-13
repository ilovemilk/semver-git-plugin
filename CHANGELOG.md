 Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [2.2.4]
### Fixed
- Getting the current branch name would not always resolve properly but not does
### Added 
- Option to use lightweight or annotated tags 

## [2.2.3]
### Fixed
- Remove trailing dash if dirty marker is empty.
- Branch parsing regex now supports / (slashes) (e.g. feature/ilovemilk/super-feature)

## [2.2.2]
### Fixed
- Remove trailing dash if SNAPSHOT is empty.
- Fix documentation.

## [2.2.1]
### Fixed
- Branch parsing regex now supports . (dots) (e.g. hotfix/5.3.1)

## [2.2.0]
### Added
- Support [conventional commits](https://www.conventionalcommits.org/en/v1.0.0/) by a special incrementer.

## [2.1.0]
### Added
- Support multi module with custom tag prefix.

## [2.0.2]
### Fixed
- Branch parsing regex now supports numbers [0-9]

## [2.0.1]
### Fixed
- Branch parsing regex now supports camelCase

## [2.0.0]
### Added
- Custom branch specific formatter

## [1.2.1] - 2019-09-13
### Fixes
- Current branch returning the branch name instead of HEAD on a detached HEAD

## [1.2.0] - 2019-05-11
### Added
- Detailed version info to info task

### Fixed
- Regex for parsing version string

## [1.1.0] - 2019-05-07
### Added
- Custom initial version.
