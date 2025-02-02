package org.utbot.framework.util

import org.utbot.framework.plugin.api.ExecutableId
import org.utbot.framework.plugin.api.util.humanReadableName
import org.utbot.framework.plugin.api.util.isEnum

fun isKnownSyntheticMethod(method: ExecutableId): Boolean =
    if (method.classId.isEnum)
        method.humanReadableName.substringBefore('(') in KnownSyntheticMethodNames.enumSyntheticMethodNames
    else
        false

/**
 * Contains names of methods that are always autogenerated and thus it is unlikely that
 * one would want to generate tests for them.
 */
private object KnownSyntheticMethodNames {
    /** List with names of enum methods that are autogenerated */
    val enumSyntheticMethodNames = listOf("values", "valueOf")
}