package com.gjgs.gjgs.modules.lecture.controllers;

import com.gjgs.gjgs.modules.lecture.dtos.create.PutLectureResponse;

import java.util.Set;

import static java.time.LocalDate.now;

public class PutLectureResponseDtoStub {

    public static PutLectureResponse getPutLectureResponseDto(String lectureStatus) {
        return PutLectureResponse.builder()
                .lectureId(1L)
                .categoryId(1L)
                .zoneId(1L)
                .title("test")
                .address("test")
                .thumbnailImageFileName("test")
                .thumbnailImageFileUrl("test")
                .minParticipants(2)
                .maxParticipants(10)
                .mainText("test")
                .lectureStatus(lectureStatus)
                .finishedProductList(Set.of(PutLectureResponse.FinishedProductResponse.builder()
                        .finishedProductId(1L)
                        .orders(1)
                        .text("테스트")
                        .finishedProductImageName("test")
                        .finishedProductImageUrl("testurl")
                        .build()
                ))
                .curriculumList(Set.of(PutLectureResponse.CurriculumResponse.builder()
                        .curriculumId(1L)
                        .orders(1)
                        .title("test")
                        .detailText("test")
                        .curriculumImageName("test")
                        .curriculumImageUrl("teset")
                        .build()))
                .scheduleList(Set.of(PutLectureResponse.ScheduleResponse.builder()
                        .scheduleId(1L)
                        .lectureDate(now())
                        .startHour(12)
                        .startMinute(0)
                        .endHour(13)
                        .endMinute(0)
                        .progressMinute(60)
                        .build()))
                .price(PutLectureResponse.PriceResponse.builder()
                        .regularPrice(1000)
                        .priceOne(1000)
                        .priceTwo(1000)
                        .priceThree(1000)
                        .priceFour(1000)
                        .build())
                .coupon(PutLectureResponse.CouponResponse.builder()
                        .couponCount(10)
                        .couponPrice(500)
                        .build())
                .build();
    }
}
