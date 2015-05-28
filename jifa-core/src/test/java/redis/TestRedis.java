package redis;

import com.jfinal.ext.plugin.redis.RedisKit;
import com.jfinal.ext.plugin.redis.RedisPlugin;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * 
 * june
 * 2015年3月12日 上午11:29:17
 */
public class TestRedis {
	
	@Test
	public void test(){
		new RedisPlugin().start();
		RedisKit.setString("abc", "123");
		assertEquals(RedisKit.getString("abc"), "123");
	}
}
