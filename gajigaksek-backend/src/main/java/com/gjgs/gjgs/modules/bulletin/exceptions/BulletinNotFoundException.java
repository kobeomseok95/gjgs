package com.gjgs.gjgs.modules.bulletin.exceptions;

import com.gjgs.gjgs.modules.utils.exceptions.BusinessException;
import com.gjgs.gjgs.modules.utils.exceptions.ErrorBase;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BulletinNotFoundException extends BusinessException {

    public BulletinNotFoundException(ErrorBase errorBase) {
        super(errorBase);
    }
}
