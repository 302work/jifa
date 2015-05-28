package com.dosola.core.config;


import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.wall.WallFilter;
import com.dosola.core.exception.DosolaErrorRender;
import com.jfinal.config.*;
import com.jfinal.core.JFinal;
import com.jfinal.ext.handler.UrlSkipHandler;
import com.jfinal.ext.interceptor.ExceptionInterceptor;
import com.jfinal.ext.plugin.tablebind.AutoTableBindPlugin;
import com.jfinal.ext.route.AutoBindRoutes;
import com.jfinal.plugin.druid.DruidPlugin;

/**
 * 系统配置
 * @author june
 * 2014-11-26
 */
public class AppConfig extends JFinalConfig {
	
	/**
	 * 配置常量
	 */
	public void configConstant(Constants me) {
		// 加载少量必要配置，随后可用getProperty(...)获取值
		loadPropertyFile("application.properties");
		me.setDevMode(getPropertyToBoolean("devMode", true));
	}
	
	/**
	 * 配置路由
	 */
	public void configRoute(Routes me) {
		AutoBindRoutes ab = new AutoBindRoutes();
		ab.autoScan(false);
		me.add(ab);
	}
	
	/**
	 * 配置插件
	 */
	public void configPlugin(Plugins me) {
		// 配置druid数据库连接池插件
		DruidPlugin dp = new DruidPlugin(getProperty("jdbc.url"), getProperty("jdbc.name"), getProperty("jdbc.password"));
		dp.setMaxActive(200);
		dp.setRemoveAbandoned(true);
		dp.setLogAbandoned(true);
		dp.setMaxWait(60000);
		StatFilter filter = new StatFilter();
		filter.setSlowSqlMillis(1000);
//		filter.setMergeSql(true);
		dp.addFilter(filter);
		WallFilter wall = new WallFilter(); 
		wall.setDbType("mysql"); 
//		dp.addFilter(wall);
		me.add(dp);
		
		//配置表映射
		AutoTableBindPlugin atbp =  new AutoTableBindPlugin(dp);
		atbp.setShowSql(true);
		atbp.autoScan(false);
		me.add(atbp);
		
		//配置redis插件
//		RedisPlugin redisPlugin = new RedisPlugin();
//		me.add(redisPlugin);
		
	}
	
	/**
	 * 配置全局拦截器
	 */
	public void configInterceptor(Interceptors me) {
		ExceptionInterceptor exceptionInterceptor = new ExceptionInterceptor();
//		exceptionInterceptor.addMapping(IllegalArgumentException.class, "/exceptions/a.html");
//		exceptionInterceptor.addMapping(IllegalStateException.class, "exceptions/b.html");
		exceptionInterceptor.setDefault(new DosolaErrorRender());
		me.add(exceptionInterceptor);
	}
	
	/**
	 * 配置处理器
	 */
	public void configHandler(Handlers me) {
		//不处理dorado的请求
		me.add(new UrlSkipHandler("/dorado/.*", false));
	}
	
	/**
	 * 建议使用 JFinal 手册推荐的方式启动项目
	 * 运行此 main 方法可以启动项目，此main方法可以放置在任意的Class类定义中，不一定要放于此
	 */
	public static void main(String[] args) {
		JFinal.start("src/main/webapp", 8080, "/", 5);
	}
}
