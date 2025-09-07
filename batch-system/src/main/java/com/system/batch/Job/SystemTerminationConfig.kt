package com.system.batch.Job

import com.system.batch.infra.BatchConfig
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.transaction.PlatformTransactionManager
import java.util.concurrent.atomic.AtomicInteger

//@Import(BatchConfig::class)
@Configuration
open class SystemTerminationConfig(
    private val jobRepository: JobRepository,
    private val transactionManager: PlatformTransactionManager
) {
    private var processKilled: AtomicInteger = AtomicInteger(0)
    private val TERMINATION_TARGET = 5

    // Job 정의
    // JobBuilder는 스프링 5.x 이상부터 생성자 방식으로 변경됨
    // jobRepository를 생성자 주입 받아서 사용한다.
    @Bean
    open fun systemTerminationSimulationJob(): Job {
        return JobBuilder("systemTerminationSimulationJob", jobRepository)
            .start(enterWorldStep())
            .next(meetNPCStep())
            .next(defeatProcessStep())
            .next(completeQuestStep())
            .build()
    }

    @Bean
    open fun enterWorldStep(): Step{
        return StepBuilder("enterWorldStep", jobRepository)
            .tasklet({contribution, chunkContext ->
                println("Entering the world... 접속 완료")
                RepeatStatus.FINISHED
            }, transactionManager)
            .build()
    }

    @Bean
    open fun meetNPCStep(): Step{
        return StepBuilder("meetNPCStep", jobRepository)
            .tasklet({contribution, chunkContext ->
                println("Meeting NPC... NPC와 대화 완료")
                println("첫 번째 미션은 좀비 프로세스 " + TERMINATION_TARGET + "마리를 처치하는 것입니다.")
                RepeatStatus.FINISHED
            }, transactionManager)
            .build()
    }

    @Bean
    open fun defeatProcessStep(): Step {
        return StepBuilder("defeatProcessStep", jobRepository)
            .tasklet({contribution, chunkContext ->
                var terminated = processKilled.incrementAndGet()
                println("현재 처치한 좀비 프로세스 수: $terminated, 목표: $TERMINATION_TARGET")
                if (terminated < TERMINATION_TARGET) {
                    RepeatStatus.CONTINUABLE
                } else {
                    println("모든 좀비 프로세스를 처치했습니다! 미션 완료!")
                    RepeatStatus.FINISHED
                }
            }, transactionManager)
            .build()
    }

    @Bean
    open fun completeQuestStep(): Step {
        return StepBuilder("completeQuestStep", jobRepository)
            .tasklet({contribution, chunkContext ->
                println("Quest Completed! 퀘스트 완료!")
                RepeatStatus.FINISHED
            }, transactionManager)
            .build()
    }
}

