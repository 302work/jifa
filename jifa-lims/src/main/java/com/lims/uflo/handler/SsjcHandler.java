package com.lims.uflo.handler;

import org.springframework.stereotype.Service;

import com.lims.uflo.handler.base.AbstractHandler;


/**
 * 申请检测
 * @author june
 * @date 2015-10-19 下午11:05:22
 *
 */
@Service
public class SsjcHandler extends AbstractHandler{

	@Override
	protected String getRoleId() {
		return "500445e0-1e1b-4082-a52e-f09786e9da3c";
	}

}
