package org.utbot.framework.codegen.model.constructor

import org.utbot.framework.plugin.api.ClassId
import org.utbot.framework.plugin.api.ExecutableId
import org.utbot.framework.plugin.api.FieldId
import org.utbot.framework.plugin.api.UtClusterInfo
import org.utbot.framework.plugin.api.UtExecution
import org.utbot.framework.plugin.api.UtExecutionSuccess
import org.utbot.framework.plugin.api.UtMethodTestSet
import org.utbot.framework.plugin.api.util.executableId
import org.utbot.framework.plugin.api.util.objectClassId
import org.utbot.framework.plugin.api.util.voidClassId
import soot.jimple.JimpleBody

data class CgMethodTestSet private constructor(
    val executableId: ExecutableId,
    val jimpleBody: JimpleBody? = null,
    val errors: Map<String, Int> = emptyMap(),
    val clustersInfo: List<Pair<UtClusterInfo?, IntRange>>,
) {
    var executions: List<UtExecution> = emptyList()
        private set

    constructor(from: UtMethodTestSet) : this(
        from.method.callable.executableId,
        from.jimpleBody,
        from.errors,
        from.clustersInfo
    ) {
        executions = from.executions
    }

    /**
     * Splits [CgMethodTestSet] into separate test sets having
     * unique result model [ClassId] in each subset.
     */
    fun splitExecutionsByResult() : List<CgMethodTestSet> {
        val successfulExecutions = executions.filter { it.result is UtExecutionSuccess }
        val executionsByResult: Map<ClassId, List<UtExecution>> =
            if (successfulExecutions.isNotEmpty()) {
                successfulExecutions.groupBy { (it.result as UtExecutionSuccess).model.classId }
            } else {
                mapOf(objectClassId to executions)
            }

        return executionsByResult.map{ (_, executions) -> substituteExecutions(executions) }
    }

    /**
     * Splits [CgMethodTestSet] test sets by affected static fields statics.
     *
     * A separate test set is created for each combination of modified statics.
     */
    fun splitExecutionsByChangedStatics(): List<CgMethodTestSet> {
        val successfulExecutions = executions.filter { it.result is UtExecutionSuccess }
        val executionsByStaticsUsage: Map<Set<FieldId>, List<UtExecution>> =
            if (successfulExecutions.isNotEmpty()) {
                successfulExecutions.groupBy {
                    it.stateBefore.statics.keys
                }
            } else {
                mapOf(executions.first().stateBefore.statics.keys to executions)
            }

        return executionsByStaticsUsage.map { (_, executions) -> substituteExecutions(executions) }
    }

    /**
     * Finds a [ClassId] of all result models in executions.
     *
     * Tries to find a unique result type in testSets or
     * gets executable return type.
     */
    fun resultType(): ClassId {
        return when (executableId.returnType) {
            voidClassId -> executableId.returnType
            else -> {
                val successfulExecutions = executions.filter { it.result is UtExecutionSuccess }
                if (successfulExecutions.isNotEmpty()) {
                    successfulExecutions
                        .map { (it.result as UtExecutionSuccess).model.classId }
                        .distinct()
                        .singleOrNull()
                        ?: executableId.returnType
                } else {
                    executableId.returnType
                }
            }
        }
    }

    private fun substituteExecutions(newExecutions: List<UtExecution>): CgMethodTestSet =
        copy().apply { executions = newExecutions }
}