package com.stylefeng.guns.rest.modular.user;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.dubbo.config.annotation.Reference;
import com.stylefeng.guns.api.user.UserAPI;
import com.stylefeng.guns.api.user.UserInfoModel;
import com.stylefeng.guns.api.user.UserModel;
import com.stylefeng.guns.rest.common.CurrentUser;
import com.stylefeng.guns.rest.modular.vo.ResponseVO;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/user/")
@RestController
public class UserController {

    @Reference(interfaceClass = UserAPI.class)
    private UserAPI userAPI;

    @RequestMapping(name = "register", method = RequestMethod.POST)
    public ResponseVO register(UserModel userModel) {
        if (StringUtils.isEmpty(userModel.getUsername()) || StringUtils.isEmpty(userModel.getPassword())) {
            return ResponseVO.serviceFail("用户名或密码不能为空");
        }

        boolean isSuccess = userAPI.register(userModel);
        return isSuccess ? ResponseVO.success("注册成功") : ResponseVO.serviceFail("注册失败");
    }

    @RequestMapping(name = "check", method = RequestMethod.POST)
    public ResponseVO check(String username) {
        if (!StringUtils.isEmpty(username)) {
            boolean notExist = userAPI.checkUsername(username);
            return notExist ? ResponseVO.success("用户名不存在，当前可用") : ResponseVO.serviceFail("用户名已存在");
        } else {
            return ResponseVO.serviceFail("用户名不能为空");
        }
    }

    @RequestMapping(name = "logout", method = RequestMethod.GET)
    public ResponseVO logout() {
        /*
        *
        * 应用：
        * 1、前端存储JWT【7天】 JWT刷新
        * 2、服务器会存储 活动用户信息 【30分钟】
        * 3、JWT里的userId为key，查找活跃用户
        *
        * 退出：
        * 1、前端删除JWT
        * 2、后端服务器删除活跃用户缓存
        *
        * 现状：
        * 1、前端删除掉JWT
        *
        * */

        return ResponseVO.success("用户退出成功");
    }

    @RequestMapping(name = "getUserInfo", method = RequestMethod.GET)
    public ResponseVO getUserInfo() {
        String userId = CurrentUser.getCurrentUserId();
        if (!StringUtils.isEmpty(userId)) {
            UserInfoModel userInfoModel = userAPI.getUserInfo(userId);
            return userInfoModel!=null ? ResponseVO.success(userInfoModel) : ResponseVO.appFail("用户信息查询失败");
        } else {
            return ResponseVO.serviceFail("用户未登录");
        }
    }

    @RequestMapping(name = "updateUserInfo", method = RequestMethod.POST)
    public ResponseVO updateUserInfo(UserInfoModel userInfoModel) {
        String userId = CurrentUser.getCurrentUserId();
        if (!StringUtils.isEmpty(userId)) {
            if (userInfoModel.getUuid() != Integer.parseInt(userId)) {
                return ResponseVO.serviceFail("请修改您的个人信息");
            }
            UserInfoModel userInfo = userAPI.updateUserInfo(userInfoModel);
            return userInfo!=null ? ResponseVO.success(userInfo) : ResponseVO.appFail("用户信息修改失败");
        } else {
            return ResponseVO.serviceFail("用户未登录");
        }
    }
}
