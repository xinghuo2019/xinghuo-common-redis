spring boot 集成 swagger 封装公共组件

1、基于spring boot 2.0.7.RELEASE 版本，spring cloud Finchley.SR2版本

2、在Java spring boot项目pom.xml中引入星火插件，如下
</br>

支持单节点/集群	

</br>
3、配置application.properties 属性文件</br>
	在application.properties 文件中加入如下配置</br>
	
	字段名称								是否必填		字段描述					
	#单节点配置
	spring.redis.host						是				单节点服务器地址			
	spring.redis.port						是				服务器端口	
	spring.redis.database					是				Redis数据库索引（默认为0）	
	#集群配置
	spring.redis.cluster.nodes				是				集群服务器地址				
	#公共配置
	spring.redis.password					否				数据库密码					
	spring.redis.timeout					否				连接池超时时间	
	spring.redis.lettuce.pool.max-active	否				连接池最大连接数(使用负值表示没限制)	
	spring.redis.lettuce.pool.max-wait		否				连接池最大阻塞等待时间(使用负值表示没限制)	
	spring.redis.lettuce.pool.max-idle		否				连接池中的最大空闲连接	
	spring.redis.lettuce.pool.min-idle		否				连接池中的最小空闲连接	

4、在业务使用类中注入XhRedisUtils<Object>工具类对象
示例如下：
package com.xinghuo.redis.dao;
import org.springframework.stereotype.Repository;
import com.xinghuo.Student;
@RestController
@RequestMapping("")
public class XhDemoController {
	@Autowired
	XhRedisUtils<Object> objectRedisDao;	
	@Autowired
	XhRedisUtils<Student> studentRedisDao;
}
5、redis分布式锁
	/**
	 * redis分布式锁加锁方法 
	 * @param key加锁键全局唯一
	 * @param value加锁值为当前时间+超时时间(时间戳格式)
	 * @return boolean false-获取锁失败 true-获取锁成功
	 */
	public boolean lock(String key, Long value)
	
	/**
	 * redis分布式锁解锁方法
	 * @param key解锁键全局唯一
	 * @param value 解锁值为超时时间(时间戳格式)
	 * @return boolean false-解锁失败 true-解锁成功
	 */
	public void unlock(String key, Long value)
	
6、可以直接注入RedisTemplate对象
	如下：
	@Autowired
	private RedisTemplate<String,T> redisTemplate;
	
	@Autowired
	private StringRedisTemplate stringRedisTemplate;



