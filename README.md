# :ghost: __semver-git-plugin__
*Version your gradle projects with git tags and semantic versioning.*

[![GitHub version](https://img.shields.io/github/tag/ilovemilk/semver-git-plugin.svg)](https://img.shields.io/github/tag/ilovemilk/semver-git-plugin.svg)
[![License](https://img.shields.io/github/license/ilovemilk/semver-git-plugin.svg)](https://img.shields.io/github/license/ilovemilk/semver-git-plugin.svg)
[![Build Status](https://travis-ci.com/ilovemilk/semver-git-plugin.svg)](https://travis-ci.com/ilovemilk/semver-git-plugin)
[![codecov](https://codecov.io/gh/ilovemilk/semver-git-plugin/branch/master/graph/badge.svg)](https://codecov.io/gh/ilovemilk/semver-git-plugin)

## Apply the plugin

Gradle 2.1 and higher

```
plugins {
    id("io.wusa.semver-git-plugin").version("1.1.0")
}
```

Gradle 1.x and 2.0
```
buildscript {
   repositories {
      maven {
         url "https://plugins.gradle.org/m2/"
       }
   }
   dependencies {
      classpath 'io.wusa:semver-git-plugin:1.1.0'
   }
}

apply plugin: 'io.wusa.semver-git-plugin'
```

## Configure the plugin

```
semver {
    nextVersion = "major", "minor" (default), "patch" or "none"
    snapshotSuffix = "SNAPSHOT" (default) or a pattern, e.g. "<count>.g<sha><dirty>"
    dirtyMarker = "-dirty" (default) replaces <dirty> in snapshotSuffix
    initialVersion = "0.1.0" (default) initial version in semantic versioning
}
```

## Release

The versions have to be stored as annotated git tags in the format of [semantic versioning](https://semver.org/).

To create a new annotated release tag:

```bash
git tag -a 1.0.0-alpha.1 -m "new alpha release of version 1.0.0"
git push -- tags
```

Following commits without a release tag will have the `snapshotSuffix` (default `SNAPSHOT`) appended 
and the version number bumped according to `nextVersion` (default `minor`) strategy, e.g., `1.1.0-alpha.1-SNAPSHOT`.

## Version usage

Then you can access the version of your project via `semver.info.version`.

Examples:

```kotlin
allprojects {
    version = semver.info.version
}
```

```kotlin
project.version = semver.info.version
```

## Version information

Accessing the following information via `semver.info.*` e.g., `semver.info.version`.

| Property | Description | Git: master | Git: feature/ghosty |
|----------|-------------|-------------|---------------------|
| branch.group | Group of the branch | master | feature      |
| branch.name  | Name of the branch  | master | feature/ghosty |
| branch.id    | Tokenized branch name | master | feature-ghosty |
| commit       | Full sha1 commit hash | 4ecabe2e8646fd0b577dcda83e5c23447e230496 | 4ecabe2e8646fd0b577dcda83e5c23447e230496 |
| shortCommit  | Short sha1 commit has | d2459d | d2459d |
| tag          | Current tag | If any name of the tag else none | If any name of the tag else none |
| lastTag      | Last tag    | If any name of the tag else none | If any name of the tag else none |
| dirty        | Current state of the working copy | `true` if the working copy contains uncommitted files | `true` if the working copy contains uncommitted files |

## Display version

The `version` is based on the current or last tag.

* If the current commit has an annotated tag this tag will be the version.
* If the current commit has no annotated tag the version takes the last tag and builds the new version based on `nextVersion`, which bumps the version accordingly by one, and on the `snapshotSuffix`:
    * `<count>` corresponds to the number of commits after the last tag.
    * `<sha>` is the current short commit sha.
    * `<dirty>` is the customizable dirty flag.
* If no annotated tag exists the initial commit will be version 0.1.0 as recommended by [Semantic Versioning 2.0.0](https://semver.org/).
  The following commits will be build based on this version until a annotated tag is created.
## Tasks

The semver plugin offers two tasks.

### `showVersion`

Displays the version information:

```bash
> Task :showVersion
Version: 0.0.1
```

### `showInfo`

Displays the full info:

```bash
> Task :showInfo
Branch name: feature/test
Branch group: feature
Branch id: feature-test
Commit: ca836f6e43b679867293ec23f0ca382bd5027e86
Short commit: ca836f6
Tag: none
Last tag: 0.0.1
Dirty: false
```

## License

The MIT License (MIT)


