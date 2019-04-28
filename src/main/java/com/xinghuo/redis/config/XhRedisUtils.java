package com.xinghuo.redis.config;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/***************************************************************************
 * <PRE>
 *	
 *  className       : XhRedisUtils.java
 * 
 *  Description     : redis工具类, redis的string,list,set,zset增删改查基类 ，T是泛型，
 * 
 *  AUTHOR          : liquanfa
 * 
 *  Date   		    : 2018-04-23
 * 
 * </PRE>
 ***************************************************************************/
@Component
public class XhRedisUtils<T> {
	
	private Logger logger = LoggerFactory.getLogger(XhRedisUtils.class);
	
	@Autowired
	private RedisTemplate<String,T> redisTemplate;
	
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	
	/**
	 * 如果封装的方法无法满足自己的特需业务，可以获取RedisTemplate自己操作redis实现
	 * @return RedisTemplate
	 */
	public RedisTemplate<String, T> getTemplate() {
		return redisTemplate;
	}
	/*-----------------------------key相关操作 key-begin------------------------------------*/
	
	/**
	 * <p>
	 * 根据 key删除
	 * </p>
	 * @param key  缓存的key
	 */
	public void deleteByKey(String key){
		if(exist(key)){
			this.redisTemplate.delete(key);
		}
	}
	/**
	 * <p>
	 * 删除（根据ID 批量删除）
	 * </p>
	 * 
	 * @param keys  缓存的多个key集合
	 */
	public void deleteBatchKeys(Collection<String> keys){
		for(String key : keys){
			this.redisTemplate.delete(key);
		}
	}
	/**
	 * <p>
	 * 删除（根据patter查找key批量删除）
	 * </p>
	 *    ？通配任意多个字符
	 *    *通配任意多个字符
	 *    []通配[]内的某一个字符
	 * @param pattern   key的正则表达式
	 */
	public void deleteBatchByKeyPatter(String pattern){
		Set<String> keys = this.redisTemplate.keys(pattern);
		if(!CollectionUtils.isEmpty(keys)){
			this.redisTemplate.delete(keys);
		}
	}
	/**
	 * 判断缓存中是否存在key对应的value
	 * 
	 * @param key  缓存的key
	 * @return 存在返回true，不存在返回false
	 */
	public boolean exist(final String key){
		return this.redisTemplate.hasKey(key);
	}
	
	/**
	 * 设置过期时间
	 * @param key  缓存的key
	 * @param timeout 超时时长
	 * @param unit  时间单位，如：TimeUnit.SECONDS
	 * @return
	 */
	public boolean expire(String key,long timeout,TimeUnit unit){
		return this.redisTemplate.expire(key, timeout, unit);
	}
	
	/**
	 * 设置过期时间
	 * @param key  缓存的key
	 * @param timeout 超时时长
	 * @param unit  时间单位，如：TimeUnit.SECONDS
	 * @return
	 */
	public boolean expire(String key,long timeout){
		return this.redisTemplate.expire(key, timeout,TimeUnit.SECONDS);
	}
	
	/**
	 * 设置过期时间
	 * @param key  缓存的key
	 * @param date 过期时间
	 * @return
	 */
	public boolean expireAt(String key,Date date){
		return this.redisTemplate.expireAt(key, date);
	}
	
	/**
	 * 查找匹配的key
	 * @param pattern key的正则表达式
	 *    ？通配任意多个字符
	 *    *通配任意多个字符
	 *    []通配[]内的某一个字符
	 * @return  匹配pattern的key集合
	 */
	public Set<String> keys(String pattern){
		return this.redisTemplate.keys(pattern);
	}

	/**
	 * <p>
	 * 注意：集群环境下只有0库，所以集群环境下不支持该方法
	 * </p>
	 * 将当前数据库的key移动到指定数据库db中
	 * @param key  缓存的key
	 * @param dbIndex  数据库索引
	 * @return
	 */
	public boolean move(String key,int dbIndex){
		//TODOTODO
		return this.redisTemplate.move(key, dbIndex);
	}
	
	/**
	 * 移除key的过期时间
	 * @param key  缓存的key
	 * @return
	 */
	public boolean persist(String key){
		return this.redisTemplate.persist(key);
	}
	/**
	 * 返回key剩余的过期时间
	 * @param key  缓存的key
	 * @return  剩余的过期时间，单位是秒
	 */
	public Long getExpire(String key){
		return this.redisTemplate.getExpire(key);
	}
	
	/**
	 * 修改key的名称
	 * @param oldKey 缓存的旧key
	 * @param newKey  缓存的新key
	 */
	public void updateKey(String oldKey,String newKey){
		this.redisTemplate.rename(oldKey, newKey);
	}
	
	/**
	 * 仅当newKey不存在时，将oldKey修改为newKey
	 * @param oldKey 缓存的旧key
	 * @param newKey  缓存的新key
	 * @return
	 */
	public Boolean updateKeyIfAbsent(String oldKey,String newKey){
		return this.redisTemplate.renameIfAbsent(oldKey, newKey);
	}
	
	/**
	 * 返回key所存储的值的类型
	 * @param key  缓存的key
	 * @return key存储数据的类型
	 */
	public DataType getKeyType(String key){
		return this.redisTemplate.type(key);
	}
	/*-----------------------------key相关操作 key-end------------------------------------*/
	
	/*-----------------------------string相关操作 string-begin------------------------------------*/
	
