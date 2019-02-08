package io.wusa

data class Language(val name: String, val hotness: Int)

class SemverGit {
    fun kotlinLanguage() = Language("Kotlin", 10)
}