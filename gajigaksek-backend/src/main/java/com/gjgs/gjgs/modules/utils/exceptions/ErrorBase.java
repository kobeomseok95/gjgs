package com.gjgs.gjgs.modules.utils.exceptions;

public interface ErrorBase extends ErrorRoot {

    ErrorBase[] getValues();

    int getStatus();

    String getMessage();

    String getName();

    String getCode();
}
