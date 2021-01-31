package cn.dark.cache;

/**
 * @author dark
 * @date 2021-01-24
 */
public interface Cache {

    String getName();

    String get(String key);

    String set(String key, String value, long expire);

}
