package io.wusa.incrementer

import io.wusa.Version
import io.wusa.exception.NoIncrementerFoundException
import org.gradle.api.Project
import java.lang.IllegalArgumentException

enum class VersionIncrementer: IIncrementer {
    NO_VERSION_INCREMENTER {
        override fun increment(version: Version, project: Project): Version {
            return NoVersionIncrementer().increment(version, project)
        }
    },
    PATCH_INCREMENTER {
        override fun increment(version: Version, project: Project): Version {
            return PatchVersionIncrementer().increment(version, project)
        }
    },
    MINOR_INCREMENTER {
        override fun increment(version: Version, project: Project): Version {
            return MinorVersionIncrementer().increment(version, project)
        }
    },
    MAJOR_INCREMENTER {
        override fun increment(version: Version, project: Project): Version {
            return MajorVersionIncrementer().increment(version, project)
        }
    },
    CONVENTIONAL_COMMITS_INCREMENTER {
        override fun increment(version: Version, project: Project): Version {
            return ConventionalCommitsIncrementer().increment(version, project)
        }
    };

    companion object {
        fun getVersionIncrementerByName(name: String): VersionIncrementer {
            try {
                return valueOf(name.toUpperCase())
            } catch (ex: IllegalArgumentException) {
                throw NoIncrementerFoundException("The in the config specified incrementer was not found.")
            }
        }
    }
}
