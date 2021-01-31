package cn.dark.cache;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author dark
 * @date 2021-01-24
 */
public class CacheManager implements InitializingBean, ApplicationContextAware {

    private ApplicationContext applicationContext;
    
    private Map<String, Cache> cacheMap = new ConcurrentHashMap<>();

    public Cache getCache(String cacheName) {
        return cacheMap.get(cacheName);
    }

    public void putCache(String cacheName, Cache cache) {
        cacheMap.put(cacheName, cache);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, Cache> beansMap = applicationContext.getBeansOfType(Cache.class);
        if (CollectionUtils.isEmpty(beansMap)) {
            throw new NoSuchBeanDefinitionException("No besan of cn.dark.cache.Cache");
        }

        Set<Map.Entry<String, Cache>> entries = beansMap.entrySet();
        for (Map.Entry<String, Cache> entry : entries) {
            cacheMap.put(StringUtils.isEmpty(entry.getValue().getName()) ? entry.getKey() : entry.getValue().getName(), entry.getValue());
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
