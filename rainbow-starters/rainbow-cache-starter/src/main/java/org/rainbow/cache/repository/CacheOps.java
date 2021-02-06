package org.rainbow.cache.repository;

import org.rainbow.core.cache.CacheHashKey;
import org.rainbow.core.cache.CacheKey;
import org.springframework.lang.NonNull;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * @author K
 * @date 2021/2/5  11:14
 */
public interface CacheOps {
    /**
     * 删除key
     *
     * @param keys {@link org.rainbow.core.cache.CacheKey}
     * @return 被删除的keys的数量
     */
    Long del(@NonNull CacheKey... keys);

    /**
     * 删除key
     *
     * @param keys 键
     * @return 被删除的keys的数量
     */
    Long del(@NonNull String... keys);

    /**
     * 判断是否存在key
     *
     * @param key 键
     * @return true/false
     */
    Boolean exists(@NonNull CacheKey key);

    /**
     * 设置键值
     *
     * @param key             键
     * @param value           值
     * @param cacheNullValues 是否缓存空值
     */
    void set(@NonNull CacheKey key, Object value, boolean... cacheNullValues);

    /**
     * 根据key获取value
     *
     * @param key             键
     * @param cacheNullValues 如果值为空，是否用{@link org.rainbow.cache.redis.NullVal}填充
     * @param <T>             对应类型
     * @return 值
     */
    <T> T get(@NonNull CacheKey key, boolean... cacheNullValues);

    /**
     * 根据key获取value
     *
     * @param key             键
     * @param cacheNullValues 如果值为空，是否用{@link org.rainbow.cache.redis.NullVal}填充
     * @param <T>             对应类型
     * @return 值
     */
    <T> T get(@NonNull String key, boolean... cacheNullValues);

    /**
     * 根据key list 获取 List<T>
     *
     * @param cacheKeys 键
     * @param <T>       对应类型
     * @return 值
     */
    <T> List<T> find(@NonNull Collection<CacheKey> cacheKeys);

    /**
     * 根据key获取value
     *
     * @param cacheKey        键
     * @param function        处理方法
     * @param cacheNullValues 如果值为空，是否用{@link org.rainbow.cache.redis.NullVal}填充
     * @param <T>             对应类型
     * @return 值
     */
    <T> T get(@NonNull CacheKey cacheKey, Function<CacheKey, ? extends T> function, boolean... cacheNullValues);

    /**
     * 删除数据库中的所有键
     */
    void flushDb();

    /**
     * 对对应key的值进行递增操作
     *
     * @param cacheKey key
     * @return 执行递增操作后key对应的值
     */
    Long incr(@NonNull CacheKey cacheKey);

    /**
     * 对对应key的值进行递增操作 指定步长
     *
     * @param cacheKey  key
     * @param increment 步长
     * @return 执行递增操作后key对应的值
     */
    Long incrBy(@NonNull CacheKey cacheKey, long increment);

    /**
     * 对对应key的值进行递增操作 指定步长
     *
     * @param key       key
     * @param increment 步长
     * @return 执行递增操作后key对应的值
     */
    Double incrByFloat(@NonNull CacheKey key, double increment);

    /**
     * 递减
     *
     * @param cacheKey key
     * @return 执行递减操作后key对应的值
     */
    Long decr(@NonNull CacheKey cacheKey);

    /**
     * 对对应的值执行递减操作
     *
     * @param cacheKey  key
     * @param decrement 递减步长
     * @return 执行递减操作后key对应的值
     */
    Long decrBy(@NonNull CacheKey cacheKey, long decrement);

    /**
     * 根据key获取set里的值
     *
     * @param key 键
     * @return set里面的值
     */
    Set<String> keys(@NonNull String key);

    /**
     * 给键设置过期时间
     *
     * @param cacheKey 键
     * @return true/false
     */
    Boolean expire(@NonNull CacheKey cacheKey);

    /**
     * 持久化键值
     *
     * @param cacheKey 键
     * @return true/false
     */
    Boolean persist(@NonNull CacheKey cacheKey);

    /**
     * 返回key所存储的value的数据结构类型
     *
     * @param cacheKey 键
     * @return 数据结构类型
     */
    String type(@NonNull CacheKey cacheKey);

    /**
     * 以毫秒为单位返回 key 的剩余生存时间
     *
     * @param cacheKey 键
     * @return 秒数
     */
    Long ttl(@NonNull CacheKey cacheKey);

    /**
     * 以毫秒为单位返回 key 的剩余生存时间
     *
     * @param cacheKey 键
     * @return 毫秒数
     */
    Long pTtl(@NonNull CacheKey cacheKey);

    /**
     * 设置 key 指定的哈希集中指定字段的值
     *
     * @param cacheHashKey    键
     * @param value           值
     * @param cacheNullValues 如果值为空，是否用{@link org.rainbow.cache.redis.NullVal}填充
     */
    void hSet(@NonNull CacheHashKey cacheHashKey, Object value, boolean... cacheNullValues);

