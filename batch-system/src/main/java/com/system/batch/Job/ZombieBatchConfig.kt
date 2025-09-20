package com.system.batch.Job

import com.system.batch.Tasklet.ZombieprocessCleanupTasklet
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager

@Configuration
open class ZombieBatchConfig (
    private val jobRepository: JobRepository,
    private val transactionManager: PlatformTransactionManager,
) {

    @Bean
    open fun zombieProcessCleanupTasklet(): Tasklet {
        return ZombieprocessCleanupTasklet()
    }

    @Bean
    open fun zombieCleanupStep(): Step {
        return StepBuilder("zombieCleanupStep", jobRepository)
            //이 트랜잭션 매니저는 생략할 수 있음. 생략하면 jobRepository에 등록된 트랜잭션 매니저가 사용된다.
            .tasklet(zombieProcessCleanupTasklet(), transactionManager)
            .build()
    }

    @Bean
    open fun zombieCleanupJob(): Job {
        return JobBuilder("zombieCleanupJob", jobRepository)
            .start(zombieCleanupStep())
            .build()
    }
}