package com.lims.uflo.handler;

import org.springframework.stereotype.Service;

import com.lims.uflo.handler.base.AbstractHandler;

/**
 * 审核报告
 * @author june
 * @date 2015-10-19 下午11:33:24
 *
 */
@Service
public class ScbgHandler extends AbstractHandler{

	@Override
	protected String getRoleId() {
		return "83a0a644-44ed-4920-a27a-99556ea56121";
	}

}
