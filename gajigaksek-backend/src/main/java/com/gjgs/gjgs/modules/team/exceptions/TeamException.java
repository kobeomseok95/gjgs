package com.gjgs.gjgs.modules.team.exceptions;

import com.gjgs.gjgs.modules.utils.exceptions.BusinessException;
import com.gjgs.gjgs.modules.utils.exceptions.ErrorBase;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.Errors;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TeamException extends BusinessException {

    public TeamException(ErrorBase errorBase, Errors errors) {
        super(errorBase.getMessage(), errorBase, errors);
    }

    public TeamException(ErrorBase errorBase) {
        super(errorBase.getMessage(), errorBase);
    }
}
