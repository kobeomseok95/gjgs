package com.gjgs.gjgs.testutils.repository;

import com.gjgs.gjgs.config.TestConfig;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import javax.persistence.EntityManager;

//@CustomDataJpaTest
//@CustomJdbcBatchTest
@Import(TestConfig.class)
public class RepositoryTest {

    @Autowired EntityManager em;
    @Autowired JPAQueryFactory query;

    protected void flushAndClear() {
        em.flush();
        em.clear();
    }
}
