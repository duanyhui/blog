package com.duan.util;


import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.duan.shiro.AccountProfile;
import org.apache.shiro.SecurityUtils;

public class ShiroUtil {
    public static AccountProfile getProfile(){


//        JSONObject jsonObject= JSONUtil.parseObj(SecurityUtils.getSubject().getPrincipal());
//
//        return JSONUtil.toBean(jsonObject,AccountProfile.class) ;

        //编辑报错把注释去掉,把上面的方法注释掉
        return (AccountProfile) SecurityUtils.getSubject().getPrincipal();
    }
}
