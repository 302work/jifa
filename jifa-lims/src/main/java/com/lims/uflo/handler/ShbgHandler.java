package com.lims.uflo.handler;

import org.springframework.stereotype.Service;

import com.lims.uflo.handler.base.AbstractHandler;

/**
 * 生成报告
 * @author june
 * @date 2015-10-19 下午11:23:34
 *
 */
@Service
public class ShbgHandler extends AbstractHandler{

	@Override
	protected String getRoleId() {
		return "ca2b374d-e4b7-4ac9-8669-83cc318a250b";
	}

}