	/**
	 * 写入缓存
	 * @param key  缓存的key
	 * @param obj 需要放入缓存中的数据T
	 * @return 成功返回true，失败返回false
	 */
	@SuppressWarnings("unchecked")
	public boolean set(String key, T obj){
		try{
			ValueOperations<String, T>  operations = (ValueOperations<String, T>) this.redisTemplate.opsForValue();
			operations.set(key, obj);
		} catch(Exception e){
			e.printStackTrace();
			return false;
		}
		return true;
	}
	/**
	 * 写入缓存
	 * @param key  缓存的key
	 * @param obj 需要放入缓存中的数据T
	 * @param expireTime 过期时长，单位是秒
	 * @return
	 */
	public boolean set(String key, T obj,Long expireTime){
		return this.set(key, obj, expireTime, TimeUnit.SECONDS);
	}
	/**
	 * 写入缓存
	 * @param key  缓存的key
	 * @param obj 需要放入缓存中的数据T
	 * @param expireTime  过期时长
	 * @param unit  时间单位，如：TimeUnit.SECONDS
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public boolean set(String key, T obj,Long expireTime,TimeUnit unit){
		try{
			ValueOperations<String, T>  operations = (ValueOperations<String, T>) this.redisTemplate.opsForValue();
			operations.set(key, obj);
			this.redisTemplate.expire(key, expireTime, unit);
		} catch(Exception e){
			return false;
		}
		return true;
	}
	/**
	 * 方法功能说明：  写入缓存,string 类型，object 类型
	 * 创建时间：2018年10月10日 上午11:39:48
	 * 开发者：胡佛传  
	 * @参数： @param key 缓存的key
	 * @参数： @param obj 需要放入缓存中的数据 string 或者json
	 * @参数： @return  
	 * @修改者:
	 * @修改时间：    
	 * @return boolean     
	 * @throws
	 */
	public boolean setObj(String key, T obj){
		try{
			this.redisTemplate.opsForValue().set(key, obj);
		} catch(Exception e){
			return false;
		}
		return true;
	}
	/**
	 * 
	 * 方法功能说明：  方法功能说明：  写入缓存,string 类型，object 类型
	 * 创建时间：2018年10月10日 上午11:42:48
	 * 开发者：胡佛传  
	 * @参数： @param key  缓存的key
	 * @参数： @param obj 需要放入缓存中的数据 string 或者json
	 * @参数： @param expireTime 过期时长
	 * @参数： @param unit 时间单位，如：TimeUnit.SECONDS
	 * @参数： @return  
	 * @修改者:
	 * @修改时间：    
	 * @return boolean     
	 * @throws
	 */
	public boolean setObj(String key, T obj,Long expireTime,TimeUnit unit){
		try{
			this.redisTemplate.opsForValue().set(key, obj);
			this.redisTemplate.expire(key, expireTime, unit);
		} catch(Exception e){
			return false;
		}
		return true;
	}
	
	/**
	 * 根据key读取缓存
	 * @param key  缓存的key
	 * @return 缓存中的元素T
	 */
	@SuppressWarnings("unchecked")
	public T get(final String key){
		T result = null;
		ValueOperations<String, T>  operations = (ValueOperations<String, T>) this.redisTemplate.opsForValue();
		result = operations.get(key);
		
		return result;
	}
	
	/**
	 * 方法功能说明：  获取key对应的value值
	 * 创建时间：2018年10月10日 上午11:57:09
	 * 开发者：胡佛传  
	 * @参数： @param key   缓存的key
	 * @参数： @return  string,json
	 * @修改者:
	 * @修改时间：    
	 * @return Object     
	 * @throws
	 */
	public T getObj(final String key){
		T result = null;
		result = this.redisTemplate.opsForValue().get(key);
		return result;
	}
	
	/**
	 * 只有key不存在时设置key的值
	 * @param key  缓存的key
	 * @param obj 需要放入缓存中的数据T
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Boolean setIfAbsent(String key,T obj){
		ValueOperations<String, T>  operations = (ValueOperations<String, T>) this.redisTemplate.opsForValue();
		return operations.setIfAbsent(key, obj);
	}
	/**
	 * 将给定key的值设为value，并返回key的旧值
	 * @param key 缓存的key
	 * @param value 需要设置的key的新值
	 * @return key对应的旧值
	 */
	@SuppressWarnings("unchecked")
	public T getAndSet(String key,T value){
		return (T) this.redisTemplate.opsForValue().getAndSet(key, value);
	}
	/**
	 * <p>
	 * 注意：使用时确保keys中所有key对应的value都是T的json序列化类型，所有key对应的value都是redis中的同一种数据类型
	 * </p>
	 * 通过redis key的集合，获取多个key对应的value值，以list形式返回
	 * @param keys redis缓存中的多个
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<T> multiGet(Collection<String> keys){
		ValueOperations<String, T>  operations = (ValueOperations<String, T>) this.redisTemplate.opsForValue();
		return (List<T>) operations.multiGet(keys);
	}
	/**
	 * 批量添加
	 * @param map 多个key-value的键值对，key为String，value为元素T类型
	 */
	public void multiSet(Map<String,T> map){
		this.redisTemplate.opsForValue().multiSet(map);
	}
	/**
	 * 方法功能说明：  批量添加
	 * 创建时间：2018年10月10日 上午11:58:45
	 * 开发者：胡佛传  
	 * @参数： @param map   多个key-value的键值对，key为String，value为元素obj类型
	 * @修改者:
	 * @修改时间：    
	 * @return void     
	 * @throws
	 */
	public void multiSetObj(Map<String,T> map){
		this.redisTemplate.opsForValue().multiSet(map);
	}
	
