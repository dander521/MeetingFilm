package com.stylefeng.guns.rest.modular.user;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.stylefeng.guns.api.user.UserAPI;
import com.stylefeng.guns.api.user.UserInfoModel;
import com.stylefeng.guns.api.user.UserModel;
import com.stylefeng.guns.core.util.MD5Util;
import com.stylefeng.guns.rest.common.persistence.dao.MoocUserTMapper;
import com.stylefeng.guns.rest.common.persistence.model.MoocUserT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Date;

@Component
@Service(interfaceClass = UserAPI.class, loadbalance = "roundrobin")
public class UserServiceImpl implements UserAPI {

    @Autowired
    private MoocUserTMapper moocUserTMapper;

    @Override
    public boolean register(UserModel userModel) {
        // 将注册信息实体转换为数据实体
        MoocUserT moocUserT = new MoocUserT();
        moocUserT.setUserName(userModel.getUsername());
        moocUserT.setUserPwd(MD5Util.encrypt(userModel.getPassword())); // 注意
        moocUserT.setEmail(userModel.getEmail());
        moocUserT.setUserPhone(userModel.getPhone());
        moocUserT.setAddress(userModel.getAddress());
        // 创建时间和修改时间默认值为当前时间

        // 数据加密两种方式 【MD5混淆加密 + 盐值 -> Shiro】

        // 将数据实体存入数据库
        Integer insert = moocUserTMapper.insert(moocUserT);
        if (insert > 0) {
            return true;
        }

        return false;
    }



    @Override
    public int login(String name, String password) {

        // 根据登录账号获取数据库信息
        MoocUserT moocUserT = new MoocUserT();
        moocUserT.setUserName(name);

        MoocUserT result = moocUserTMapper.selectOne(moocUserT);

        // 获取结果 密码校验
        if (result!=null && result.getUuid()>0) {
            String md5Pwd = MD5Util.encrypt(password);
            if (result.getUserPwd().equals(md5Pwd)) {
                return result.getUuid();
            }
        }

        return 0;
    }

    @Override
    public boolean checkUsername(String username) {
        EntityWrapper<MoocUserT> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("user_name", username);
        Integer result = moocUserTMapper.selectCount(entityWrapper);
        if (result!=null && result>0) {
            return false;
        }

        return true;
    }

    @Override
    public UserInfoModel getUserInfo(String uuid) {

        MoocUserT moocUserT = moocUserTMapper.selectById(uuid);

        UserInfoModel userInfoModel = do2UserInfo(moocUserT);

        return userInfoModel;
    }

    @Override
    public UserInfoModel updateUserInfo(UserInfoModel userInfoModel) {

        // 将传入的数据转为MoocUserT

        MoocUserT moocUserT = new MoocUserT();
        moocUserT.setUuid(userInfoModel.getUuid());
        moocUserT.setUserName(userInfoModel.getUsername());
        moocUserT.setUpdateTime(time2Date(System.currentTimeMillis()));
        moocUserT.setUserSex(userInfoModel.getSex());
        moocUserT.setUserPhone(userInfoModel.getPhone());
        moocUserT.setNickName(userInfoModel.getNickname());
        moocUserT.setLifeState(Integer.parseInt(userInfoModel.getLifeState()));
        moocUserT.setHeadUrl(userInfoModel.getHeadAddress());
        moocUserT.setEmail(userInfoModel.getEmail());
        moocUserT.setBirthday(userInfoModel.getBirthday());
        moocUserT.setBiography(userInfoModel.getBiography());
        moocUserT.setAddress(userInfoModel.getAddress());
        moocUserT.setBeginTime(userInfoModel.getBeginTime());

        // 存入数据库

        Integer isSuccess = moocUserTMapper.updateById(moocUserT);
        if (isSuccess > 0) {
            UserInfoModel userInfo = getUserInfo(""+moocUserT.getUuid());
            return userInfo;
        }

        return userInfoModel;
    }

    private Date time2Date(long currentTimeMillis) {
        Date date = new Date(currentTimeMillis);
        return date;
    }

    private UserInfoModel do2UserInfo(MoocUserT moocUserT) {
        UserInfoModel userInfoModel = new UserInfoModel();

        userInfoModel.setUuid(moocUserT.getUuid());
        userInfoModel.setUsername(moocUserT.getUserName());
        userInfoModel.setUpdateTime(moocUserT.getUpdateTime());
        userInfoModel.setSex(moocUserT.getUserSex());
        userInfoModel.setPhone(moocUserT.getUserPhone());
        userInfoModel.setNickname(moocUserT.getNickName());
        userInfoModel.setLifeState(""+moocUserT.getLifeState());
        userInfoModel.setHeadAddress(moocUserT.getHeadUrl());
        userInfoModel.setEmail(moocUserT.getEmail());
        userInfoModel.setBirthday(moocUserT.getBirthday());
        userInfoModel.setBiography(moocUserT.getBiography());
        userInfoModel.setBeginTime(moocUserT.getBeginTime());
        userInfoModel.setAddress(moocUserT.getAddress());

        return userInfoModel;
    }

}
