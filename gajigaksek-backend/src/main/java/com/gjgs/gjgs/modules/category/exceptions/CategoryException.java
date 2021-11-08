package com.gjgs.gjgs.modules.category.exceptions;

import com.gjgs.gjgs.modules.utils.exceptions.BusinessException;
import com.gjgs.gjgs.modules.utils.exceptions.ErrorBase;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CategoryException extends BusinessException {

    public CategoryException(ErrorBase code) {
        super(code.getMessage(), code);
    }
}