	/**
	 * 同时设置一个或多个key-value对，当且仅当给定的key都存在返回true
	 * @param map  多个key-value的键值对，key为String，value为元素T类型
	 */
	public Boolean multiSetIfAbsent(Map<String,T> map){
		return this.redisTemplate.opsForValue().multiSetIfAbsent(map);
	}
	/**
	 * 方法功能说明：  同时设置一个或多个key-value对，当且仅当给定的key都存在返回true
	 * 创建时间：2018年10月10日 上午11:59:53
	 * 开发者：胡佛传  
	 * @参数： @param map 多个key-value的键值对，key为String，value为元素obj类型
	 * @参数： @return  
	 * @修改者:
	 * @修改时间：    
	 * @return Boolean     
	 * @throws
	 */
	public Boolean multiSetIfAbsentObj(Map<String,T> map){
		return this.redisTemplate.opsForValue().multiSetIfAbsent(map);
	}
	/**
	 * 返回key中字符串的子串
	 * @param key  string缓存的key
	 * @param start 索引起始位置
	 * @param end   索引结束位置
	 * @return
	 */
	public String getRange(String key,long start,long end){
		return this.redisTemplate.opsForValue().get(key, start, end);
	}
	/**
	 * 将value参数覆盖给定key所储存的字符串。从偏移量offset开始
	 * @param key  string缓存的key
	 * @param value  string值
	 * @param offset 移量offset开始位置
	 */
	public void setRange(String key,T value,long offset){
		this.redisTemplate.opsForValue().set(key, value, offset);
	}
	
	
	
	/*-----------------------------string相关操作 string-end------------------------------------*/
	
	
	
	/*-----------------------------hash相关操作 string-begin------------------------------------*/
	
	
	/*-----------------------------hash相关操作 string-end------------------------------------*/
	
	
	
	/*-- redis list 添加一个元素到列表的头部（左边），尾部（右边）--------list相关操作 list-begin----------------*/
	/**
	 * 通过索引index获取list列表中的元素
	 * @param key  list缓存的key
	 * @param index  索引
	 * @return 索引位置的元素T
	 */
	@SuppressWarnings("unchecked")
	public T lIndex(String key,long index){
		ListOperations<String, T>  operations = (ListOperations<String, T>) this.redisTemplate.opsForList();
		return operations.index(key, index);
	}
	/**
	 * 获取list列表指定范围内的元素
	 * @param key  list缓存的key
	 * @param start 开始位置，0是开始位置
	 * @param end 结束位置，-1返回所有
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<T> lRange(String key,long start,long end){
		ListOperations<String, T>  operations = (ListOperations<String, T>) this.redisTemplate.opsForList();
		return operations.range(key, start, end);
	}
	/**
	 * 存储在list头部
	 * @param key  list缓存的key
	 * @param value 需要加入的元素T
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Long lLeftPush(String key,T value){
		ListOperations<String, T>  operations = (ListOperations<String, T>) this.redisTemplate.opsForList();
		return operations.leftPush(key, value);
	}
	
	/**
	 * 批量存储在list头部
	 * @param key    list缓存的key
	 * @param values   需要加入的多个元素T集合
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Long lLeftPushBatch(String key,Collection<T> values){
		ListOperations<String, T>  operations = (ListOperations<String, T>) this.redisTemplate.opsForList();
		return operations.leftPushAll(key, values);
	}
	
	/**
	 * 当list存在时存储在list头部
	 * @param key  list缓存的key
	 * @param value 需要加入的元素T
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Long lLeftPushIfPresent(String key,T value){
		ListOperations<String, T>  operations = (ListOperations<String, T>) this.redisTemplate.opsForList();
		return operations.leftPushIfPresent(key, value);
	}
	
	/**
	 * 当pivot存在时存储在pivot前面
	 * 
	 * @param key  list缓存的key
	 * @param pivot 元素
	 * @param value 需要加入的元素T
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Long lLeftPushIfPresent(String key,T pivot,T value){
		ListOperations<String, T>  operations = (ListOperations<String, T>) this.redisTemplate.opsForList();
		return operations.leftPush(key,pivot, value);
	}
	
	
	
	
	/**
	 * 存储在list尾部
	 * @param key  list缓存的key
	 * @param value 需要加入的元素T
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Long lRightPush(String key,T value){
		ListOperations<String, T>  operations = (ListOperations<String, T>) this.redisTemplate.opsForList();
		return operations.rightPush(key, value);
	}
	
	/**
	 * 批量存储在list尾部
	 * @param key    list缓存的key
	 * @param values   需要加入的多个元素T集合
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Long lRightPushBatch(String key,Collection<T> values){
		ListOperations<String, T>  operations = (ListOperations<String, T>) this.redisTemplate.opsForList();
		return operations.rightPushAll(key, values);
	}
	
	/**
	 * 当list存在时存储在list尾部
	 * @param key  list缓存的key
	 * @param value 需要加入的元素T
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Long lRightPushIfPresent(String key,T value){
		ListOperations<String, T>  operations = (ListOperations<String, T>) this.redisTemplate.opsForList();
		return operations.rightPushIfPresent(key, value);
	}
	
	/**
	 * 当pivot存在时存储在pivot后面
	 * 
	 * @param key  list缓存的key
	 * @param pivot 元素
	 * @param value 需要加入的元素T
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Long lRightPushIfPresent(String key,T pivot,T value){
		ListOperations<String, T>  operations = (ListOperations<String, T>) this.redisTemplate.opsForList();
		return operations.rightPush(key,pivot, value);
	}
	/**
	 * 通过索引设置list列表元素的值
	 * @param key    list缓存的key
	 * @param index  索引
	 * @param value  需要放入index位置的元素T
	 */
	@SuppressWarnings("unchecked")
	public void lSet(String key,long index,T value){
		ListOperations<String, T>  operations = (ListOperations<String, T>) this.redisTemplate.opsForList();
		operations.set(key, index, value);
	}
	/**
	 * 移除并获取列表的第一个元素
	 * @param key  list缓存的key
	 * @return 移除的元素T
	 */
	@SuppressWarnings("unchecked")
	public T lLefPop(String key){
		ListOperations<String, T>  operations = (ListOperations<String, T>) this.redisTemplate.opsForList();
		return operations.leftPop(key);
	}
	
