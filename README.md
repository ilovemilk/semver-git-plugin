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
    id("io.wusa.semver-git-plugin").version("3.0.0")
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
      classpath 'io.wusa:semver-git-plugin:3.0.0'
   }
}

apply plugin: 'io.wusa.semver-git-plugin'
```

## Configure the plugin

`build.gradle.kts`
```kotlin
semver {
    snapshotSuffix = "SNAPSHOT" // (default) appended if the commit is without a release tag
    dirtyMarker = "dirty" // (default) appended if the are uncommitted changes
    initialVersion = "0.1.0" // (default) initial version in semantic versioning
    tagPrefix = "" // (default) each project can have its own tags identified by a unique prefix.
    tagtype = TagType.Annotated // (default) options are Annotated or Lightweight
    branches { // list of branch configurations
        branch {
            snapshotSuffix = "SNAPSHOT" // (default) branch specific snapshot suffix
            regex = ".+" // regex for the branch you want to configure, put this one last
            incrementer = MinorVersionIncrementer // (default) version incrementer
            formatter = { "${semver.info.version.major}.${semver.info.version.minor}.${semver.info.version.patch}+build.${semver.info.count}.sha.${semver.info.shortCommit}" } // (default) version formatting closure
        }
    }
}
```

`build.gradle`
```groovy
semver {
    snapshotSuffix = "SNAPSHOT" // (default) appended if the commit is without a release tag
    dirtyMarker = "dirty" // (default) appended if the are uncommitted changes
    initialVersion = "0.1.0" // (default) initial version in semantic versioning
    tagPrefix = "" // (default) each project can have its own tags identified by a unique prefix.
    tagtype = TagType.Annotated // (default) options are Annotated or Lightweight
    branches { // list of branch configurations
        branch {
            snapshotSuffix = "SNAPSHOT" // (default) branch specific snapshot suffix
            regex = ".+" // regex for the branch you want to configure, put this one last
            incrementer = GroovyMinorVersionIncrementer as Transformer // (default) version incrementer
            formatter = { "${semver.info.version.major}.${semver.info.version.minor}.${semver.info.version.patch}+build.${semver.info.count}.sha.${semver.info.shortCommit}" } // (default) version formatting closure
        }
    }
}
```

### Use with a Gradle multi-module project
For projects which take advantage of Gradle's multi-module projects, it is possible to specify different tags 
for each module.

The tags for each module must be distinguished with an unambiguous prefix. For example, if you have three modules
foo, bar, and blega, you may consider using the module name as the prefix. This prefix is configured in the `semver`
configuration block:
```kotlin
semver {
  tagPrefix = "foo-"
}
```  

Given the above configuration, the tags for the "foo" module must all begin with "foo-"; e.g., "foo-3.2.6",
etc... Note that besides the prefix, the rules for the tag names must still follow all the same semver rules as
apply in any other case.

## Incrementer

| Groovy Incrementer | Kotlin Incrementer | Description | 
|----------|----------|-------------|
| GroovyNoVersionIncrementer | NoVersionIncrementer  | Doesn't increment the version at all. |
| GroovyPatchVersionIncrementer | PatchVersionIncrementer | Increments the patch version by one. |
| GroovyMinorVersionIncrementer | MinorVersionIncrementer | Increments the minor version by one. |
| GroovyMajorVersionIncrementer | MajorVersionIncrementer | Increments the major version by one. |
| GroovyConventionalCommitsVersionIncrementer | ConventionalCommitsVersionIncrementer | Increments the version according to [conventional commits](https://www.conventionalcommits.org/en/v1.0.0/). |

## Custom Incrementer

It's possible to implement your own incrementer by implementing the Gradle [Transformer interface](https://docs.gradle.org/current/javadoc/org/gradle/api/Transformer.html) with Version in and Version out.

`build.gradle.kts`
```kotlin
incrementer = 
    object : Transformer<Version, Version> {
        override fun transform(version: Version): Version {
            version.major += 1
            version.minor += 1
            version.patch += 1
            return version
        }
    }
