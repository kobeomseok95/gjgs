package com.gjgs.gjgs.infra.config;

//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import springfox.documentation.builders.ApiInfoBuilder;
//import springfox.documentation.builders.PathSelectors;
//import springfox.documentation.builders.RequestHandlerSelectors;
//import springfox.documentation.service.ApiInfo;
//import springfox.documentation.spi.DocumentationType;
//import springfox.documentation.spring.web.plugins.Docket;
//import springfox.documentation.swagger2.annotations.EnableSwagger2;

//@Configuration
//@EnableSwagger2
//public class SwaggerConfig {
//
//    // 그룹명이나 이동경로, 보여질 api가 속한 패키지 등의 자세한 정보
//    @Bean
//    public Docket swaggerApi() {
//        return new Docket(DocumentationType.SWAGGER_2)
//                .select()
//                .apis(RequestHandlerSelectors.any()) // 현재 requestmapping으로 할당된 모든 URL 리스트 추출
//                .paths(PathSelectors.ant("/api/**")) //PathSelectors.any() 를 할경우 모든 경로가 다 사용된다. RestController가 아닌 것까지 사용된다.
//                .build()
//                .apiInfo(swaggerInfo())
//                .useDefaultResponseMessages(false); // 200,400 번등 status code를 자동으로 만들어주는 기능. 오류가 날 수 있기에 꺼준다.
//    }
//
//    // swagger-ui.html
//    // swagger-ui 메인으로 보여질 정보
//    private ApiInfo swaggerInfo() {
//        return new ApiInfoBuilder()
//                .title("가지각색 API Documentation")
//                .description("가지각색 개발시 사용되는 서버 API에 대한 연동 문서입니다")
//                //.license("backtony").licenseUrl("http://github.com/backtony").version("1")
//                .build();
//    }
//}