	/**
	 * 移除并获取列表的第一个元素，如果列表没有元素会阻塞列表直到等待超时或发现可以弹出元素为止
	 * 
	 * @param key   list缓存的key
	 * @param timeout 超时时长
	 * @param unit  时间单位，如：TimeUnit.SECONDS
	 * @return  移除的元素T
	 */
	@SuppressWarnings("unchecked")
	public T lBlockLefPop(String key,long timeout,TimeUnit unit){
		ListOperations<String, T>  operations = (ListOperations<String, T>) this.redisTemplate.opsForList();
		return operations.leftPop(key,timeout,unit);
	}
	
	/**
	 * 移除并获取列表的最后一个元素
	 * @param key  list缓存的key
	 * @return  移除的元素T
	 */
	@SuppressWarnings("unchecked")
	public T lRightPop(String key){
		ListOperations<String, T>  operations = (ListOperations<String, T>) this.redisTemplate.opsForList();
		return operations.rightPop(key);
	}
	
	/**
	 * 移除并获取列表的最后一个元素，如果列表没有元素会阻塞列表直到等待超时或发现可以弹出元素为止
	 * 
	 * @param key list缓存的key
	 * @param timeout 超时时长
	 * @param unit  时间单位，如：TimeUnit.SECONDS
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public T lBlockRightPop(String key,long timeout,TimeUnit unit){
		ListOperations<String, T>  operations = (ListOperations<String, T>) this.redisTemplate.opsForList();
		return operations.rightPop(key,timeout,unit);
	}
	/**
	 * 移除sourceKey列表的最后一个元素,将该元素添加到另外一个列表destinationKey，并返回
	 * 
	 * @param sourceKey   list缓存的key
	 * @param destinationKey  list缓存的key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public T lRightPopAndLeftPush(String sourceKey,String destinationKey){
		ListOperations<String, T>  operations = (ListOperations<String, T>) this.redisTemplate.opsForList();
		return operations.rightPopAndLeftPush(sourceKey, destinationKey);
	}
	/**
	 * 移除sourceKey列表的最后一个元素,将该元素添加到另外一个列表destinationKey，并返回。如果列表没有元素会阻塞列表直到等待超时或发现可以弹出元素为止
	 * 
	 * @param sourceKey list缓存的key
	 * @param destinationKey  list缓存的key
	 * @param timeout 超时时长
	 * @param unit  时间单位，如：TimeUnit.SECONDS
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public T lRightPopAndLeftPush(String sourceKey,String destinationKey,long timeout,TimeUnit unit){
		ListOperations<String, T>  operations = (ListOperations<String, T>) this.redisTemplate.opsForList();
		return operations.rightPopAndLeftPush(sourceKey, destinationKey,timeout,unit);
	}
	
	/**
	 * <p>
	 * 删除list列表中值等于value的元素
	 * </p>
	 * 
	 * @param key  list缓存的key
	 * @param index  元素T的索引
	 * 	index=0,删除所有值等于value的元素
	 * 	index>0,从头部删除第一个值等于value的元素
	 *  index<0,从尾部删除第一个值等于value的元素
	 * @param value 需要删除的元素T
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Long lDelete(String key,long index,T value){
		ListOperations<String, T>  operations = (ListOperations<String, T>) this.redisTemplate.opsForList();
		return operations.remove(key, index,value);
	}
	
	/**
	 * 裁剪list
	 * @param key  list缓存的key
	 * @param start 裁剪的索引起始位置
	 * @param end   裁剪的索引终止位置
	 */
	@SuppressWarnings("unchecked")
	public void lCutList(String key,long start,long end){
		ListOperations<String, T>  operations = (ListOperations<String, T>) this.redisTemplate.opsForList();
		operations.trim(key, start, end);
	}
	/**
	 * 获取列表长度
	 * @param key list缓存的key
	 * @return 列表中元素数量
	 */
	public long lSize(String key){
		return this.redisTemplate.opsForList().size(key);
	}
	/*--redis list 添加一个元素到列表的头部（左边），尾部（右边）----list相关操作 list-end-------------------*/
	
	
	/*-----------------------------set相关操作 set-begin------------------------------------*/
	
	
	/**
	 * set添加元素
	 * @param key set缓存的key
	 * @param values 需要添加到集合总的元素T
	 */
	@SuppressWarnings("unchecked")
	public void sAdd(String key,T value){
		SetOperations<String, T>  operations = (SetOperations<String, T>) this.redisTemplate.opsForSet();
		operations.add(key, value);
	}
	/**
	 * 方法功能说明：  set添加元素
	 * 创建时间：2018年10月10日 下午12:02:12
	 * 开发者：胡佛传  
	 * @参数： @param key set缓存的key
	 * @参数： @param value  需要添加到集合总的元素obj
	 * @修改者:
	 * @修改时间：    
	 * @return void     
	 * @throws
	 */
	public void sAddObj(String key,T value){
		this.redisTemplate.opsForSet().add(key, value);
	}
	
	/**
	 * set添加元素
	 * @param key set缓存的key
	 * @param values 需要添加的元素T数组
	 */
	@SuppressWarnings("unchecked")
	public void sAdd(String key,T[] values){
		SetOperations<String, T>  operations = (SetOperations<String, T>) this.redisTemplate.opsForSet();
		operations.add(key, values);
	}
	
