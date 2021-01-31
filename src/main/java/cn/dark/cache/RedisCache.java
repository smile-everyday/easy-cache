package cn.dark.cache;

/**
 * @author dark
 * @date 2021-01-31
 */
public class RedisCache implements Cache {

    public static final String REDIS = "redis";

    @Override
    public String getName() {
        return REDIS;
    }

    @Override
    public String get(String key) {
        return null;
    }

    @Override
    public String set(String key, String value, long expire) {
        return null;
    }
}