```

`build.gradle`
```groovy
incrementer = { 
                it.major = it.major + 1
                it.minor = it.minor + 1
                it.patch = it.patch + 1
                it 
}
```

## Release

The versions have to be stored as annotated or lightweight (depending on the configured tag type) git tags in the format of [semantic versioning](https://semver.org/).

To create a new annotated release tag:

```bash
git tag -a 1.0.0-alpha.1 -m "new alpha release of version 1.0.0"
git push --tags
```

To create a new lightweight release tag:
```bash
git tag -a 1.0.0-alpha.1
git push --tags
```

Following commits without a release tag will have the global or branch specific `snapshotSuffix` (default `SNAPSHOT`) appended 
and the version number bumped according to `incrementer` (default `minor`) strategy, e.g., `1.1.0-alpha.1-SNAPSHOT`.

## Version usage

Then you can access the version of your project via `semver.info`.

Examples:

```kotlin
allprojects {
    version = semver.info
}
```

```kotlin
project.version = semver.info
```

## Version information

`semver.info`

Access the following information via `semver.info.*` e.g., `semver.info.tag`.

| Property | Description | Git: master | Git: feature/ghosty |
|----------|-------------|-------------|---------------------|
| info.branch.group | Group of the branch | master | feature      |
| info.branch.name  | Name of the branch  | master | feature/ghosty |
| info.branch.id    | Tokenized branch name | master | feature-ghosty |
| info.commit       | Full sha1 commit hash | 4ecabe2e8646fd0b577dcda83e5c23447e230496 | 4ecabe2e8646fd0b577dcda83e5c23447e230496 |
| info.shortCommit  | Short sha1 commit has | d2459d | d2459d |
| info.tag          | Current tag | If any name of the tag else none | If any name of the tag else none |
| info.lastTag      | Last tag    | If any name of the tag else none | If any name of the tag else none |
| info.dirty        | Current state of the working copy | `true` if the working copy contains uncommitted files | `true` if the working copy contains uncommitted files |
| info              | Formatted version | 0.1.0 | 0.1.0 |
| info.version.major | Major version of 2.0.0-rc.1+build.123 | 2 | 2 |
| info.version.minor | Minor version of 2.0.0-rc.1+build.123 | 0 | 0 |
| info.version.patch | Patch version of 2.0.0-rc.1+build.123 | 0 | 0 |
| info.version.build | Build number of 2.0.0-rc.1+build.123 | build.123 | build.123 |
| info.version.prerelease | Pre release of 2.0.0-rc.1+build.123 | rc.1 | rc.1 |

## Display version

The `semver.info` is based on the current or last tag (depending on the specified tag type).

* If the current commit has an tag this tag will be the version.
* If the current commit has no tag the version takes the last tag and builds the new version based on:
    * The ordering of the branch configuration is important for the matching.
* If no tag exists the initial commit will be version 0.1.0 as recommended by [Semantic Versioning 2.0.0](https://semver.org/).
  The following commits will be build based on this version until a tag is created.

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
Commit: fd132d87d51cee610c5b273050625850e1d62a3b
Short commit: fd132d8
Tag: none
Last tag: 0.0.1
Dirty: false
Version: 0.1.0-SNAPSHOT
Version major: 0
Version minor: 1
Version patch: 0
Version pre release: none
Version build: none
```

### `createVersionProperties`

Creates a version properties file in the build directory with all version information.

```properties
branch.group=feature
branch.id=feature-test
branch.name=test
commit=84da291157d470d82d8c69fd9c292a7a577ba532
dirty=false
last.tag=0.1.0
short.commit=84da291
tag=0.1.0
version.build=none
version.major=0
version.minor=1
version.patch=0
version.prerelease=none
version=0.1.0
```

## License

The MIT License (MIT)


