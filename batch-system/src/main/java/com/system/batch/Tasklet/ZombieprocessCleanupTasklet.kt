package com.system.batch.Tasklet

import lombok.extern.slf4j.Slf4j
import org.slf4j.LoggerFactory
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.repeat.RepeatStatus

@Slf4j
class ZombieprocessCleanupTasklet : Tasklet {

    val processToKill: Int = 10
    private var killedProcesses = 0

    private val log = LoggerFactory.getLogger(ZombieprocessCleanupTasklet::class.java)

    @Override
    override fun execute(
        contribution: StepContribution,
        chunkContext: ChunkContext
    ): RepeatStatus? {
        killedProcesses++
        log.info(" 프로세스 강제 종료.. ({}/{})", killedProcesses, processToKill)

        if(killedProcesses == processToKill) {
            log.info("모든 좀비 프로세스를 제거했습니다.")
            return RepeatStatus.FINISHED
        }

        return RepeatStatus.CONTINUABLE
    }
}