	/**
	 * set添加元素
	 * @param key  set缓存的key
	 * @param values  需要添加的元素T集合
	 */
	@SuppressWarnings("unchecked")
	public void sAdd(String key,Collection<T> values){
		T[] array = (T[]) values.toArray();
		SetOperations<String, T>  operations = (SetOperations<String, T>) this.redisTemplate.opsForSet();
		operations.add(key, array);
	}
	/**
	 * <p>
	 * 注意当set中的所有元素都被移除时，set也会被移除不存在
	 * </p>
	 * set移除元素
	 * @param key  set缓存的key
	 * @param values  需要移除的多个元素数组
	 */
	@SuppressWarnings("unchecked")
	public void sRemove(String key,T[] values){
		SetOperations<String, T>  operations = (SetOperations<String, T>) this.redisTemplate.opsForSet();
		operations.remove(key, values);
	}
	/**
	 * 从集合中随机移除一个元素
	 * @param key set缓存的key
	 * @return  从集合中移除的元素T
	 */
	@SuppressWarnings("unchecked")
	public T sPop(String key){
		SetOperations<String, T>  operations = (SetOperations<String, T>) this.redisTemplate.opsForSet();
		return operations.pop(key);
	}
	/**
	 * 将sourceKey列表中的value元素移动到另外一个列表destinationKey
	 * @param sourceKey set缓存的key
	 * @param destinationKey 目标set缓存的key
	 * @param value 需要移动的元素T
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Boolean sMove(String sourceKey,String destinationKey,T value){
		SetOperations<String, T>  operations = (SetOperations<String, T>) this.redisTemplate.opsForSet();
		return operations.move(sourceKey, value, destinationKey);
	}
	/**
	 * 获取set集合的大小
	 * @param key  set缓存的key
	 * @return 集合中元素的数量
	 */
	public Long sSize(String key){
		return this.redisTemplate.opsForSet().size(key);
	}
	/**
	 * 判断set集合中是否包含value
	 * @param key  set缓存的key
	 * @param value 需要判断是否存在的元素T
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Boolean sExistMember(String key,T value){
		SetOperations<String, T>  operations = (SetOperations<String, T>) this.redisTemplate.opsForSet();
		return operations.isMember(key, value);
	}
	/**
	 * 获取两个集合的交集
	 * @param key   set缓存的key
	 * @param otherKey  set缓存的key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Set<T> sIntersect(String key,String otherKey){
		SetOperations<String, T>  operations = (SetOperations<String, T>) this.redisTemplate.opsForSet();
		return operations.intersect(key, otherKey);
	}
	
	
	/**
	 * 获取key集合与多个集合的交集
	 * @param key   set缓存的key
	 * @param otherKeys  set缓存的多个key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Set<T> sIntersect(String key,Collection<String> otherKeys){
		SetOperations<String, T>  operations = (SetOperations<String, T>) this.redisTemplate.opsForSet();
		return operations.intersect(key, otherKeys);
	}
	
	/**
	 * key集合与otherKey集合的交集存储到destinationKey集合中
	 * @param key   set缓存的key
	 * @param otherKey  set缓存的key
	 * @param destinationKey 目标set集合的key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Long sIntersectAndStore(String key,String otherKey,String destinationKey){
		SetOperations<String, T>  operations = (SetOperations<String, T>) this.redisTemplate.opsForSet();
		return operations.intersectAndStore(key, otherKey, destinationKey);
	}
	
	/**
	 *  key集合与多个集合的交集存储到destinationKey集合中
	 * @param key   set缓存的key
	 * @param otherKeys  set缓存的多个key
	 * @param destinationKey 目标set集合的key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Long sIntersectAndStore(String key,Collection<String> otherKeys,String destinationKey){
		SetOperations<String, T>  operations = (SetOperations<String, T>) this.redisTemplate.opsForSet();
		return operations.intersectAndStore(key, otherKeys, destinationKey);
	}
	/**
	 * 获取两个集合的并集
	 * @param key   set缓存的key
	 * @param otherKey  set缓存的key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Set<T> sUnion(String key,String otherKey){
		SetOperations<String, T>  operations = (SetOperations<String, T>) this.redisTemplate.opsForSet();
		return operations.union(key, otherKey);
	}
	
	/**
	 * 获取key集合与多个集合的并集
	 * @param key   set缓存的key
	 * @param otherKeys  set缓存的多个key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Set<T> sUnion(String key,Collection<String> otherKeys){
		SetOperations<String, T>  operations = (SetOperations<String, T>) this.redisTemplate.opsForSet();
		return operations.union(key, otherKeys);
	}
	
	/**
	 * key集合与otherKey集合的并集存储到destinationKey集合中
	 * @param key   set缓存的key
	 * @param otherKey  set缓存的key
	 * @param destinationKey 目标set集合的key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Long sUnionAndStore(String key,String otherKey,String destinationKey){
		SetOperations<String, T>  operations = (SetOperations<String, T>) this.redisTemplate.opsForSet();
		return operations.unionAndStore(key, otherKey, destinationKey);
	}
	
	/**
	 * key集合与多个集合的并集存储到destinationKey集合中
	 * @param key   set缓存的key
	 * @param otherKeys  set缓存的多个key
	 * @param destinationKey 目标set集合的key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Long sUnionAndStore(String key,Collection<String> otherKeys,String destinationKey){
		SetOperations<String, T>  operations = (SetOperations<String, T>) this.redisTemplate.opsForSet();
		return operations.unionAndStore(key, otherKeys, destinationKey);
	}
	
	
	/**
	 * 获取两个集合的差集
	 * @param key   set缓存的key
	 * @param otherKey  set缓存的key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Set<T> sDifference(String key,String otherKey){
		SetOperations<String, T>  operations = (SetOperations<String, T>) this.redisTemplate.opsForSet();
		return operations.difference(key, otherKey);
	}
	
	/**
	 * 获取key集合与多个集合的差集
	 * @param key   set缓存的key
	 * @param otherKeys  set缓存的多个key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Set<T> sDifference(String key,Collection<String> otherKeys){
		SetOperations<String, T>  operations = (SetOperations<String, T>) this.redisTemplate.opsForSet();
		return operations.difference(key, otherKeys);
	}
	
	/**
	 * key集合与otherKey集合的差集存储到destinationKey集合中
	 * @param key   set缓存的key
	 * @param otherKey  set缓存的key
	 * @param destinationKey 目标set集合的key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Long sDifferenceAndStore(String key,String otherKey,String destinationKey){
		SetOperations<String, T>  operations = (SetOperations<String, T>) this.redisTemplate.opsForSet();
		return operations.differenceAndStore(key, otherKey, destinationKey);
	}
	
	/**
	 * key集合与多个集合的差集存储到destinationKey集合中
	 * @param key   set缓存的key
	 * @param otherKeys  set缓存的多个key
	 * @param destinationKey 目标set集合的key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Long sDifferenceAndStore(String key,Collection<String> otherKeys,String destinationKey){
		SetOperations<String, T>  operations = (SetOperations<String, T>) this.redisTemplate.opsForSet();
		return operations.differenceAndStore(key, otherKeys, destinationKey);
	}
	/**
	 * 获取集合中的所有元素
	 * @param key set缓存的key
	 * @return 所有的元素T集合
	 */
	@SuppressWarnings("unchecked")
	public Set<T> sObtainAllMember(String key){
		SetOperations<String, T>  operations = (SetOperations<String, T>) this.redisTemplate.opsForSet();
		return operations.members(key);
	}
	/**
	 * 随机获取集合中的一个元素
	 * @param key  set缓存的key
	 * @return 元素T
	 */
	@SuppressWarnings("unchecked")
	public T sRandomMember(String key){
		SetOperations<String, T>  operations = (SetOperations<String, T>) this.redisTemplate.opsForSet();
		return operations.randomMember(key);
	}
	
