package com.gjgs.gjgs.modules.team.exceptions;

import com.gjgs.gjgs.modules.utils.exceptions.BusinessException;
import com.gjgs.gjgs.modules.utils.exceptions.ErrorBase;

public class NotTeamMemberException extends BusinessException {
    public NotTeamMemberException(ErrorBase code) {
        super(code.getMessage(), code);
    }
}
