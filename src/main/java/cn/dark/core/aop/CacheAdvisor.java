package cn.dark.core.aop;

import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractBeanFactoryPointcutAdvisor;

/**
 * @author dark
 * @date 2021-01-30
 */
public class CacheAdvisor extends AbstractBeanFactoryPointcutAdvisor {

    private CachePointcut pointcut = new CachePointcut();

    @Override
    public Pointcut getPointcut() {
        return pointcut;
    }
}
