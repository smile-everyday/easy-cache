package cn.dark.lock;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author dark
 * @date 2021-01-24
 */
public class LockManager implements InitializingBean, ApplicationContextAware {

    private ApplicationContext applicationContext;

    private static final Map<String, Lock> LOCK_MAP = new ConcurrentHashMap<>();

    public Lock getLock(String lockName) {
        return LOCK_MAP.get(lockName);
    }

    public void putLock(String lockName, Lock lock) {
        LOCK_MAP.put(lockName, lock);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, Lock> lockMap = applicationContext.getBeansOfType(Lock.class);
        for (Map.Entry<String, Lock> entry : lockMap.entrySet()) {
            LOCK_MAP.put(StringUtils.isEmpty(entry.getValue().getName()) ? entry.getKey() : entry.getValue().getName(), entry.getValue());
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
