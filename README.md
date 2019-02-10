# __semver-git-plugin__

[![Build Status](https://travis-ci.com/ilovemilk/semver-git-plugin.svg)](https://travis-ci.com/ilovemilk/semver-git-plugin)

### Usage

```kotlin
plugins {
    id("io.wusa.semver-git-plugin").version("[version]")
}

semver {
    nextVersion = "major", "minor" (default), "patch" or e.g. "3.0.0-rc2"
    snapshotSuffix = "SNAPSHOT" (default) or a pattern, e.g. "<count>.g<sha><dirty>-SNAPSHOT"
    dirtyMarker = "-dirty" (default) replaces <dirty> in snapshotSuffix
    gitDescribeArgs = ""--match *[0-9].[0-9]*.[0-9]*" (default) or other arguments for git describe.
}
```

