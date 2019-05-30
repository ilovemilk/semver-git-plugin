package io.wusa.extension

import groovy.lang.Closure
import org.gradle.api.Project
import org.gradle.util.ConfigureUtil

class Branches(val project: Project) {
    val branch = mutableListOf<Branch>()

    fun branch(configure: Branch.() -> Unit) {
        createAndAddBranch().configure()
    }
    fun branch(closure: Closure<*>): Branch {
        return ConfigureUtil.configure(closure, createAndAddBranch())
    }

    private fun createAndAddBranch() = Branch(project).apply {
        branch.add(this)
    }
}