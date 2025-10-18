package com.system.batch.Tasklet

import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.repeat.RepeatStatus
import java.io.File

class DeleteOldFileTasklet(
    private val path: String,
    private val daysOld: Int
) : Tasklet {

    override fun execute(
        contribution: StepContribution,
        chunkContext: ChunkContext
    ): RepeatStatus? {
        val dir = File(path)
        val cutoffTime = System.currentTimeMillis() - (daysOld * 24 * 60 * 60 * 1000)

        var files = dir.listFiles()
        if(files != null) {
            for (file in files) {
                if (file.lastModified() < cutoffTime) {
                    file.delete()
                } else {
                    return RepeatStatus.CONTINUABLE
                }
            }
        }
        return RepeatStatus.FINISHED
    }
}
