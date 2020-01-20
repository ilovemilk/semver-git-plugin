package io.wusa

import io.mockk.mockkObject
import io.wusa.exception.NoValidSemverTagFoundException
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SemanticVersionFactoryTest {

    private lateinit var project: Project

    @BeforeEach
    internal fun setUp() {
        project = ProjectBuilder.builder().build()
        mockkObject(GitService)
    }
    
    @Test
    fun `parse version`() {
        val semanticVersionFactory: IVersionFactory = SemanticVersionFactory()
        Assertions.assertEquals(semanticVersionFactory.createFromString("1.0.0-0-g123-dirty"), Version(1, 0, 0, "", "", Suffix(0, "123", true)))
        Assertions.assertEquals(semanticVersionFactory.createFromString("0.0.1-1-gfe17e7f"), Version(0, 0, 1, "", "", Suffix(1, "fe17e7f", false)))
        Assertions.assertEquals(semanticVersionFactory.createFromString("1.0.0-2-g123-dirty"), Version(1, 0, 0, "", "", Suffix(2, "123", true)))
        Assertions.assertEquals(semanticVersionFactory.createFromString("1.0.0-0"), Version(1, 0, 0, "0", "", null))
        Assertions.assertEquals(semanticVersionFactory.createFromString("0.0.4"), Version(0, 0, 4, "", "", null))
        Assertions.assertEquals(semanticVersionFactory.createFromString("1.2.3"), Version(1, 2, 3, "", "", null))
        Assertions.assertEquals(semanticVersionFactory.createFromString("10.20.30"), Version(10, 20, 30, "", "", null))
        Assertions.assertEquals(semanticVersionFactory.createFromString("1.1.2-prerelease+meta"), Version(1, 1, 2, "prerelease", "meta", null))
        Assertions.assertEquals(semanticVersionFactory.createFromString("1.1.2+meta"), Version(1, 1, 2, "", "meta", null))
        Assertions.assertEquals(semanticVersionFactory.createFromString("1.1.2+meta-valid"), Version(1, 1, 2, "", "meta-valid", null))
        Assertions.assertEquals(semanticVersionFactory.createFromString("1.0.0-alpha"), Version(1, 0, 0, "alpha", "", null))
        Assertions.assertEquals(semanticVersionFactory.createFromString("1.0.0-beta"), Version(1, 0, 0, "beta", "", null))
        Assertions.assertEquals(semanticVersionFactory.createFromString("1.0.0-alpha.beta"), Version(1, 0, 0, "alpha.beta", "", null))
        Assertions.assertEquals(semanticVersionFactory.createFromString("1.0.0-alpha.beta.1"), Version(1, 0, 0, "alpha.beta.1", "", null))
        Assertions.assertEquals(semanticVersionFactory.createFromString("1.0.0-alpha.1"), Version(1, 0, 0, "alpha.1", "", null))
        Assertions.assertEquals(semanticVersionFactory.createFromString("1.0.0-alpha0.valid"), Version(1, 0, 0, "alpha0.valid", "", null))
        Assertions.assertEquals(semanticVersionFactory.createFromString("1.0.0-alpha.0valid"), Version(1, 0, 0, "alpha.0valid", "", null))
        Assertions.assertEquals(semanticVersionFactory.createFromString("1.0.0-alpha-a.b-c-somethinglong+build.1-aef.1-its-okay"), Version(1, 0, 0, "alpha-a.b-c-somethinglong", "build.1-aef.1-its-okay", null))
        Assertions.assertEquals(semanticVersionFactory.createFromString("1.0.0-rc.1+build.1"), Version(1, 0, 0, "rc.1", "build.1", null))
        Assertions.assertEquals(semanticVersionFactory.createFromString("2.0.0-rc.1+build.123"), Version(2, 0, 0, "rc.1", "build.123", null))
        Assertions.assertEquals(semanticVersionFactory.createFromString("1.2.3-beta"), Version(1, 2, 3, "beta", "", null))
        Assertions.assertEquals(semanticVersionFactory.createFromString("10.2.3-DEV-SNAPSHOT"), Version(10, 2, 3, "DEV-SNAPSHOT", "", null))
        Assertions.assertEquals(semanticVersionFactory.createFromString("1.2.3-SNAPSHOT-123"), Version(1, 2, 3, "SNAPSHOT-123", "", null))
        Assertions.assertEquals(semanticVersionFactory.createFromString("1.0.0"), Version(1, 0, 0, "", "", null))
        Assertions.assertEquals(semanticVersionFactory.createFromString("2.0.0"), Version(2, 0, 0, "", "", null))
        Assertions.assertEquals(semanticVersionFactory.createFromString("1.1.7"), Version(1, 1, 7, "", "", null))
        Assertions.assertEquals(semanticVersionFactory.createFromString("2.0.0+build.1848"), Version(2, 0, 0, "", "build.1848", null))
        Assertions.assertEquals(semanticVersionFactory.createFromString("2.0.1-alpha.1227"), Version(2, 0, 1, "alpha.1227", "", null))
        Assertions.assertEquals(semanticVersionFactory.createFromString("1.0.0-alpha+beta"), Version(1, 0, 0, "alpha", "beta", null))
        Assertions.assertEquals(semanticVersionFactory.createFromString("1.2.3----RC-SNAPSHOT.12.9.1--.12+788"), Version(1, 2, 3, "---RC-SNAPSHOT.12.9.1--.12", "788", null))
        Assertions.assertEquals(semanticVersionFactory.createFromString("1.2.3----R-S.12.9.1--.12+meta"), Version(1, 2, 3, "---R-S.12.9.1--.12", "meta", null))
        Assertions.assertEquals(semanticVersionFactory.createFromString("1.2.3----RC-SNAPSHOT.12.9.1--.12"), Version(1, 2, 3, "---RC-SNAPSHOT.12.9.1--.12", "", null))
        Assertions.assertEquals(semanticVersionFactory.createFromString("1.0.0+0.build.1-rc.10000aaa-kk-0.1"), Version(1, 0, 0, "", "0.build.1-rc.10000aaa-kk-0.1", null))
        Assertions.assertEquals(semanticVersionFactory.createFromString("9999999.9999999.9999999"), Version(9999999, 9999999, 9999999, "", "", null))
        Assertions.assertEquals(semanticVersionFactory.createFromString("1.0.0-0A.is.legal"), Version(1, 0, 0, "0A.is.legal", "", null))
        Assertions.assertEquals(semanticVersionFactory.createFromString("1.1.1"), Version(1, 1, 1, "", "", null))
        Assertions.assertEquals(semanticVersionFactory.createFromString("1.0.0-5-g5242341-dirty"), Version(1, 0, 0, "", "", Suffix(5, "5242341", true)))
        Assertions.assertEquals(semanticVersionFactory.createFromString("1.0.0-5-g5242341"), Version(1, 0, 0, "", "", Suffix(5, "5242341", false)))
        Assertions.assertEquals(semanticVersionFactory.createFromString("5000.1.1000000-5-g5242341-dirty"), Version(5000, 1, 1000000, "", "", Suffix(5, "5242341", true)))
        Assertions.assertEquals(semanticVersionFactory.createFromString("v1.0.0"), Version(1, 0, 0, "", "", null))
        Assertions.assertEquals(semanticVersionFactory.createFromString("V1.0.0"), Version(1, 0, 0, "", "", null))
        Assertions.assertEquals(semanticVersionFactory.createFromString("1.0.0--dirty-5-g5242341"), Version(1, 0, 0, "-dirty", "", Suffix(5, "5242341", false)))
        Assertions.assertEquals(semanticVersionFactory.createFromString("1.0.0-dirty--5-g5242341"), Version(1, 0, 0, "dirty-", "", Suffix(5, "5242341", false)))
        Assertions.assertEquals(semanticVersionFactory.createFromString("1.0.0-dirty-5--g5242341"), Version(1, 0, 0, "dirty-5--g5242341", "", null))
        Assertions.assertEquals(semanticVersionFactory.createFromString("1.0.0--g5242341"), Version(1, 0, 0, "-g5242341", "", null))
        Assertions.assertEquals(semanticVersionFactory.createFromString("1.0.0-g"), Version(1, 0, 0, "g", "", null))
        Assertions.assertEquals(semanticVersionFactory.createFromString("1.0.0--g"), Version(1, 0, 0, "-g", "", null))
        Assertions.assertEquals(semanticVersionFactory.createFromString("1.0.0-g123-5"), Version(1, 0, 0, "g123-5", "", null))
        Assertions.assertEquals(semanticVersionFactory.createFromString("1.0.0-5-dirty"), Version(1, 0, 0, "5-dirty", "", null))
        Assertions.assertEquals(semanticVersionFactory.createFromString("1.0.0-5"), Version(1, 0, 0, "5", "", null))
        Assertions.assertEquals(semanticVersionFactory.createFromString("1.0.0-5-g"), Version(1, 0, 0, "5-g", "", null))

        Assertions.assertThrows(NoValidSemverTagFoundException::class.java) {
            semanticVersionFactory.createFromString("1")
        }
        Assertions.assertThrows(NoValidSemverTagFoundException::class.java) {
            semanticVersionFactory.createFromString("1.2")
        }
        Assertions.assertThrows(NoValidSemverTagFoundException::class.java) {
            semanticVersionFactory.createFromString("1.2.3-0123")
        }
        Assertions.assertThrows(NoValidSemverTagFoundException::class.java) {
            semanticVersionFactory.createFromString("1.2.3-0123.0123")
        }
        Assertions.assertThrows(NoValidSemverTagFoundException::class.java) {
            semanticVersionFactory.createFromString("1.1.2+.123")
        }
        Assertions.assertThrows(NoValidSemverTagFoundException::class.java) {
            semanticVersionFactory.createFromString("+invalid")
        }
        Assertions.assertThrows(NoValidSemverTagFoundException::class.java) {
            semanticVersionFactory.createFromString("-invalid")
        }
        Assertions.assertThrows(NoValidSemverTagFoundException::class.java) {
            semanticVersionFactory.createFromString("-invalid+invalid")
        }
        Assertions.assertThrows(NoValidSemverTagFoundException::class.java) {
            semanticVersionFactory.createFromString("-invalid.01")
        }
        Assertions.assertThrows(NoValidSemverTagFoundException::class.java) {
            semanticVersionFactory.createFromString("alpha")
        }
        Assertions.assertThrows(NoValidSemverTagFoundException::class.java) {
            semanticVersionFactory.createFromString("alpha.beta")
        }
        Assertions.assertThrows(NoValidSemverTagFoundException::class.java) {
            semanticVersionFactory.createFromString("alpha.beta.1")
        }
        Assertions.assertThrows(NoValidSemverTagFoundException::class.java) {
            semanticVersionFactory.createFromString("alpha.1")
        }
        Assertions.assertThrows(NoValidSemverTagFoundException::class.java) {
            semanticVersionFactory.createFromString("alpha+beta")
        }
        Assertions.assertThrows(NoValidSemverTagFoundException::class.java) {
            semanticVersionFactory.createFromString("alpha_beta")
        }
        Assertions.assertThrows(NoValidSemverTagFoundException::class.java) {
            semanticVersionFactory.createFromString("alpha..")
        }
        Assertions.assertThrows(NoValidSemverTagFoundException::class.java) {
            semanticVersionFactory.createFromString("beta")
        }
        Assertions.assertThrows(NoValidSemverTagFoundException::class.java) {
            semanticVersionFactory.createFromString("1.0.0-alpha_beta")
        }
        Assertions.assertThrows(NoValidSemverTagFoundException::class.java) {
            semanticVersionFactory.createFromString("-")
        }
        Assertions.assertThrows(NoValidSemverTagFoundException::class.java) {
            semanticVersionFactory.createFromString("1.0.0-alpha..")
        }
        Assertions.assertThrows(NoValidSemverTagFoundException::class.java) {
            semanticVersionFactory.createFromString("1.0.0-alpha..1")
        }
        Assertions.assertThrows(NoValidSemverTagFoundException::class.java) {
            semanticVersionFactory.createFromString("1.0.0-alpha...1")
        }
        Assertions.assertThrows(NoValidSemverTagFoundException::class.java) {
            semanticVersionFactory.createFromString("1.0.0-alpha....1")
        }
        Assertions.assertThrows(NoValidSemverTagFoundException::class.java) {
            semanticVersionFactory.createFromString("1.0.0-alpha.....1")
        }
        Assertions.assertThrows(NoValidSemverTagFoundException::class.java) {
            semanticVersionFactory.createFromString("1.0.0-alpha......1")
        }
        Assertions.assertThrows(NoValidSemverTagFoundException::class.java) {
            semanticVersionFactory.createFromString("1.0.0-alpha.......1")
        }
        Assertions.assertThrows(NoValidSemverTagFoundException::class.java) {
            semanticVersionFactory.createFromString("01.1.1")
        }
        Assertions.assertThrows(NoValidSemverTagFoundException::class.java) {
            semanticVersionFactory.createFromString("1.01.1")
        }
        Assertions.assertThrows(NoValidSemverTagFoundException::class.java) {
            semanticVersionFactory.createFromString("1.1.01")
        }
        Assertions.assertThrows(NoValidSemverTagFoundException::class.java) {
            semanticVersionFactory.createFromString("1.2")
        }
        Assertions.assertThrows(NoValidSemverTagFoundException::class.java) {
            semanticVersionFactory.createFromString("1.2.3.DEV")
        }
        Assertions.assertThrows(NoValidSemverTagFoundException::class.java) {
            semanticVersionFactory.createFromString("1.2-SNAPSHOT")
        }
        Assertions.assertThrows(NoValidSemverTagFoundException::class.java) {
            semanticVersionFactory.createFromString("1.2.31.2.3----RC-SNAPSHOT.12.09.1--..12+788")
        }
        Assertions.assertThrows(NoValidSemverTagFoundException::class.java) {
            semanticVersionFactory.createFromString("1.2-RC-SNAPSHOT")
        }
        Assertions.assertThrows(NoValidSemverTagFoundException::class.java) {
            semanticVersionFactory.createFromString("-1.0.3-gamma+b7718")
        }
        Assertions.assertThrows(NoValidSemverTagFoundException::class.java) {
            semanticVersionFactory.createFromString("+justmeta")
        }
        Assertions.assertThrows(NoValidSemverTagFoundException::class.java) {
            semanticVersionFactory.createFromString("9.8.7+meta+meta")
        }
        Assertions.assertThrows(NoValidSemverTagFoundException::class.java) {
            semanticVersionFactory.createFromString("9.8.7-whatever+meta+meta")
        }
        Assertions.assertThrows(NoValidSemverTagFoundException::class.java) {
            semanticVersionFactory.createFromString("9999999.9999999.9999999----RC-SNAPSHOT.12.09.1--------------------------------..12")
        }
        Assertions.assertThrows(NoValidSemverTagFoundException::class.java) {
            semanticVersionFactory.createFromString("1.1.1 ")
        }
        Assertions.assertThrows(NoValidSemverTagFoundException::class.java) {
            semanticVersionFactory.createFromString("")
        }
        Assertions.assertThrows(NoValidSemverTagFoundException::class.java) {
            semanticVersionFactory.createFromString("111")
        }
        Assertions.assertThrows(NoValidSemverTagFoundException::class.java) {
            semanticVersionFactory.createFromString("a.b.c")
        }
        Assertions.assertThrows(NoValidSemverTagFoundException::class.java) {
            semanticVersionFactory.createFromString("1.0.0-")
        }
    }
}