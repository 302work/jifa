/**
 * 
 */
package com.dosola.core.annotation;

import java.lang.annotation.*;

/**
 * 配置事务
 * @author june
 * 2014年12月1日 下午10:52:25
 * 
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface DosolaTx {
	String value() default "";
}
