package cn.dark.core.bean;

import cn.dark.core.annotation.EasyCache;
import lombok.Data;
import lombok.experimental.Accessors;

import java.lang.reflect.Method;

/**
 * 对应 {@link EasyCache} 注解的实体类
 *
 * @author dark
 * @date 2021-01-24
 */
@Data
@Accessors(chain = true)
public class EasyCacheBean {

    /**
     * @see EasyCache#cacheTypes()
     */
    private String[] cacheTypes;

    /**
     * @see EasyCache#expire()
     */
    private long expire;

    /**
     * @see EasyCache#keyPrefix()
     */
    private String keyPrefix;

    /**
     * @see EasyCache#key()
     */
    private String key;

    /**
     * 切面捕获到的方法
     */
    private Method method;

    /**
     * @see EasyCache#lock()
     */
    private LockBean lockBean;

}
