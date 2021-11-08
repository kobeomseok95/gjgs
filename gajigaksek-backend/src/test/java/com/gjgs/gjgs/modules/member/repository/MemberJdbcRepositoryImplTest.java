package com.gjgs.gjgs.modules.member.repository;


import com.gjgs.gjgs.config.CustomJdbcBatchTest;
import com.gjgs.gjgs.modules.category.entity.Category;
import com.gjgs.gjgs.modules.dummy.CategoryDummy;
import com.gjgs.gjgs.modules.dummy.MemberDummy;
import com.gjgs.gjgs.modules.member.entity.Member;
import com.gjgs.gjgs.modules.member.repository.impl.MemberJdbcRepositoryImpl;
import com.gjgs.gjgs.modules.member.repository.interfaces.MemberCategoryRepository;
import com.gjgs.gjgs.modules.member.repository.interfaces.MemberJdbcRepository;
import com.gjgs.gjgs.testutils.repository.SetUpMemberRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Import({
        MemberJdbcRepositoryImpl.class
})
@CustomJdbcBatchTest
class MemberJdbcRepositoryImplTest extends SetUpMemberRepository {

    @Autowired MemberJdbcRepository memberJdbcRepository;
    @Autowired MemberCategoryRepository memberCategoryRepository;

    @AfterEach
    void tearDown() throws Exception {
        memberCategoryRepository.deleteAll();
    }

    @DisplayName("멤버가 찜한 카테고리 벌크 insert 쿼리")
    @Test
    void insert_memberCategory_list() throws Exception {

        //given
        Member member = memberRepository.save(MemberDummy.createDataJpaTestMember(zone,category));
        Category category2 = categoryRepository.save(CategoryDummy.createCategory(2));
        flushAndClear();

        //when
        memberJdbcRepository.insertMemberCategoryList(member.getId(), Arrays.asList(category.getId(),category2.getId()));
        long count = memberCategoryRepository.count();

        //then
        assertEquals(2, count);
    }
}
