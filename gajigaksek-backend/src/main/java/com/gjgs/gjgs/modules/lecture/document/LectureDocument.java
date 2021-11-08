package com.gjgs.gjgs.modules.lecture.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PRIVATE;
import static org.springframework.data.elasticsearch.annotations.DateFormat.date_hour_minute_second_millis;
import static org.springframework.data.elasticsearch.annotations.DateFormat.epoch_millis;

@Document(indexName = "lecture")
@Setting(settingPath = "elasticsearch/lecture-settings.json")
@Mapping(mappingPath = "elasticsearch/lecture-mappings.json")
@Getter @NoArgsConstructor(access = PRIVATE) @AllArgsConstructor(access = PRIVATE) @Builder
public class LectureDocument {

    @Id
    private String id;

    @Field(type = FieldType.Long)
    private Long lectureId;

    @Field(type = FieldType.Text)
    private String imageUrl;

    @Field(type = FieldType.Text, analyzer = "word_analyzer")
    private String title;

    @Field(type = FieldType.Text, analyzer = "word_analyzer")
    private String description;

    @Field(type = FieldType.Text, analyzer = "word_analyzer")
    private String finishedProductText;

    @Field(type = FieldType.Long)
    private Long zoneId;

    @Field(type = FieldType.Long)
    private Long categoryId;

    @Field(type = FieldType.Integer)
    private Long regularPrice;

    @Field(type = FieldType.Integer)
    private Long priceOne;

    @Field(type = FieldType.Integer)
    private Long priceTwo;

    @Field(type = FieldType.Integer)
    private Long priceThree;

    @Field(type = FieldType.Integer)
    private Long priceFour;

    @Field(type = FieldType.Integer)
    private Long reviewCount;

    @Field(type = FieldType.Double)
    private Double score;

    @Field(type = FieldType.Long)
    private Long clickCount;

    @CreatedDate
    @Field(type = FieldType.Date, format = {date_hour_minute_second_millis, epoch_millis})
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Field(type = FieldType.Date, format = {date_hour_minute_second_millis, epoch_millis})
    private LocalDateTime lastModifiedDate;
}