	/**
	 * 随机获取集合中的count个元素
	 * @param key  set缓存的key
	 * @param count  随机获取集合中的元素数量
	 * @return 元素T集合
	 */
	@SuppressWarnings("unchecked")
	public List<T> sRandomMembers(String key,long count){
		SetOperations<String, T>  operations = (SetOperations<String, T>) this.redisTemplate.opsForSet();
		return operations.randomMembers(key,count);
	}
	
	/**
	 * 随机获取集合中的count个元素并除掉重复的
	 * @param key  set缓存的key
	 * @param count  随机获取集合中的元素数量
	 * @return 元素T集合
	 */
	@SuppressWarnings("unchecked")
	public Set<T> sDistinctRandomMembers(String key,long count){
		SetOperations<String, T>  operations = (SetOperations<String, T>) this.redisTemplate.opsForSet();
		return operations.distinctRandomMembers(key,count);
	}
	
	/*-----------------------------set相关操作 set-end------------------------------------*/
	
	
	/*-----------------------------zset相关操作 zset-begin------------------------------------*/
	/**
	 * 添加元素。按照元素score值由小到大排序。如果元素已经存在就更新该元素的score值
	 * @param key  zset缓存的key
	 * @param value   要增加到zset集合中的元素
	 * @param score   要增加到zset集合中的元素的score
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Boolean zAdd(String key,T value,double score){
		ZSetOperations<String, T> operations = (ZSetOperations<String, T>) this.redisTemplate.opsForZSet();
		return operations.add(key, value, score);
	}
	/**
	 *  添加元素。自定义元素的比较排序规则
	 * @param key   zset缓存的key
	 * @param values  要增加到zset集合中的元素集合
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Long zAdd(String key,Set<TypedTuple<T>> values){
		ZSetOperations<String, T> operations = (ZSetOperations<String, T>) this.redisTemplate.opsForZSet();
		return operations.add(key, values);
	}
	/**
	 * 移除元素
	 * @param key  zset缓存的key
	 * @param values  需要移除的多个元素数组
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Long zRemove(String key,T[] values){
		ZSetOperations<String, T> operations = (ZSetOperations<String, T>) this.redisTemplate.opsForZSet();
		return operations.remove(key, values);
	}
	
	/**
	 * 移除元素
	 * @param key  zset缓存的key
	 * @param value 需要移除的元素
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Long zRemove(String key,T value){
		ZSetOperations<String, T> operations = (ZSetOperations<String, T>) this.redisTemplate.opsForZSet();
		return operations.remove(key, value);
	}
	
	/**
	 * 移除元素
	 * @param key  zset缓存的key
	 * @param values  需要移除的多个元素集合
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Long zRemove(String key,Collection<T> values){
		T[] array = (T[]) values.toArray();
		ZSetOperations<String, T> operations = (ZSetOperations<String, T>) this.redisTemplate.opsForZSet();
		return operations.remove(key, array);
	}
	
	/**
	 * 增加元素的score值，并返回增加后的值
	 * @param key  zset缓存的key
	 * @param value 需要增加score在zset中的元素
	 * @param delta  score的增加值
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Double zIncrementScore(String key,T value,double delta){
		ZSetOperations<String, T> operations = (ZSetOperations<String, T>) this.redisTemplate.opsForZSet();
		return operations.incrementScore(key, value, delta);
	}
	
	/**
	 * 返回value在集合中的排名。（按照元素score值由小到大排序）
	 * @param key  zset缓存的key
	 * @param value 需要获取在zset中排名的元素
	 * @return   value在集合中的排名
	 */
	@SuppressWarnings("unchecked")
	public Long zRank(String key,T value){
		ZSetOperations<String, T> operations = (ZSetOperations<String, T>) this.redisTemplate.opsForZSet();
		return operations.rank(key, value);
	}
	
	/**
	 * 返回value在集合中的排名。（按照元素score值由大到小排序）
	 * @param key  zset缓存的key
	 * @param value 需要获取在zset中排名的元素
	 * @return   value在集合中的排名
	 */
	@SuppressWarnings("unchecked")
	public Long zReverseRank(String key,T value){
		ZSetOperations<String, T> operations = (ZSetOperations<String, T>) this.redisTemplate.opsForZSet();
		return operations.reverseRank(key, value);
	}
	
