package com.lims.uflo.handler;

import org.springframework.stereotype.Service;

import com.lims.uflo.handler.base.AbstractHandler;

/**
 * 审核检测单
 * @author june
 * @date 2015-10-19 下午11:15:27
 *
 */
@Service
public class ShjcdHandler extends AbstractHandler{

	@Override
	protected String getRoleId() {
		return "78550a81-bac3-4cf3-8fee-12f1ffabf196";
	}

}
