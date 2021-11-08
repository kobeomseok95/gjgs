package com.gjgs.gjgs.modules.matching.exception;

import com.gjgs.gjgs.modules.utils.exceptions.BusinessException;
import com.gjgs.gjgs.modules.utils.exceptions.ErrorBase;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.Errors;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MatchingException extends BusinessException {
    public MatchingException(ErrorBase code) {
        super(code.getMessage(), code);
    }
    public MatchingException(ErrorBase code, Errors errors) {
        super(code.getMessage(), code, errors);
    }
}
