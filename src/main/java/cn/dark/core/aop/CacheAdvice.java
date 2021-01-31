package cn.dark.core.aop;

import cn.dark.cache.Cache;
import cn.dark.cache.CacheManager;
import cn.dark.cache.RedisCache;
import cn.dark.core.bean.EasyCacheBean;
import cn.dark.core.spel.ElParser;
import cn.dark.core.typeHandler.TypeHandler;
import cn.dark.lock.Lock;
import cn.dark.lock.LockManager;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author dark
 * @date 2021-01-30
 */
@Slf4j
public class CacheAdvice implements MethodInterceptor, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private LockManager lockManager;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        EasyCacheBean easyCacheBean = getEasyCacheBean(invocation);
        String value = getElValue(invocation, easyCacheBean);
        String cacheKey = easyCacheBean.getKeyPrefix() + value;
        Object cacheResult = getCache(easyCacheBean, cacheKey);
        if (cacheResult != null) {
            return handleType(invocation, cacheResult);
        }

        Lock lock = null;
        try {
            lock = lockManager.getLock(easyCacheBean.getLockBean().getLockType());
            lock.lock(easyCacheBean.getLockBean().getKeyPreffix() + easyCacheBean.getLockBean().getKey(),
                    easyCacheBean.getLockBean().getExpire(),
                    TimeUnit.SECONDS);
            Object result = invocation.proceed();
            if (result != null) {
                if (log.isDebugEnabled()) {
                    log.debug("Query from db: ", result);
                }

                putCache(easyCacheBean, cacheKey, result);
            }
            return result;
        } catch (Exception e) {
            log.error("Query from db is error: ", e);
        } finally {
            lock.unLock();
        }
        return null;
    }

    private void putCache(EasyCacheBean easyCacheBean, String cacheKey, Object result) {
        String[] cacheTypes = easyCacheBean.getCacheTypes();
        if (cacheTypes == null || cacheTypes.length == 0) {
            Cache cache = cacheManager.getCache(RedisCache.REDIS);
            cache.set(cacheKey, JSONObject.toJSONString(result), easyCacheBean.getExpire());
            return;
        }

        for (String cacheType : cacheTypes) {
            Cache cache = cacheManager.getCache(cacheType);
            cache.set(cacheKey, JSONObject.toJSONString(result), easyCacheBean.getExpire());
        }
    }

    private Object handleType(MethodInvocation invocation, Object result) {
        Map<String, TypeHandler> typeHandlers = applicationContext.getBeansOfType(TypeHandler.class);
        if (CollectionUtils.isEmpty(typeHandlers)) {
            return result;
        }

        for (TypeHandler handler : typeHandlers.values()) {
            if (handler.support(invocation.getMethod().getGenericReturnType())) {
                return handler.handler(result, invocation.getMethod().getGenericReturnType());
            }
        }
        return result;
    }

    private Object getCache(EasyCacheBean easyCacheBean, String cacheKey) {
        String[] cacheTypes = easyCacheBean.getCacheTypes();
        if (cacheTypes == null || cacheTypes.length == 0) {
            Cache cache = cacheManager.getCache(RedisCache.REDIS);
            return cache.get(cacheKey);
        }

        for (String cacheType : cacheTypes) {
            Cache cache = cacheManager.getCache(cacheType);
            String value = cache.get(cacheKey);
            if (!StringUtils.isEmpty(value)) {
                return value;
            }
        }
        return null;
    }

    private String getElValue(MethodInvocation invocation, EasyCacheBean easyCacheBean) {
        String[] parameterNames = new DefaultParameterNameDiscoverer().getParameterNames(easyCacheBean.getMethod());
        return ElParser.getKeyByParameter(easyCacheBean.getKey(), parameterNames, invocation.getArguments());
    }

    private EasyCacheBean getEasyCacheBean(MethodInvocation invocation) {
        Class<?> targetClass = invocation.getThis() != null ? AopUtils.getTargetClass(invocation.getThis()) : null;
        return  CachePointcut.getCacheBean(invocation.getMethod(), targetClass);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