    /**
     * 返回 key 指定的哈希集中该字段所关联的值
     *
     * @param cacheHashKey   键
     * @param cacheNullValue 如果值为空，是否用{@link org.rainbow.cache.redis.NullVal}填充
     * @param <T>            对应的类型
     * @return 值
     */
    <T> T hGet(@NonNull CacheHashKey cacheHashKey, boolean... cacheNullValues);

    /**
     * 返回 key 指定的哈希集中该字段所关联的值
     *
     * @param cacheHashKey   键
     * @param function       处理方法
     * @param cacheNullValue 如果值为空，是否用{@link org.rainbow.cache.redis.NullVal}填充
     * @param <T>            对应的类型
     * @return 值
     */
    <T> T hGet(@NonNull CacheHashKey cacheHashKey, Function<CacheHashKey, T> function, boolean... cacheNullValue);

    /**
     * 返回hash里面field是否存在
     *
     * @param cacheHashKey 键
     * @return true/false
     */
    Boolean hExists(@NonNull CacheHashKey cacheHashKey);

    /**
     * 从 key 指定的哈希集中移除指定的域。在哈希集中不存在的域将被忽略。
     *
     * @param key    键
     * @param values 要移除的值
     * @return 返回从哈希集中成功移除的域的数量，不包括指出但不存在的那些域
     */
    Long hDel(@NonNull String key, Object... values);

    /**
     * 根据key删除哈希集
     *
     * @param cacheHashKey 键
     * @return 返回从哈希集中成功移除的域的数量
     */
    Long hDel(@NonNull CacheHashKey cacheHashKey);

    /**
     * 获取key对应的哈希集对应的长度
     *
     * @param cacheHashKey 键
     * @return 对应的哈希集对应的长度
     */
    Long hLen(@NonNull CacheHashKey cacheHashKey);

    /**
     * 增加 key 指定的哈希集中指定字段的数值。
     * 如果 key 不存在，会创建一个新的哈希集并与 key 关联。如果字段不存在，则字段的值在该操作执行前被设置为 0
     *
     * @param cacheHashKey 键和指定的field
     * @param increment    步长
     * @return 增值操作执行后的该字段的值
     */
    Long hIncrBy(@NonNull CacheHashKey cacheHashKey, long increment);

    /**
     * 增加 key 指定的哈希集中指定字段的数值。
     * 如果 key 不存在，会创建一个新的哈希集并与 key 关联。如果字段不存在，则字段的值在该操作执行前被设置为 0
     *
     * @param cacheHashKey 键和指定的field
     * @param increment    步长
     * @return 增值操作执行后的该字段的值
     */
    Double hIncrBy(@NonNull CacheHashKey cacheHashKey, double increment);

    /**
     * 获取 key 对应的哈希集中所有的键
     *
     * @param cacheHashKey key
     * @return 哈希集中所有的键
     */
    Set<Object> hKeys(@NonNull CacheHashKey cacheHashKey);

    /**
     * 获取 key 对应的哈希集中所有的值
     *
     * @param cacheHashKey key
     * @return 哈希集中所有的值
     */
    List<Object> hVals(@NonNull CacheHashKey cacheHashKey);

    /**
     * 获取 key 对应的哈希集中所有的键值对
     *
     * @param cacheHashKey key
     * @return 哈希集中所有键值对
     */
    Map<Object, Object> hGetAll(@NonNull CacheHashKey cacheHashKey);

    /**
     * 添加一个或多个指定的member元素到集合的 key中
     * 指定的一个或者多个元素member 如果已经在集合key中存在则忽略.如果集合key 不存在，则新建集合key,并添加member元素到集合key中.
     *
     * @param cacheKey 键
     * @param val      值
     * @return 返回新成功添加到集合里元素的数量，不包括已经存在于集合中的元素.
     */
    Long sAdd(@NonNull CacheKey cacheKey, Object val);

    /**
     * 在key集合中移除指定的元素. 如果指定的元素不是key集合中的元素则忽略 如果key集合不存在则被视为一个空的集合，该命令返回0.
     * 如果key的类型不是一个集合,则返回错误
     *
     * @param cacheKey 键
     * @param values   要移除的值
     * @return 从集合中移除元素的个数，不包括不存在的成员
     */
    Long sRem(@NonNull CacheKey cacheKey, Object... values);

    /**
     * 返回key集合所有的元素
     *
     * @param cacheKey 键
     * @return 集合中所有的元素
     */
    Set<Object> sMembers(@NonNull CacheKey cacheKey);

    /**
     * 从存储在key的集合中移除并返回一个或多个随机元素
     *
     * @param cacheKey 键
     * @param <T>      对应的类型
     * @return 被删除的元素
     */
    <T> T sPop(@NonNull CacheKey cacheKey);

    /**
     * 返回集合存储的key的基数 (集合元素的数量).
     *
     * @param cacheKey 键
     * @return 集合元素的数量
     */
    Long sCard(@NonNull CacheKey cacheKey);
}

