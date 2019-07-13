package com.stylefeng.guns.rest.modular.order;

import com.alibaba.dubbo.config.annotation.Reference;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.order.OrderServiceAPI;
import com.stylefeng.guns.api.order.vo.OrderVO;
import com.stylefeng.guns.rest.common.CurrentUser;
import com.stylefeng.guns.rest.modular.vo.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/order/")
public class OrderController {

    private static final String IMG_PRE = "";

    @Reference(interfaceClass = OrderServiceAPI.class, check = false)
    private OrderServiceAPI orderServiceAPI;

    // 购票
    @RequestMapping(value = "buyTickets", method = RequestMethod.POST)
    public ResponseVO buyTickets(Integer fieldId, String soldSeats, String seatsName) throws IOException {

        try {
            // 验证前端参数
            // 验证售出的票是否为真
            boolean isTrue = orderServiceAPI.isTrueSeats(fieldId + "", soldSeats);
            // 已销售的座位里 有没有这些座位
            boolean isNotSold = orderServiceAPI.isNotSoldSeats(fieldId + "", soldSeats);

            if (isTrue && isNotSold) {
                // 创建订单信息
                String userId = CurrentUser.getCurrentUserId();
                if (userId != null && userId.trim().length()>0) {
                    OrderVO orderVO = orderServiceAPI.saveOrderInfo(fieldId,soldSeats,seatsName,Integer.parseInt(userId));
                    if (orderVO == null) {
                        log.error("购票未成功");
                        return ResponseVO.serviceFail("购票业务异常");
                    } else {
                        return ResponseVO.success(orderVO);
                    }
                } else {
                    return ResponseVO.serviceFail("用户未登录");
                }
            } else {
                return ResponseVO.serviceFail("订单中的座位编号有问题");
            }
        } catch (Exception e) {
            log.error("购票未成功");
            return ResponseVO.serviceFail("购票业务异常");
        }
    }

    @RequestMapping(value = "getOrderInfo", method = RequestMethod.POST)
    public ResponseVO getOrderInfo(
            @RequestParam(name = "nowPage",required = false,defaultValue = "1") Integer nowPage,
            @RequestParam(name = "pageSize",required = false,defaultValue = "5") Integer pageSize
    ) {

        // 获取当前登录人的信息
        String userId = CurrentUser.getCurrentUserId();
        // 使用当前登录人 获取已经购买的订单
        Page<OrderVO> page = new Page<>(nowPage,pageSize);

        if (userId != null && userId.trim().length()>0) {
            Page<OrderVO> orderByUserId = orderServiceAPI.getOrderByUserId(Integer.parseInt(userId), page);
            return ResponseVO.success(nowPage,pageSize,IMG_PRE,orderByUserId.getRecords());
        } else {
            return ResponseVO.serviceFail("用户未登录");
        }
    }
}
