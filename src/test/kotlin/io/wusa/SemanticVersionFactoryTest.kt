package io.wusa

import io.mockk.mockkObject
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.lang.IllegalArgumentException

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
        Assertions.assertEquals(semanticVersionFactory.createFromString("1.0.0-0-g123-dirty", project), Version(1, 0, 0, "", "", Suffix(0, "123", true), project))
        Assertions.assertEquals(semanticVersionFactory.createFromString("0.0.1-1-gfe17e7f", project), Version(0, 0, 1, "", "", Suffix(1, "fe17e7f", false), project))
        Assertions.assertEquals(semanticVersionFactory.createFromString("1.0.0-2-g123-dirty", project), Version(1, 0, 0, "", "", Suffix(2, "123", true), project))
        Assertions.assertEquals(semanticVersionFactory.createFromString("1.0.0-0", project), Version(1, 0, 0, "0", "", null, project))
        Assertions.assertEquals(semanticVersionFactory.createFromString("0.0.4", project), Version(0, 0, 4, "", "", null, project))
        Assertions.assertEquals(semanticVersionFactory.createFromString("1.2.3", project), Version(1, 2, 3, "", "", null, project))
        Assertions.assertEquals(semanticVersionFactory.createFromString("10.20.30", project), Version(10, 20, 30, "", "", null, project))
        Assertions.assertEquals(semanticVersionFactory.createFromString("1.1.2-prerelease+meta", project), Version(1, 1, 2, "prerelease", "meta", null, project))
        Assertions.assertEquals(semanticVersionFactory.createFromString("1.1.2+meta", project), Version(1, 1, 2, "", "meta", null, project))
        Assertions.assertEquals(semanticVersionFactory.createFromString("1.1.2+meta-valid", project), Version(1, 1, 2, "", "meta-valid", null, project))
        Assertions.assertEquals(semanticVersionFactory.createFromString("1.0.0-alpha", project), Version(1, 0, 0, "alpha", "", null, project))
        Assertions.assertEquals(semanticVersionFactory.createFromString("1.0.0-beta", project), Version(1, 0, 0, "beta", "", null, project))
        Assertions.assertEquals(semanticVersionFactory.createFromString("1.0.0-alpha.beta", project), Version(1, 0, 0, "alpha.beta", "", null, project))
        Assertions.assertEquals(semanticVersionFactory.createFromString("1.0.0-alpha.beta.1", project), Version(1, 0, 0, "alpha.beta.1", "", null, project))
        Assertions.assertEquals(semanticVersionFactory.createFromString("1.0.0-alpha.1", project), Version(1, 0, 0, "alpha.1", "", null, project))
        Assertions.assertEquals(semanticVersionFactory.createFromString("1.0.0-alpha0.valid", project), Version(1, 0, 0, "alpha0.valid", "", null, project))
        Assertions.assertEquals(semanticVersionFactory.createFromString("1.0.0-alpha.0valid", project), Version(1, 0, 0, "alpha.0valid", "", null, project))
        Assertions.assertEquals(semanticVersionFactory.createFromString("1.0.0-alpha-a.b-c-somethinglong+build.1-aef.1-its-okay", project), Version(1, 0, 0, "alpha-a.b-c-somethinglong", "build.1-aef.1-its-okay", null, project))
        Assertions.assertEquals(semanticVersionFactory.createFromString("1.0.0-rc.1+build.1", project), Version(1, 0, 0, "rc.1", "build.1", null, project))
        Assertions.assertEquals(semanticVersionFactory.createFromString("2.0.0-rc.1+build.123", project), Version(2, 0, 0, "rc.1", "build.123", null, project))
        Assertions.assertEquals(semanticVersionFactory.createFromString("1.2.3-beta", project), Version(1, 2, 3, "beta", "", null, project))
        Assertions.assertEquals(semanticVersionFactory.createFromString("10.2.3-DEV-SNAPSHOT", project), Version(10, 2, 3, "DEV-SNAPSHOT", "", null, project))
        Assertions.assertEquals(semanticVersionFactory.createFromString("1.2.3-SNAPSHOT-123", project), Version(1, 2, 3, "SNAPSHOT-123", "", null, project))
        Assertions.assertEquals(semanticVersionFactory.createFromString("1.0.0", project), Version(1, 0, 0, "", "", null, project))
        Assertions.assertEquals(semanticVersionFactory.createFromString("2.0.0", project), Version(2, 0, 0, "", "", null, project))
        Assertions.assertEquals(semanticVersionFactory.createFromString("1.1.7", project), Version(1, 1, 7, "", "", null, project))
        Assertions.assertEquals(semanticVersionFactory.createFromString("2.0.0+build.1848", project), Version(2, 0, 0, "", "build.1848", null, project))
        Assertions.assertEquals(semanticVersionFactory.createFromString("2.0.1-alpha.1227", project), Version(2, 0, 1, "alpha.1227", "", null, project))
        Assertions.assertEquals(semanticVersionFactory.createFromString("1.0.0-alpha+beta", project), Version(1, 0, 0, "alpha", "beta", null, project))
        Assertions.assertEquals(semanticVersionFactory.createFromString("1.2.3----RC-SNAPSHOT.12.9.1--.12+788", project), Version(1, 2, 3, "---RC-SNAPSHOT.12.9.1--.12", "788", null, project))
        Assertions.assertEquals(semanticVersionFactory.createFromString("1.2.3----R-S.12.9.1--.12+meta", project), Version(1, 2, 3, "---R-S.12.9.1--.12", "meta", null, project))
        Assertions.assertEquals(semanticVersionFactory.createFromString("1.2.3----RC-SNAPSHOT.12.9.1--.12", project), Version(1, 2, 3, "---RC-SNAPSHOT.12.9.1--.12", "", null, project))
        Assertions.assertEquals(semanticVersionFactory.createFromString("1.0.0+0.build.1-rc.10000aaa-kk-0.1", project), Version(1, 0, 0, "", "0.build.1-rc.10000aaa-kk-0.1", null, project))
        Assertions.assertEquals(semanticVersionFactory.createFromString("9999999.9999999.9999999", project), Version(9999999, 9999999, 9999999, "", "", null, project))
        Assertions.assertEquals(semanticVersionFactory.createFromString("1.0.0-0A.is.legal", project), Version(1, 0, 0, "0A.is.legal", "", null, project))
        Assertions.assertEquals(semanticVersionFactory.createFromString("1.1.1", project), Version(1, 1, 1, "", "", null, project))
        Assertions.assertEquals(semanticVersionFactory.createFromString("1.0.0-5-g5242341-dirty", project), Version(1, 0, 0, "", "", Suffix(5, "5242341", true), project))
        Assertions.assertEquals(semanticVersionFactory.createFromString("1.0.0-5-g5242341", project), Version(1, 0, 0, "", "", Suffix(5, "5242341", false), project))
        Assertions.assertEquals(semanticVersionFactory.createFromString("5000.1.1000000-5-g5242341-dirty", project), Version(5000, 1, 1000000, "", "", Suffix(5, "5242341", true), project))
        Assertions.assertEquals(semanticVersionFactory.createFromString("v1.0.0", project), Version(1, 0, 0, "", "", null, project))
        Assertions.assertEquals(semanticVersionFactory.createFromString("V1.0.0", project), Version(1, 0, 0, "", "", null, project))
        Assertions.assertEquals(semanticVersionFactory.createFromString("1.0.0--dirty-5-g5242341", project), Version(1, 0, 0, "-dirty", "", Suffix(5, "5242341", false), project))
        Assertions.assertEquals(semanticVersionFactory.createFromString("1.0.0-dirty--5-g5242341", project), Version(1, 0, 0, "dirty-", "", Suffix(5, "5242341", false), project))
        Assertions.assertEquals(semanticVersionFactory.createFromString("1.0.0-dirty-5--g5242341", project), Version(1, 0, 0, "dirty-5--g5242341", "", null, project))
        Assertions.assertEquals(semanticVersionFactory.createFromString("1.0.0--g5242341", project), Version(1, 0, 0, "-g5242341", "", null, project))
        Assertions.assertEquals(semanticVersionFactory.createFromString("1.0.0-g", project), Version(1, 0, 0, "g", "", null, project))
        Assertions.assertEquals(semanticVersionFactory.createFromString("1.0.0--g", project), Version(1, 0, 0, "-g", "", null, project))
        Assertions.assertEquals(semanticVersionFactory.createFromString("1.0.0-g123-5", project), Version(1, 0, 0, "g123-5", "", null, project))
        Assertions.assertEquals(semanticVersionFactory.createFromString("1.0.0-5-dirty", project), Version(1, 0, 0, "5-dirty", "", null, project))
        Assertions.assertEquals(semanticVersionFactory.createFromString("1.0.0-5", project), Version(1, 0, 0, "5", "", null, project))
        Assertions.assertEquals(semanticVersionFactory.createFromString("1.0.0-5-g", project), Version(1, 0, 0, "5-g", "", null, project))

        Assertions.assertThrows(IllegalArgumentException::class.java) {
            semanticVersionFactory.createFromString("1", project)
        }
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            semanticVersionFactory.createFromString("1.2", project)
        }
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            semanticVersionFactory.createFromString("1.2.3-0123", project)
        }
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            semanticVersionFactory.createFromString("1.2.3-0123.0123", project)
        }
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            semanticVersionFactory.createFromString("1.1.2+.123", project)
        }
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            semanticVersionFactory.createFromString("+invalid", project)
        }
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            semanticVersionFactory.createFromString("-invalid", project)
        }
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            semanticVersionFactory.createFromString("-invalid+invalid", project)
        }
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            semanticVersionFactory.createFromString("-invalid.01", project)
        }
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            semanticVersionFactory.createFromString("alpha", project)
        }
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            semanticVersionFactory.createFromString("alpha.beta", project)
        }
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            semanticVersionFactory.createFromString("alpha.beta.1", project)
        }
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            semanticVersionFactory.createFromString("alpha.1", project)
        }
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            semanticVersionFactory.createFromString("alpha+beta", project)
        }
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            semanticVersionFactory.createFromString("alpha_beta", project)
        }
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            semanticVersionFactory.createFromString("alpha..", project)
        }
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            semanticVersionFactory.createFromString("beta", project)
        }
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            semanticVersionFactory.createFromString("1.0.0-alpha_beta", project)
        }
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            semanticVersionFactory.createFromString("-", project)
        }
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            semanticVersionFactory.createFromString("1.0.0-alpha..", project)
        }
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            semanticVersionFactory.createFromString("1.0.0-alpha..1", project)
        }
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            semanticVersionFactory.createFromString("1.0.0-alpha...1", project)
        }
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            semanticVersionFactory.createFromString("1.0.0-alpha....1", project)
        }
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            semanticVersionFactory.createFromString("1.0.0-alpha.....1", project)
        }
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            semanticVersionFactory.createFromString("1.0.0-alpha......1", project)
        }
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            semanticVersionFactory.createFromString("1.0.0-alpha.......1", project)
        }
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            semanticVersionFactory.createFromString("01.1.1", project)
        }
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            semanticVersionFactory.createFromString("1.01.1", project)
        }
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            semanticVersionFactory.createFromString("1.1.01", project)
        }
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            semanticVersionFactory.createFromString("1.2", project)
        }
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            semanticVersionFactory.createFromString("1.2.3.DEV", project)
        }
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            semanticVersionFactory.createFromString("1.2-SNAPSHOT", project)
        }
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            semanticVersionFactory.createFromString("1.2.31.2.3----RC-SNAPSHOT.12.09.1--..12+788", project)
        }
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            semanticVersionFactory.createFromString("1.2-RC-SNAPSHOT", project)
        }
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            semanticVersionFactory.createFromString("-1.0.3-gamma+b7718", project)
        }
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            semanticVersionFactory.createFromString("+justmeta", project)
        }
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            semanticVersionFactory.createFromString("9.8.7+meta+meta", project)
        }
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            semanticVersionFactory.createFromString("9.8.7-whatever+meta+meta", project)
        }
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            semanticVersionFactory.createFromString("9999999.9999999.9999999----RC-SNAPSHOT.12.09.1--------------------------------..12", project)
        }
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            semanticVersionFactory.createFromString("1.1.1 ", project)
        }
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            semanticVersionFactory.createFromString("", project)
        }
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            semanticVersionFactory.createFromString("111", project)
        }
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            semanticVersionFactory.createFromString("a.b.c", project)
        }
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            semanticVersionFactory.createFromString("1.0.0-", project)
        }
    }
}