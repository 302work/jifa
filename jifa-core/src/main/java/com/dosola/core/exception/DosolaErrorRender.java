package com.dosola.core.exception;

import com.dosola.core.common.ReturnContext;
import com.jfinal.ext.render.exception.ExceptionRender;
import com.jfinal.render.RenderFactory;
import org.apache.commons.lang.StringUtils;

/**
 * 异常统一处理
 * june
 * 2015年3月17日 下午2:49:31
 */
public class DosolaErrorRender extends ExceptionRender {
	
	public DosolaErrorRender(){}
	
	public DosolaErrorRender(String view){
		this.view = view;
	}
	
	@Override
	public void render() {
		Exception e = this.getException();
		e.printStackTrace();
		String view = getView();
		if(StringUtils.isNotEmpty(view)){
			 RenderFactory.me().getRender(view).setContext(request, response).render();
			 return;
		}
//		new ErrorRender(500,null).render();
		ReturnContext rc = ReturnContext.init();
		rc.setResultMsg("程序内部错误，请稍后再试");
		RenderFactory.me().getJsonRender(rc).setContext(request, response).render();
	}

}