	/**
	 * 获取集合中的元素，并且把score值也获取（按照元素score值由小到大排序）
	 * @param key  zset缓存的key
	 * @param start 开始位置
	 * @param end  结束位置，-1查询所有
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Set<TypedTuple<T>> zRangeWithScores(String key,long start,long end){
		ZSetOperations<String, T> operations = (ZSetOperations<String, T>) this.redisTemplate.opsForZSet();
		return operations.rangeWithScores(key, start, end);
	}
	
	/**
	 * 获取集合中的元素。（按照元素score值由小到大排序）
	 * @param key  zset缓存的key
	 * @param start 开始位置
	 * @param end  结束位置，-1查询所有
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Set<T> zRange(String key,long start,long end){
		ZSetOperations<String, T> operations = (ZSetOperations<String, T>) this.redisTemplate.opsForZSet();
		return operations.range(key, start, end);
	}
	
	/**
	 * 根据score值查询集合，并且把score值也获取（按照元素score值由小到大排序）
	 * @param key zset缓存的key
	 * @param min score的最小值
	 * @param max score的最大值
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Set<TypedTuple<T>> zRangeByScoreWithScores(String key,double min,double max){
		ZSetOperations<String, T> operations = (ZSetOperations<String, T>) this.redisTemplate.opsForZSet();
		return operations.rangeByScoreWithScores(key, min, max);
	}
	
	/**
	 * 根据score值查询得到集合temp，截取temp中索引起始位置start到索引结束位置end之间的所有元素，并且把score值也获取（按照元素score值由小到大排序）
	 * @param key zset缓存的key
	 * @param min score的最小值
	 * @param max score的最大值
	 * @param start 索引起始位置
	 * @param end 索引终止位置
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Set<TypedTuple<T>> zRangeByScoreWithScores(String key,double min,double max,long start,long end){
		ZSetOperations<String, T> operations = (ZSetOperations<String, T>) this.redisTemplate.opsForZSet();
		return operations.rangeByScoreWithScores(key, min, max,start,end);
	}
	/**
	 * 根据score值查询集合。（按照元素score值由小到大排序）
	 * @param key zset缓存的key
	 * @param min score的最小值
	 * @param max score的最大值
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Set<T> zRangeByScore(String key,double min,double max){
		ZSetOperations<String, T> operations = (ZSetOperations<String, T>) this.redisTemplate.opsForZSet();
		return operations.rangeByScore(key, min, max);
	}
	/**
	 * 获取集合中的元素，并且把score值也获取（按照元素score值由大到小排序）
	 * @param key zset缓存的key
	 * @param start 开始位置
	 * @param end  结束位置，-1查询所有
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Set<TypedTuple<T>> zReverseRangeWithScores(String key,long start,long end){
		ZSetOperations<String, T> operations = (ZSetOperations<String, T>) this.redisTemplate.opsForZSet();
		return operations.reverseRangeWithScores(key, start, end);
	}
	
	/**
	 * 获取集合中的元素。（按照元素score值由大到小排序）
	 * @param key zset缓存的key
	 * @param start 开始位置
	 * @param end  结束位置，-1查询所有
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Set<T> zReverseRange(String key,long start,long end){
		ZSetOperations<String, T> operations = (ZSetOperations<String, T>) this.redisTemplate.opsForZSet();
		return operations.reverseRange(key, start, end);
	}
	
	/**
	 * 根据score值查询集合，并且把score值也获取（按照元素score值由大到小排序）
	 * @param key zset缓存的key
	 * @param min score的最小值
	 * @param max score的最大值
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Set<TypedTuple<T>> zReverseRangeByScoreWithScores(String key,double min,double max){
		ZSetOperations<String, T> operations = (ZSetOperations<String, T>) this.redisTemplate.opsForZSet();
		return operations.reverseRangeByScoreWithScores(key, min, max);
	}
	
	/**
	 * 根据score值查询得到集合temp，截取temp中索引起始位置start到索引结束位置end之间的所有元素，并且把score值也获取（按照元素score值由大到小排序）
	 * @param key zset缓存的key
	 * @param min score的最小值
	 * @param max score的最大值
	 * @param start 索引起始位置
	 * @param end 索引终止位置
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Set<TypedTuple<T>> zReverseRangeByScoreWithScores(String key,double min,double max,long start,long end){
		ZSetOperations<String, T> operations = (ZSetOperations<String, T>) this.redisTemplate.opsForZSet();
		return operations.reverseRangeByScoreWithScores(key, min, max,start,end);
	}
	/**
	 * 根据score值查询集合。（按照元素score值由大到小排序）
	 * @param key  zset缓存的key
	 * @param min score的最小值
	 * @param max score的最大值
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Set<T> zReverseRangeByScore(String key,double min,double max){
		ZSetOperations<String, T> operations = (ZSetOperations<String, T>) this.redisTemplate.opsForZSet();
		return operations.reverseRangeByScore(key, min, max);
	}
	
	/**
	 * 获取集中元素的score的值，score>=min并且score<=max的集合中元素数量
	 * @param key zset缓存的key
	 * @param min score的最小值
	 * @param max score的最大值
	 * @return 符合条件的元素数量
	 */
	@SuppressWarnings("unchecked")
	public Long zCount(String key,double min,double max){
		ZSetOperations<String, T> operations = (ZSetOperations<String, T>) this.redisTemplate.opsForZSet();
		return operations.count(key, min, max);
	}
	/**
	 * 获取集合大小
	 * @param key zset缓存的key
	 * @return zset缓存的key对应的集合大小
	 */
	public Long zSize(String key){
		return this.redisTemplate.opsForZSet().size(key);
	}
	
