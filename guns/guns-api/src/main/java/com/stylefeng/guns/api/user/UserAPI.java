package com.stylefeng.guns.api.user;

public interface UserAPI {

    int login(String name, String password);

    boolean register(UserModel userModel);

    boolean checkUsername(String username);

    UserInfoModel getUserInfo(String uuid);

    UserInfoModel updateUserInfo(UserInfoModel userInfoModel);
}
