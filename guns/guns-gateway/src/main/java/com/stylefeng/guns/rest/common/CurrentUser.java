package com.stylefeng.guns.rest.common;


public class CurrentUser {

    // 线程绑定的存储空间
    private static final ThreadLocal<String> threadlocal = new ThreadLocal<>();
    // 将用户信息放入存储空间
    public static void saveUserId(String userId) {
        threadlocal.set(userId);
    }

    // 将用户信息取出
    public static String getCurrentUserId() {
        return threadlocal.get();
    }

    /*   考虑到jvm内存大小 存储对象过多 使用存储id   */
//    public static void saveUserInfo(UserInfoModel userInfoModel) {
//        threadlocal.set(userInfoModel);
//    }
    // 将用户信息取出
//    public static UserInfoModel getCurrentUser() {
//        return threadlocal.get();
//    }
}
