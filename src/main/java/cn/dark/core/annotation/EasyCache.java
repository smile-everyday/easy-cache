package cn.dark.core.annotation;

import org.springframework.core.annotation.AliasFor;
import sun.rmi.runtime.Log;

import java.lang.annotation.*;

/**
 * 使用该注解缓存捕获方法的返回值
 *
 * @author dark
 * @date 2021-01-24
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EasyCache {

    /**
     * 缓存方式
     */
    @AliasFor("cacheTypes")
    String[] value() default {"redis"};

    /**
     * 缓存方式
     */
    @AliasFor("value")
    String[] cacheTypes() default {"redis"};

    /**
     * redis key 的前缀
     */
    String keyPrefix() default "easy-cache:";

    /**
     * key的后缀，
     */
    String key() default "";

    /**
     * 过期时间，默认60s
     */
    long expire() default 60;

    /**
     * 缓存过期后查询需要使用的锁
     */
    Lock lock() default @Lock;

}
