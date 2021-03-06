package com.gjgs.gjgs.modules.utils.global;


import com.gjgs.gjgs.modules.member.exception.TokenErrorCodes;
import com.gjgs.gjgs.modules.utils.exceptions.BusinessException;
import com.gjgs.gjgs.modules.utils.exceptions.FileErrorCodes;
import com.gjgs.gjgs.modules.utils.exceptions.HeaderErrorCodes;
import com.gjgs.gjgs.modules.utils.exceptions.search.SearchErrorCodes;
import com.gjgs.gjgs.modules.utils.response.ErrorResponse;
import com.siot.IamportRestClient.exception.IamportResponseException;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.ElasticsearchStatusException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import javax.validation.ConstraintViolationException;
import java.io.IOException;

@RestControllerAdvice
@Slf4j
public class GlobalControllerAdvice {


    /**
     * Bean Validation extends BindException
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> beanValidationException(MethodArgumentNotValidException e) {
        log.error(e.getMessage());
        ErrorResponse response = ErrorResponse.of(e.getBindingResult());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Bean Validation
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> bindException(BindException e) {
        log.error(e.getMessage());
        ErrorResponse response = ErrorResponse.of(e.getBindingResult());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }


    /**
     * ???????????? ?????? ????????????
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> businessException(BusinessException e) {

        log.error(e.getMessage());

        ErrorResponse response = new ErrorResponse();
        if (e.getErrors() != null) {
            response = ErrorResponse.of(e.getErrorCodeBase(), e.getErrors(), e.getMessage());
        } else {
            response = ErrorResponse.of(e.getErrorCodeBase());
        }
        return new ResponseEntity<>(response, HttpStatus.valueOf(e.getErrorCodeBase().getStatus()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> runtimeException(RuntimeException e) {
        log.error(e.getMessage());
        ErrorResponse response = ErrorResponse.of(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * ??? ??? ?????? ?????? ??????
     */
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error(e.getMessage());
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * ?????? ?????? ????????????
     */
    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity handleMissingParams(MissingRequestHeaderException e) {

        String name = e.getHeaderName();
        log.error(e.getMessage());
        if (name.equals("Authorization")) {
            final ErrorResponse response = ErrorResponse.of(TokenErrorCodes.NO_AUTHORIZATION_TOKEN);
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        if (name.equals("KakaoAccessToken")) {
            final ErrorResponse response = ErrorResponse.of(TokenErrorCodes.NO_KAKAO_ACCESS_TOKEN);
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        if (name.equals("RefreshToken")) {
            final ErrorResponse response = ErrorResponse.of(TokenErrorCodes.NO_REFRESH_TOKEN);
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
        final ErrorResponse response = ErrorResponse.of(HeaderErrorCodes.MISSING_REQUEST_HEADER);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * AccessDeniedException
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> accessDeniedException(AccessDeniedException e) {
        log.error(e.getMessage());
        return new ResponseEntity<>(ErrorResponse.ofForbidden("?????? ????????? ????????????."), HttpStatus.FORBIDDEN);
    }

    /**
     * request body missing exception
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> httpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error(e.getMessage());
        final ErrorResponse response = ErrorResponse.of("body??? ????????? json ?????? ???????????? ????????????, ???????????? ?????? json ?????? ?????????????????????.");
        return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
    }

    /**
     *  Keyword ConstraintViolationException
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> constraintViolationException(ConstraintViolationException e) {
        log.error(e.getMessage());
        ErrorResponse response = ErrorResponse.of(SearchErrorCodes.KEYWORD_IS_NOT_ALLOWED);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     *  DatabaseConstraintViolationException
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> constraintViolationException(DataIntegrityViolationException e) {
        log.error(e.getMostSpecificCause().getMessage());
        return new ResponseEntity<>(ErrorResponse.of("?????? ????????? ?????? ????????? ????????? ????????????."), HttpStatus.BAD_REQUEST);
    }

    /**
     *  Multipart-Form-Data Exception
     *  if??? ????????? ?????? : ????????? ?????? ???????????? ???????????? ????????? ????????? ????????? ??????
     */
    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<ErrorResponse> missingServletRequestPartException(MissingServletRequestPartException e) {
        log.error(e.getMessage());
//        if (e.getRequestPartName().toUpperCase().contains("FILE")) {
        return fileNotFoundResponse();
//        }
//        return null;
    }

    private ResponseEntity<ErrorResponse> fileNotFoundResponse() {
        ErrorResponse response = ErrorResponse.of(FileErrorCodes.MISSING_FILE);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IamportResponseException.class)
    public ResponseEntity<ErrorResponse> iamportResponseException(IamportResponseException e) {
        ErrorResponse response = ErrorResponse.of(e.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(ElasticsearchStatusException.class)
    public ResponseEntity<ErrorResponse> elasticsearchStatusException(ElasticsearchStatusException e) {
        ErrorResponse response = ErrorResponse.of("?????? ?????? ??????????????? ?????? ????????????.");
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<ErrorResponse> ioException(IOException e) {
        log.error(e.getMessage());
        ErrorResponse response = ErrorResponse.of("?????? ?????? ??????????????? ?????? ????????????.");
        return ResponseEntity.badRequest().body(response);
    }
}

