/**
 * 
 */
package com.jfinal.ext.plugin.redis;

import com.jfinal.log.Logger;
import com.jfinal.plugin.IPlugin;
import org.apache.commons.lang.StringUtils;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedisPool;

import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Redis插件
 * @author june
 * 2015年3月2日 下午3:02:57
 */
public class RedisPlugin implements IPlugin {
	
	/**
	 * 集群连接池
	 */
	private static ShardedJedisPool shardedJedisPool;
	/**
	 * 配置文件路径
	 */
	private String configurationFileName = "redis";
	
	private final Logger logger = Logger.getLogger(getClass());
	
	public RedisPlugin(){}
	public RedisPlugin(String configurationFileName ){
		this.configurationFileName = configurationFileName;
	}
	
	@Override
	public boolean start() {
		createShardedJedisPool();
		RedisKit.init(shardedJedisPool);
		logger.info("初始化redis插件成功");
		return true;
	}

	
	/**
	 * 初始化连接池
	 */
	private void createShardedJedisPool() {
		ResourceBundle bundle = ResourceBundle.getBundle(configurationFileName);
		if (bundle == null) {
			throw new IllegalArgumentException(configurationFileName
					+ " is not found!");
		}
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxTotal(Integer.valueOf(bundle
				.getString("redis.pool.maxTotal")));
		config.setMaxIdle(Integer.valueOf(bundle
				.getString("redis.pool.maxIdle")));
		config.setMaxWaitMillis(Long.valueOf(bundle
				.getString("redis.pool.maxWait")));
		config.setTestOnBorrow(Boolean.valueOf(bundle
				.getString("redis.pool.testOnBorrow")));
		config.setTestOnReturn(Boolean.valueOf(bundle
				.getString("redis.pool.testOnReturn")));
		List<JedisShardInfo> list = createJedisShardInfoList(bundle);
		shardedJedisPool = new ShardedJedisPool(config, list);  
	}
	/**
	 * 构建redis集群配置信息
	 * @param bundle
	 * @return
	 */
	private List<JedisShardInfo> createJedisShardInfoList(ResourceBundle bundle){
		List<JedisShardInfo> list = new LinkedList<JedisShardInfo>(); 
		String defaultRedisPort = bundle.getString("redis.port");
		for(int i=0;i<10;i++){
			if(!bundle.containsKey("redis"+(i==0?"":i)+".ip")){
				continue;
			}
			String redisIp = bundle.getString("redis"+(i==0?"":i)+".ip");
			String redisPort = bundle.getString("redis"+(i==0?"":i)+".port");
			if(StringUtils.isEmpty(redisPort)){
				redisPort = defaultRedisPort;
			}
			if(StringUtils.isNotEmpty(redisIp) && StringUtils.isNotEmpty(redisPort)){
				JedisShardInfo jedisShardInfo = new JedisShardInfo(redisIp,redisPort);
				logger.info("初始化redis，redis"+(i==0?"":i)+".ip:"+redisIp+",端口："+redisPort);
				list.add(jedisShardInfo);
			}
		}
		return list;
	}
	@Override
	public boolean stop() {
		if(shardedJedisPool!=null){
			shardedJedisPool.destroy();
		}
		return true;
	}

}
