package com.lims.controller;

import com.dosola.core.controller.BaseController;
import com.jfinal.ext.route.ControllerBind;

/**
 * 默认页面
 * @author june
 * 2015年11月05日 14:13
 */
@ControllerBind(controllerKey = "/")
public class IndexController extends BaseController {
    public void index () {
        renderJsp("index.jsp");
    }
}
