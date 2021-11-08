package com.gjgs.gjgs.modules.team.exceptions;

import com.gjgs.gjgs.modules.utils.exceptions.BusinessException;
import com.gjgs.gjgs.modules.utils.exceptions.ErrorBase;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TeamNotFoundException extends BusinessException {

    public TeamNotFoundException(ErrorBase errorBase) {
        super(errorBase);
    }
}
