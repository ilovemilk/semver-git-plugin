package io.wusa

import io.wusa.extension.Branch
import io.wusa.extension.Branches

class RegexResolver {
    companion object {
        fun findMatchingRegex(branches: Branches, branchName: String): Branch? {
            return try {
                branches.branch.firstOrNull {
                    it.regex.toRegex().matches(branchName)
                }
            } catch (e: Exception) {
                return null
            }
        }
    }
}