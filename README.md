# :ghost: __semver-git-plugin__
*Version your gradle projects with git tags and semantic versioning.*

[![Build Status](https://travis-ci.com/ilovemilk/semver-git-plugin.svg)](https://travis-ci.com/ilovemilk/semver-git-plugin)
[![codecov](https://codecov.io/gh/ilovemilk/semver-git-plugin/branch/master/graph/badge.svg)](https://codecov.io/gh/ilovemilk/semver-git-plugin)

## Apply the plugin

Gradle 2.1 and higher

```kotlin
plugins {
    id("io.wusa.semver-git-plugin").version("1.0.0")
}
```

Gradle 1.x and 2.0
```kotlin
buildscript {
   repositories {
      maven {
         url "https://plugins.gradle.org/m2/"
       }
   }
   dependencies {
      classpath 'io.wusa:semver-git-plugin:1.0.0'
   }
}

apply plugin: 'io.wusa.semver-git-plugin'
```

## Configure the plugin

```kotlin
semver {
    nextVersion = "major", "minor" (default), "patch" or "none"
    snapshotSuffix = "SNAPSHOT" (default) or a pattern, e.g. "<count>.g<sha><dirty>"
    dirtyMarker = "-dirty" (default) replaces <dirty> in snapshotSuffix
}
```

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

* If the last tag is the current tag then the current tag is the current version.
* If the last tag isn't the current tag the version is build based `nextVersion`, which bumps the version accordingly by one, and on the `snapshotSuffix`:
    * `<count>` corresponds to the number of commits after the last tag.
    * `<sha>` is the current short commit sha.
    * `<dirty>` is the customizable dirty flag. 

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
[semver] branch name: feature/test
[semver] branch group: feature
[semver] branch id: feature-test
[semver] commit: f4470859c24c031276e94eab81ed6bff5d7abd40
[semver] short commit: f447085
[semver] tag: 0.0.1
[semver] last tag: 0.0.1
[semver] dirty: false
```


### License

The MIT License (MIT)


