package com.gjgs.gjgs.modules.utils.s3.exception;


import com.gjgs.gjgs.modules.utils.exceptions.BusinessException;
import com.gjgs.gjgs.modules.utils.exceptions.ErrorBase;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.Errors;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class S3Exception extends BusinessException {
    public S3Exception(ErrorBase code, Errors errors) {
        super(code.getMessage(), code, errors);
    }

    public S3Exception(ErrorBase code) {
        super(code.getMessage(), code);
    }

}
