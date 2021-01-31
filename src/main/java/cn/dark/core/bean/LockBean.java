package cn.dark.core.bean;

import cn.dark.core.annotation.Lock;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 对应 {@link Lock} 的实体类
 *
 * @author dark
 * @date 2021-01-24
 */
@Data
@Accessors(chain = true)
public class LockBean {

    /**
     * @see Lock#lockType()
     */
    private String lockType;

    /**
     * @see Lock#keyPrefix()
     */
    private String keyPreffix;

    /**
     * @see Lock#key()
     */
    private String key;

    /**
     * @see Lock#expire()
     */
    private long expire;

}
