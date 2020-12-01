 Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [2.3.6]
### Fixed
- Fix BREAKING CHANGE regex to also accept BREAKING-CHANGE and BREAKING_CHANGE due to an incompatibility between the Conventional commit specification and the git trailers convention:
git trailers only detect keywords which do not contain space character (e.g. BREAKING-CHANGE is detected but not BREAKING CHANGE) whereas Conventional commit specification specified BREAKING CHANGE with a space.

## [2.3.5]
### Fixed
- Fix large process outputs could lead to a timeout in the command runner due to the reading buffer running full and blocking the process.

## [2.3.4]
### Fixed
- Remove debug statements.

## [2.3.3]
### Fixed
- Fix dirty working tree in the conventional commits incrementer.

## [2.3.2]
### Fixed
- Fix conventional commits incrementer for BREAKING CHANGES marked by a ! after the scope.
- Improve conventional commits incrementer regex parsing.
- Remove `git branch --show-current` to resolve detached HEAD problems and to improve Git < 2.22.0 compatibility.

## [2.3.1]
### Fixed
- Fix conventional commits incrementer for windows and annotated and lightweight tags.

## [2.3.0]
### Added 
- Option to use lightweight or annotated tags.

## [2.2.4]
### Fixed
- Fix typo in retrieving all commits since last tags which lead to problems retrieving commits.
- Fix getting current branch if the current branch is not the first one.

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
