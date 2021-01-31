package cn.dark.core.aop;

import cn.dark.core.annotation.EasyCache;
import cn.dark.core.annotation.Lock;
import cn.dark.core.bean.EasyCacheBean;
import cn.dark.core.bean.LockBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.MethodClassKey;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author dark
 * @date 2021-01-30
 */
@Slf4j
public class CachePointcut implements Pointcut, MethodMatcher {

    private static final Map<Object, EasyCacheBean> ATTRIBUTES_MAP = new ConcurrentHashMap<>();

    @Override
    public boolean matches(Method method, Class<?> targetClass) {
        Object cacheKey = getCacheKey(method, targetClass);
        EasyCacheBean easyCacheBean = ATTRIBUTES_MAP.get(cacheKey);
        if (easyCacheBean != null) {
            return true;
        }

        Method specificMethod = AopUtils.getMostSpecificMethod(method, targetClass);
        if (AnnotatedElementUtils.hasAnnotation(method, EasyCache.class)
                || AnnotatedElementUtils.hasAnnotation(specificMethod, EasyCache.class)) {
            EasyCache easyCache = AnnotationUtils.getAnnotation(specificMethod, EasyCache.class);
            EasyCacheBean cacheBean = new EasyCacheBean();
            cacheBean.setCacheTypes(easyCache.cacheTypes())
                    .setExpire(easyCache.expire())
                    .setKeyPrefix(easyCache.keyPrefix())
                    .setKey(StringUtils.isEmpty(easyCache.key()) ? UUID.randomUUID().toString() : easyCache.key())
                    .setMethod(specificMethod);

            Lock lock = easyCache.lock();
            if (lock != null) {
                LockBean lockBean = new LockBean()
                        .setKeyPreffix(lock.keyPrefix())
                        .setKey(StringUtils.isEmpty(lock.key()) ? UUID.randomUUID().toString() : lock.key())
                        .setLockType(lock.lockType())
                        .setExpire(lock.expire());
                cacheBean.setLockBean(lockBean);
            }

            if (log.isDebugEnabled()) {
                log.debug("Easy cache bean: {}", cacheBean);
            }

            ATTRIBUTES_MAP.put(cacheKey, cacheBean);
            return true;
        }
        return false;
    }

    @Override
    public boolean isRuntime() {
        return false;
    }

    @Override
    public boolean matches(Method method, Class<?> targetClass, Object... args) {
        return false;
    }

    @Override
    public ClassFilter getClassFilter() {
        return ClassFilter.TRUE;
    }

    @Override
    public MethodMatcher getMethodMatcher() {
        return this;
    }

    protected static Object getCacheKey(Method method, @Nullable Class<?> targetClass) {
        return new MethodClassKey(method, targetClass);
    }

    public static EasyCacheBean getCacheBean(Method method, @Nullable Class<?> targetClass) {
        return ATTRIBUTES_MAP.get(getCacheKey(method, targetClass));
    }
}
