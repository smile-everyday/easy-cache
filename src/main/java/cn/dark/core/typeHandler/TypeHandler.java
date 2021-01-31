package cn.dark.core.typeHandler;

import java.lang.reflect.Type;

/**
 * @author dark
 * @date 2021-01-31
 */
public interface TypeHandler {

    boolean support(Type returnType);

    Object handler(Object result, Type returnType);
}
