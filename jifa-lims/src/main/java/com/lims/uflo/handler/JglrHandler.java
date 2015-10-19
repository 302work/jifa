package com.lims.uflo.handler;

import org.springframework.stereotype.Service;

import com.lims.uflo.handler.base.AbstractHandler;


/**
 * 结果录入
 * @author june
 * @date 2015-10-19 下午11:19:09
 *
 */
@Service
public class JglrHandler extends AbstractHandler{

	@Override
	protected String getRoleId() {
		return "8a86373e-ab1f-4d0b-a3c6-fea37c01f70e";
	}

}
