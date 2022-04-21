package com.heima.common.exception;

public interface BusinessExceptionAssert extends BaseErrorInfoInterface, Assert {

    @Override
    default BaseException newException() {
        return new BusinessException(this);
    }

    @Override
    default BaseException newException(Object... args) {
//        String msg = MessageFormat.format(this.getMessage(), args);
//        return new BusinessException(this, args, msg);
        return newException();
    }

    @Override
    default BaseException newException(Throwable t, Object... args) {
//        String msg = MessageFormat.format(this.getMessage(), args);
//        return new BusinessException(this, args, msg, t);
        return newException();
    }
}