package io.wusa.incrementer

import io.wusa.Info
import io.wusa.Version
import org.gradle.api.Transformer

enum class VersionIncrementer : Transformer<Version, Info> {
    NO_VERSION_INCREMENTER {
        override fun transform(info: Info): Version {
            return NoVersionIncrementer().transform(info)
        }
    },
    PATCH_INCREMENTER {
        override fun transform(info: Info): Version {
            return PatchVersionIncrementer().transform(info)
        }
    },
    MINOR_INCREMENTER {
        override fun transform(info: Info): Version {
            return MinorVersionIncrementer().transform(info)
        }
    },
    MAJOR_INCREMENTER {
        override fun transform(info: Info): Version {
            return MajorVersionIncrementer().transform(info)
        }
    },
    CONVENTIONAL_COMMITS_INCREMENTER {
        override fun transform(info: Info): Version {
            return ConventionalCommitsIncrementer().transform(info)
        }
    };
}
