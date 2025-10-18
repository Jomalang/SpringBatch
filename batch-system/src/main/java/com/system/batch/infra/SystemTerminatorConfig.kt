package com.system.batch.infra

import org.slf4j.LoggerFactory
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager
import kotlin.jvm.java

@Configuration
open class SystemTerminatorConfig {

    companion object {
        private val log = LoggerFactory.getLogger(SystemTerminatorConfig::class.java)
    }

    @Bean
    open fun processTerminatorJob(
        jobRepository: JobRepository,
        @Qualifier(value = "terminatingStep") terminatingStep: Step
    ): Job {
        return JobBuilder("processTerminatorJob", jobRepository)
            .start(terminatingStep)
            .build()
    }

    @Bean
    open fun terminatingStep(
        jobRepository: JobRepository,
        terminatorTasklet: Tasklet,
        transactionManger: PlatformTransactionManager
    ): Step {
        return StepBuilder("terminatingStep", jobRepository)
            .tasklet(terminatorTasklet, transactionManger)
            .build()
    }

    @Bean
    @StepScope
    open fun terminatorTasklet(
        @Value("#{jobParameters['terminationId']}") terminatorId: String,
        @Value("#{jobParameters['targetCount']}") targetCount: Int
    ): Tasklet = Tasklet {_, _ ->
            log.info("시스템 종결자 정보:")
            log.info("ID: {}", terminatorId)
            log.info("제거 대상 수: {}", targetCount)
            log.info("⚡ SYSTEM TERMINATOR {} 작전을 개시합니다.", terminatorId)
            log.info("☠️ {}개의 프로세스를 종료합니다.", targetCount)

            for (i in 1..targetCount) {
                log.info("프로세스 {} 종료 완료!", i)
            }
            log.info("모든 대상 프로세스가 종료되었습니다.")
            RepeatStatus.FINISHED
    }
}
