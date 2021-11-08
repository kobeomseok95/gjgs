package com.gjgs.gjgs.modules.member.exception;


import com.gjgs.gjgs.modules.utils.exceptions.BusinessException;
import com.gjgs.gjgs.modules.utils.exceptions.ErrorBase;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.Errors;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FormException extends BusinessException {
    public FormException(ErrorBase code, Errors errors) {
        super(code.getMessage(), code, errors);
    }

    public FormException(ErrorBase code) {
        super(code.getMessage(), code);
    }

}
