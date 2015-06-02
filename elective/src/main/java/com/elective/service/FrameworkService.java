package com.elective.service;

import com.bstek.bdf2.core.business.IUser;
import com.bstek.bdf2.core.context.ContextHolder;
import com.bstek.bdf2.core.model.DefaultUser;
import com.bstek.bdf2.core.security.UserShaPasswordEncoder;
import com.bstek.bdf2.core.service.IDeptService;
import com.bstek.bdf2.core.service.IFrameworkService;
import com.bstek.bdf2.core.service.IGroupService;
import com.bstek.bdf2.core.service.IPositionService;
import com.bstek.dorado.core.Configure;
import com.elective.pojo.User;
import com.google.code.kaptcha.Constants;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.core.AuthenticationException;

import javax.annotation.Resource;

/**
 * 验证密码
 * @author june
 * 2015年06月02日 11:03
 */
public class FrameworkService implements IFrameworkService {

    @Resource(name=IDeptService.BEAN_ID)
    private IDeptService deptService;

    @Resource(name=IPositionService.BEAN_ID)
    private IPositionService positionService;

    @Resource(name=IGroupService.BEAN_ID)
    private IGroupService groupService;

    @Resource(name= UserShaPasswordEncoder.BEAN_ID)
    private PasswordEncoder passwordEncoder;

    public void authenticate(IUser iuser,UsernamePasswordAuthenticationToken authentication)
            throws AuthenticationException {
        this.preChecks(authentication);
        User user = (User)iuser;
        String presentedPassword = authentication.getCredentials().toString();
        if (!passwordEncoder.isPasswordValid(user.getPassword(), presentedPassword,user.getSalt())) {
            throw new BadCredentialsException("The password is invalid");
        }else{
            user.setDepts(deptService.loadUserDepts(user.getUsername()));
            user.setPositions(positionService.loadUserPositions(user.getUsername()));
            user.setGroups(groupService.loadUserGroups(user.getUsername()));
        }
    }

    private void preChecks(UsernamePasswordAuthenticationToken authentication)throws AuthenticationException{
        boolean useCaptcha= Configure.getBoolean("bdf2.useCaptchaForLogin");
        if(useCaptcha){
            String key= ContextHolder.getRequest().getParameter("captcha_");
            if(StringUtils.isNotEmpty(key)){
                String sessionkey=(String)ContextHolder.getHttpSession().getAttribute(Constants.KAPTCHA_SESSION_KEY);
                if(sessionkey==null){
                    throw new BadCredentialsException("验证码过期");
                }else if(!sessionkey.equals(key)){
                    throw new BadCredentialsException("验证码不正确");
                }
            }else{
                throw new BadCredentialsException("验证码不能为空");
            }
        }
        if (authentication.getPrincipal() == null) {
            throw new BadCredentialsException("Username can not be null");
        }
        if (authentication.getCredentials() == null) {
            throw new BadCredentialsException("password can not be null");
        }
    }

}
