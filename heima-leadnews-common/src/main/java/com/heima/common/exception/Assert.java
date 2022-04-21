package com.heima.common.exception;

import org.springframework.lang.Nullable;

/**
 * @author zed21@aliyun.com
 * @date 2022/3/11 21:00
 */
public interface Assert {

    BaseException newException();

    /**
     * 创建异常
     * @param args
     * @return
     */
    BaseException newException(Object... args);


    /**
     * 创建异常
     * @param t
     * @param args
     * @return
     */
    BaseException newException(Throwable t, Object... args);

    /**
     * <p>断言对象<code>obj</code>非空。如果对象<code>obj</code>为空，则抛出异常
     *
     * @param obj 待判断对象
     */
    default void assertNotNull(@Nullable Object obj) {
        if (obj == null) {
            throw newException();
        }
    }

    //断言不能为空
    default boolean assertNonNull(@Nullable Object obj) {
        return obj == null;
    }

//    default boolean assertStringsIsEmpty(@Nullable Object... strs) {
//        if (strs != null) {
//            for (Object str : strs) {
//                if (str == null || "".equals(str)) {
//                    return true;
//                }
//            }
//            return false;
//        }
//        return true;
//    }

    /**
     * <p>断言对象<code>obj</code>非空。如果对象<code>obj</code>为空，则抛出异常
     * <p>异常信息<code>message</code>支持传递参数方式，避免在判断之前进行字符串拼接操作
     *
     * @param obj 待判断对象
     * @param args message占位符对应的参数列表
     */
    default void assertNotNull(Object obj, Object... args) {
        if (obj == null) {
            throw newException(args);
        }
    }

    default void assertNotNull(Object obj1, Object obj2) {
        if (obj1 == null || obj2 == null) {
            throw newException();
        }
    }
}
