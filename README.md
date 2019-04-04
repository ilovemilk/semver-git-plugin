# :ghost: __semver-git-plugin__
*Version your gradle projects with git tags and semantic versioning.*

[![Build Status](https://travis-ci.com/ilovemilk/semver-git-plugin.svg)](https://travis-ci.com/ilovemilk/semver-git-plugin)

### Usage

```kotlin
plugins {
    id("io.wusa.semver-git-plugin").version("[version]")
}

semver {
    nextVersion = "major", "minor" (default), "patch" or "none"
    snapshotSuffix = "SNAPSHOT" (default) or a pattern, e.g. "<count>.g<sha><dirty>-SNAPSHOT"
    dirtyMarker = "-dirty" (default) replaces <dirty> in snapshotSuffix
    gitDescribeArgs = ""--match *[0-9].[0-9]*.[0-9]*" (default) or other arguments for git describe.
}
```

Then you can access the version of your project via `semver.version`.

Examples:

```kotlin
allprojects {
    version = semver.version
}
```

```kotlin
project.version = semver.version
```

####

### License

The MIT License (MIT)


