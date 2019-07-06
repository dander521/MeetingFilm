package com.stylefeng.guns.rest.modular.film;

import com.stylefeng.guns.rest.modular.vo.ResponseVO;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/film/")
@RestController
public class FilmController {

    /*
    *
    * 获取首页信息
    *
    * API网关：
    * 1、功能聚合
    * 好处：
    *   1.6个接口，一次请求，同一时刻节省5次http请求
    *   2.同一个接口对外暴露，降低了前后端分离开发的难度和复杂度
    * 坏处：
    *   1.以此获取数据过多，容易出现问题
    *
    * */
    @RequestMapping(value = "getIndex", method = RequestMethod.GET)
    public ResponseVO getIndex() {

        // 获取banner信息

        // 获取正在热映的电源

        // 即将上映的电影

        // 票房排行榜

        // 受欢迎的榜单

        // 前100

        return null;
    }
}
