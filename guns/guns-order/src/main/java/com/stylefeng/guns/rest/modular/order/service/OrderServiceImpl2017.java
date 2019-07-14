package com.stylefeng.guns.rest.modular.order.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.cinema.CinemaServiceAPI;
import com.stylefeng.guns.api.cinema.vo.FilmInfoVO;
import com.stylefeng.guns.api.cinema.vo.OrderQueryVO;
import com.stylefeng.guns.api.order.OrderServiceAPI;
import com.stylefeng.guns.api.order.vo.OrderVO;
import com.stylefeng.guns.core.util.UUIDUtil;
import com.stylefeng.guns.rest.common.persistence.dao.MoocOrder2017TMapper;
import com.stylefeng.guns.rest.common.persistence.model.MoocOrder2017T;
import com.stylefeng.guns.rest.common.util.FTPUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Service(interfaceClass = OrderServiceAPI.class, group = "order2017")
public class OrderServiceImpl2017 implements OrderServiceAPI {

    @Autowired
    private MoocOrder2017TMapper moocOrderTMapper;

    // 服务之间可黑盒互相调用
    @Reference(interfaceClass = CinemaServiceAPI.class, check = false)
    private CinemaServiceAPI cinemaServiceAPI;

    @Autowired
    private FTPUtil ftpUtil;

    // 验证售出的票是否为真
    @Override
    public boolean isTrueSeats(String fieldId, String seats) throws IOException {

        // 根据fieldId找到对应的座位位置图
        String seatPath = moocOrderTMapper.getSeatsByFieldId(fieldId);
        // 读取位置图，判断seats是否为真
        String fileStrByAddress = ftpUtil.getFileStrByAddress(seatPath);

        // 判断购买的座位 和 影院影厅当前剩余的座位是否满足购买条件
        JSONObject jsonObject = JSONObject.parseObject(fileStrByAddress);
        String ids = jsonObject.get("ids").toString();

        // 购买的座位
        String[] seatArrays = seats.split(",");
        // 剩余的座位
        String[] idsArrays = ids.split(",");
        int isTrue = 0;
        for (String id : idsArrays) {
            for (String seat : seatArrays) {
                if (seat.equalsIgnoreCase(id)) {
                    isTrue++;
                }
            }
        }

        return seatArrays.length == isTrue ? true : false;
    }

    // 已销售的座位里 有没有这些座位
    @Override
    public boolean isNotSoldSeats(String fieldId, String seats) {

        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("field_id", fieldId);

        List<MoocOrder2017T> list = moocOrderTMapper.selectList(entityWrapper);
        String[] seatsArrs = seats.split(",");

        for (MoocOrder2017T orderT : list) {
            String[] ids = orderT.getSeatsIds().split(",");
            for (String id : ids) {
                for (String seat : seatsArrs) {
                    if (id.equalsIgnoreCase(seat)) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    @Override
    public OrderVO saveOrderInfo(Integer fieldId, String soldSeats, String seatsName, Integer userId) {

        String uuid = UUIDUtil.getUuid();

        FilmInfoVO filmInfoVO = cinemaServiceAPI.getFilmInfoByFieldId(fieldId);
        Integer filmId = Integer.parseInt(filmInfoVO.getFilmId());

        OrderQueryVO orderNeeds = cinemaServiceAPI.getOrderNeeds(fieldId);
        Integer cinemaId = Integer.parseInt(orderNeeds.getCinemaId());
        double filmPrice = Double.parseDouble(orderNeeds.getFilmPrice());

        int solds = soldSeats.split(",").length;
        double totalPrice = getTotalPrice(solds, filmPrice);

        MoocOrder2017T orderT = new MoocOrder2017T();
        orderT.setUuid(uuid);
        orderT.setSeatsName(seatsName);
        orderT.setSeatsIds(soldSeats);
        orderT.setOrderUser(userId);
        orderT.setOrderPrice(totalPrice);
        orderT.setFilmPrice(filmPrice);
        orderT.setFieldId(fieldId);
        orderT.setFilmId(filmId);
        orderT.setCinemaId(cinemaId);

        Integer insert = moocOrderTMapper.insert(orderT);

        if (insert>0) {
            OrderVO orderVO = moocOrderTMapper.getOrderInfoById(uuid);
            if (orderVO==null||orderVO.getOrderId()==null) {
                log.error("订单信息查询失败，订单编号为{}",uuid);
                return null;
            } else {
                return orderVO;
            }
        } else {
            log.error("订单插入失败");
        }

        return null;
    }

    private static double getTotalPrice(int solds, double filmPrice) {
        BigDecimal soldsDeci = new BigDecimal(solds);
        BigDecimal filmPriceDeci = new BigDecimal(filmPrice);

        BigDecimal result = soldsDeci.multiply(filmPriceDeci);

        // 四舍五入 小数点两位
        BigDecimal bigDecimal = result.setScale(2, RoundingMode.UP);
        return bigDecimal.doubleValue();
    }

    @Override
    public Page<OrderVO> getOrderByUserId(Integer userId, Page<OrderVO> page) {
        Page<OrderVO> result = new Page<>();
        if (userId == null) {
            log.error("订单查询业务失败，用户编号未传入");
            return null;
        } else {
            List<OrderVO> ordersByUserId = moocOrderTMapper.getOrdersByUserId(userId, page);
            if (ordersByUserId==null && ordersByUserId.size()==0) {
                result.setTotal(0);
                result.setRecords(new ArrayList<>());
                return result;
            } else {
                EntityWrapper<MoocOrder2017T> moocOrderTEntityWrapper = new EntityWrapper<>();
                moocOrderTEntityWrapper.eq("order_user",userId);
                int count = moocOrderTMapper.selectCount(moocOrderTEntityWrapper);
                result.setTotal(count);
                result.setRecords(ordersByUserId);
                return result;
            }
        }
    }

    // 获取已售座位
    @Override
    public String getSoldSeatsByFieldId(Integer fieldId) {

        if (fieldId == null) {
            log.error("查询已售业务失败，场次未传入");
            return "";
        } else {
            String soldSeatsByFieldId = moocOrderTMapper.getSoldSeatsByFieldId(fieldId);
            return soldSeatsByFieldId;
        }
    }

    @Override
    public OrderVO getOrderInfoById(String orderId) {
        OrderVO orderInfoById = moocOrderTMapper.getOrderInfoById(orderId);
        return orderInfoById;
    }

    @Override
    public boolean paySuccess(String orderId) {

        MoocOrder2017T moocOrderT = new MoocOrder2017T();
        moocOrderT.setUuid(orderId);
        moocOrderT.setOrderStatus(1);
        Integer integer = moocOrderTMapper.updateById(moocOrderT);
        return integer>=1 ? true : false;
    }

    @Override
    public boolean payFail(String orderId) {
        MoocOrder2017T moocOrderT = new MoocOrder2017T();
        moocOrderT.setUuid(orderId);
        moocOrderT.setOrderStatus(2);
        Integer integer = moocOrderTMapper.updateById(moocOrderT);
        return integer>=1 ? true : false;
    }
}
