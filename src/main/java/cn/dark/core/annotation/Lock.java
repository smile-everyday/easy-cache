package cn.dark.core.annotation;

import java.lang.annotation.*;

/**
 * 当使用该注解，在缓存失效后会进行加锁，避免多个线程同时进入到数据库，
 * 配合{@link EasyCache} 使用
 *
 * @author dark
 * @date 2021-01-24
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Lock {

    /**
     * 锁的类型
     */
    String lockType() default "redisson";

    /**
     * key的前缀
     */
    String keyPrefix() default "easy-cache:lock:";

    /**
     * key的后缀，支持如 #key 的表达式
     */
    String key() default "";

    /**
     * 过期时间，默认60s
     */
    long expire() default 60;

}
