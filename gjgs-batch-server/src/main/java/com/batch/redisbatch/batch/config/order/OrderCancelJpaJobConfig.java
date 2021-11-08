package com.batch.redisbatch.batch.config.order;

import com.batch.redisbatch.domain.TeamOrder;
import com.batch.redisbatch.enums.TeamOrderStatus;
import com.batch.redisbatch.service.interfaces.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.*;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import java.time.LocalDateTime;
import java.util.HashMap;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class OrderCancelJpaJobConfig {
    public static final String JOB_NAME = "teamOrderCancelJob";
    public static final String STEP_NAME = "teamOrderCancelStep";

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;
    private final OrderService orderService;

    private final int chunkSize = 1000;

    @Bean
    public Job teamOrderCancelJob() throws Exception {
        return jobBuilderFactory.get(JOB_NAME)
                .start(teamOrderCancelStep())
                .incrementer(new DayJobIncrementer())
                .build();
    }

    @Bean
    @JobScope
    public Step teamOrderCancelStep(){
        return stepBuilderFactory.get(STEP_NAME)
                .<TeamOrder,TeamOrder>chunk(chunkSize)
                .reader(orderPagingReader())
                .processor(orderPagingProcessor())
                .writer(orderJpaItemWriter())
                .build();
    }

    @Bean
    @StepScope
    public JpaPagingItemReader<TeamOrder> orderPagingReader() {

        JpaPagingItemReader<TeamOrder> reader = new JpaPagingItemReader<TeamOrder>() {
            @Override
            public int getPage() {
                return 0;
            }
        };

        HashMap<String, Object> params = new HashMap<>();
        params.put("status", TeamOrderStatus.WAIT);
        params.put("date",LocalDateTime.now().minusMinutes(30));

        // 30분 지났고 status가 wait인것 가져오기
        reader.setQueryString("SELECT to FROM TeamOrder to WHERE to.teamOrderStatus =:status AND to.createdDate < :date ORDER BY to.id");
        reader.setPageSize(chunkSize);
        reader.setEntityManagerFactory(entityManagerFactory);
        reader.setParameterValues(params);
        reader.setName("orderPagingReader");

        return reader;
    }

    @Bean
    @StepScope
    public ItemProcessor<TeamOrder, TeamOrder> orderPagingProcessor() {
        return item -> {
            orderService.changeStatus(item.getTeamId(), item.getScheduleId());
            item.changeStatus(TeamOrderStatus.CANCEL);
            return item;
        };
    }

    @Bean
    @StepScope
    public JpaItemWriter<TeamOrder> orderJpaItemWriter() {
        JpaItemWriter<TeamOrder> jpaItemWriter = new JpaItemWriter<>();
        jpaItemWriter.setEntityManagerFactory(entityManagerFactory);
        return jpaItemWriter;
    }
}
