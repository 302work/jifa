package com.lims.uflo.handler;

import com.lims.uflo.handler.base.AbstractHandler;
import org.springframework.stereotype.Service;


/**
 * 申请检测
 * @author june
 * @date 2015-10-19 下午11:05:22
 *
 */
@Service
public class SqjcdHandler extends AbstractHandler{

	@Override
	protected String getRoleId() {
		return "500445e0-1e1b-4082-a52e-f09786e9da3c";
	}

}
