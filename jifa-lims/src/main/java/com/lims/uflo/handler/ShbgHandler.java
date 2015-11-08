package com.lims.uflo.handler;

import com.lims.uflo.handler.base.AbstractHandler;
import org.springframework.stereotype.Service;

/**
 * 审核报告
 * @author june
 * @date 2015-10-19 下午11:23:34
 *
 */
@Service
public class ShbgHandler extends AbstractHandler{

	@Override
	protected String getRoleId() {
		return "83a0a644-44ed-4920-a27a-99556ea56121";
	}

}
