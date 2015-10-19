package com.lims.uflo.handler;

import org.springframework.stereotype.Service;

import com.lims.uflo.handler.base.AbstractHandler;

/**
 * 结果审核
 * @author june
 * @date 2015-10-19 下午11:22:39
 *
 */
@Service
public class JgshHandler extends AbstractHandler{

	@Override
	protected String getRoleId() {
		return "adbdbe3c-050b-4da4-9559-cae92f20cccb";
	}

}