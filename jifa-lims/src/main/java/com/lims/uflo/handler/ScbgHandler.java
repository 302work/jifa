package com.lims.uflo.handler;

import com.lims.uflo.handler.base.AbstractHandler;
import org.springframework.stereotype.Service;

/**
 * 生成报告
 * @author june
 * @date 2015-10-19 下午11:33:24
 *
 */
@Service
public class ScbgHandler extends AbstractHandler{

	@Override
	protected String getRoleId() {
		return "ca2b374d-e4b7-4ac9-8669-83cc318a250b ";
	}

}
