package io.wusa.incrementer

import io.wusa.Version

enum class VersionIncrementer: IIncrementer {
    NO_VERSION_INCREMENTER {
        override fun increment(version: Version): Version {
            return NoVersionIncrementer().increment(version)
        }
    },
    PATCH_INCREMENTER {
        override fun increment(version: Version): Version {
            return PatchVersionIncrementer().increment(version)
        }
    },
    MINOR_INCREMENTER {
        override fun increment(version: Version): Version {
            return MinorVersionIncrementer().increment(version)
        }
    },
    MAJOR_INCREMENTER {
        override fun increment(version: Version): Version {
            return MajorVersionIncrementer().increment(version)
        }
    };

    companion object {
        fun getVersionIncrementerByName(name: String) = valueOf(name.toUpperCase())
    }
}