package cn.dark.lock;

import java.util.concurrent.TimeUnit;

/**
 * @author dark
 * @date 2021-01-24
 */
public interface Lock {

    String getName();

    void lock(String key, long leaseTime, TimeUnit unit);

    void unLock();

}
