package com.gjgs.gjgs.modules.question.exception;

import com.gjgs.gjgs.modules.utils.exceptions.BusinessException;
import com.gjgs.gjgs.modules.utils.exceptions.ErrorBase;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QuestionException extends BusinessException {

    public QuestionException(ErrorBase code) {
        super(code.getMessage(), code);
    }
}
