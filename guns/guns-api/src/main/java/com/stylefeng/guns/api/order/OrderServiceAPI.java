package com.stylefeng.guns.api.order;

import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.order.vo.OrderVO;

import java.io.IOException;
import java.util.List;

public interface OrderServiceAPI {

    // 验证售出的票是否为真
    boolean isTrueSeats(String fieldId, String seats) throws IOException;
    // 已销售的座位里 有没有这些座位
    boolean isNotSoldSeats(String fieldId, String seats);
    // 创建订单信息
    OrderVO saveOrderInfo(Integer fieldId,String soldSeats,String seatsName,Integer userId);
    // 使用当前登录人 获取已经购买的订单
    Page<OrderVO> getOrderByUserId(Integer userId, Page<OrderVO> page);
    // 根据fieldId 获取所有已经销售的座位编号
    String getSoldSeatsByFieldId(Integer fieldId);

    OrderVO getOrderInfoById(String orderId);

//    boolean updateOrderStatus(String orderId, Integer orderStatus);
    boolean paySuccess(String orderId);

    boolean payFail(String orderId);
}
