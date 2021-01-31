package cn.dark.core.typeHandler;

import com.alibaba.fastjson.JSONArray;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.reflect.Type;
import java.util.List;

/**
 * @author dark
 * @date 2021-01-31
 */
public class ListTypeHandler implements TypeHandler {

    @Override
    public boolean support(Type returnType) {
        //todo 强转有问题
        return List.class.isAssignableFrom(((ParameterizedTypeImpl) returnType).getRawType());
    }

    @Override
    public Object handler(Object result, Type returnType) {
        // 获得泛型的实际类型
        Type[] actualTypeArguments = ((ParameterizedTypeImpl) returnType).getActualTypeArguments();
        if (actualTypeArguments != null && actualTypeArguments.length > 0) {
            return JSONArray.parseArray((String) result, actualTypeArguments);
        } else {
            return JSONArray.parseArray((String) result, Object.class);
        }
    }
}