	/**
	 * 获取集合大小
	 * @param key zset缓存的key
	 * @return zset缓存的key对应的集合大小
	 */
	public Long zCard(String key){
		return this.redisTemplate.opsForZSet().zCard(key);
	}
	/**
	 * 获取集合中value元素的score值
	 * @param key  zset缓存的key
	 * @param value 集合中的
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Double zScore(String key,T value){
		ZSetOperations<String, T> operations = (ZSetOperations<String, T>) this.redisTemplate.opsForZSet();
		return operations.score(key, value);
	}
	/**
	 * 移除指定索引位置的成员
	 * @param key  zset缓存的key
	 * @param start 索引起始位置
	 * @param end 索引终止位置
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Long zRemoveRange(String key,long start,long end){
		ZSetOperations<String, T> operations = (ZSetOperations<String, T>) this.redisTemplate.opsForZSet();
		return operations.removeRange(key, start, end);
	}
	/**
	 * 移除指定score值范围的成员
	 * 
	 * @param key  zset缓存的key
	 * @param min score的最小值
	 * @param max score的最大值
	 * @return 
	 */
	@SuppressWarnings("unchecked")
	public Long zRemoveRangeByScore(String key,double min,double max){
		ZSetOperations<String, T> operations = (ZSetOperations<String, T>) this.redisTemplate.opsForZSet();
		return operations.removeRangeByScore(key, min, max);
	}

	/**
	 * <p>
	 * 注意：集群环境下要求所有的key都在同一个slot上
	 * </p>
	 * key集合与otherKey集合的交集存储到destinationKey集合中
	 * @param key   zset缓存的key
	 * @param otherKey  zset缓存的key
	 * @param destinationKey 目标zset集合的key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Long zIntersectAndStore(String key, String otherKey,
			String destinationKey) { 
		//TODOTODO  ZINTERSTORE can only be executed when all keys map to the same slot
		ZSetOperations<String, T> operations = (ZSetOperations<String, T>) this.redisTemplate
				.opsForZSet();
		return operations.intersectAndStore(key, otherKey, destinationKey);
	}

	/**
	 * <p>
	 * 注意：集群环境下要求所有的key都在同一个slot上
	 * </p>
	 * key集合与多个集合的交集存储到destinationKey集合中
	 * @param key   zset缓存的key
	 * @param otherKeys  zset缓存的多个key
	 * @param destinationKey 目标zset集合的key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Long zIntersectAndStore(String key, Collection<String> otherKeys,
			String destinationKey) {
		ZSetOperations<String, T> operations = (ZSetOperations<String, T>) this.redisTemplate
				.opsForZSet();
		return operations.intersectAndStore(key, otherKeys, destinationKey);
	}

	/**
	 * <p>
	 * 注意：集群环境下要求所有的key都在同一个slot上
	 * </p>
	 * key集合与otherKey集合的并集存储到destinationKey集合中
	 * @param key   zset缓存的key
	 * @param otherKey  zset缓存的key
	 * @param destinationKey 目标zset集合的key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Long zUnionAndStore(String key, String otherKey,
			String destinationKey) {
		ZSetOperations<String, T> operations = (ZSetOperations<String, T>) this.redisTemplate
				.opsForZSet();
		//ZUNIONSTORE can only be executed when all keys map to the same slot
		return operations.unionAndStore(key, otherKey, destinationKey);
	}

	/**
	 * <p>
	 * 注意：集群环境下要求所有的key都在同一个slot上
	 * </p>
	 * key集合与多个集合的并集存储到destinationKey集合中
	 * @param key   zset缓存的key
	 * @param otherKeys  zset缓存的多个key
	 * @param destinationKey 目标zset集合的key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Long zUnionAndStore(String key, Collection<String> otherKeys,
			String destinationKey) {
		ZSetOperations<String, T> operations = (ZSetOperations<String, T>) this.redisTemplate
				.opsForZSet();
		return operations.unionAndStore(key, otherKeys, destinationKey);
	}
	
	
	/*-----------------------------zset相关操作 zset-end------------------------------------*/
	
	/**
	 * redis分布式锁加锁方法
	 * 
	 * @param key
	 *            加锁键全局唯一
	 * @param value
	 *            加锁值为当前时间+超时时间(时间戳格式)
	 * 
	 * @return boolean false-获取锁失败 true-获取锁成功
	 */
	public boolean lock(String key, Long value) {
		try {
			if (stringRedisTemplate.opsForValue().setIfAbsent(key, value + "")) {
				return true;
			}
			// 避免死锁，且只让一个线程拿到锁
			String currentValue = stringRedisTemplate.opsForValue().get(key);
			// 如果锁过期了
			if (!StringUtils.isEmpty(currentValue)
					&& Long.parseLong(currentValue) < System
							.currentTimeMillis()) {
				// 只会让一个线程拿到锁，如果旧的value和currentValue相等，只会有一个线程达成条件，因为第二个线程拿到的oldValue已经和currentValue不一样了
				String oldValue = stringRedisTemplate.opsForValue().getAndSet(key,
						value + "");
				if (!StringUtils.isEmpty(oldValue)
						&& oldValue.equals(currentValue)) {
					return true;
				}
			}
		} catch (Exception e) {
			logger.error("【redis分布式锁】加锁异常, {}", e);
		}
		return false;
	}

	/**
	 * redis分布式锁解锁方法
	 * 
	 * @param key
	 *            解锁键全局唯一
	 * @param value
	 *            解锁值为超时时间(时间戳格式)
	 * 
	 * @return boolean false-解锁失败 true-解锁成功
	 */
	public void unlock(String key, Long value) {
		try {
			String currentValue = stringRedisTemplate.opsForValue().get(key);
			if (!StringUtils.isEmpty(currentValue) && value != null) {
				if (value <= Long.parseLong(currentValue)) {
					redisTemplate.opsForValue().getOperations().delete(key);
				}
			}
		} catch (Exception e) {
			logger.error("【redis分布式锁】解锁异常, {}", e);
		}
	}
}
