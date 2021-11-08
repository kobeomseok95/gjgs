package com.gjgs.gjgs.modules.team.exceptions;

import com.gjgs.gjgs.modules.utils.exceptions.BusinessException;
import com.gjgs.gjgs.modules.utils.exceptions.ErrorBase;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.Errors;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExitMemberIsTeamLeaderException extends BusinessException {

    public ExitMemberIsTeamLeaderException(ErrorBase errorBase, Errors errors) {
        super(errorBase.getMessage(), errorBase, errors);
    }

    public ExitMemberIsTeamLeaderException(ErrorBase errorBase) {
        super(errorBase.getMessage(), errorBase);
    }
}
