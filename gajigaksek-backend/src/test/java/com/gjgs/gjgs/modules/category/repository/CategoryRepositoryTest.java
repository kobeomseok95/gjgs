package com.gjgs.gjgs.modules.category.repository;


import com.gjgs.gjgs.config.CustomDataJpaTest;
import com.gjgs.gjgs.config.TestConfig;
import com.gjgs.gjgs.modules.category.entity.Category;
import com.gjgs.gjgs.modules.category.repositories.CategoryRepository;
import com.gjgs.gjgs.modules.dummy.CategoryDummy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@CustomDataJpaTest
@Import(TestConfig.class)
class CategoryRepositoryTest {

    @Autowired CategoryRepository categoryRepository;
    @Autowired EntityManager em;

    @DisplayName("categoryIdList로 몇개의 category가 현재 존재하는지 갯수 확인")
    @Test
    void count_category_by_idList() throws Exception{
        //given
        Category category1 = categoryRepository.save(CategoryDummy.createCategory(1));
        Category category2 = categoryRepository.save(CategoryDummy.createCategory(2));
        Category category3 = categoryRepository.save(CategoryDummy.createCategory(3));
        List<Long> categoryIdList = new ArrayList<>(Arrays.asList(category1.getId(),category2.getId(),category3.getId()));
        flushAndClear();

        //when then
        assertEquals(3,categoryRepository.countCategoryByIdList(categoryIdList));
    }

    private void flushAndClear() {
        em.flush();
        em.clear();
    }
}
