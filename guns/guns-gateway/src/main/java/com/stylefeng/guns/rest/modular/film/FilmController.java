package com.stylefeng.guns.rest.modular.film;

import com.alibaba.dubbo.config.annotation.Reference;
import com.stylefeng.guns.api.film.FilmServiceAPI;
import com.stylefeng.guns.api.film.vo.CatVO;
import com.stylefeng.guns.api.film.vo.SourceVO;
import com.stylefeng.guns.api.film.vo.YearVO;
import com.stylefeng.guns.rest.modular.film.vo.FilmConditionVO;
import com.stylefeng.guns.rest.modular.film.vo.FilmIndexVO;
import com.stylefeng.guns.rest.modular.vo.ResponseVO;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RequestMapping("/film/")
@RestController
public class FilmController {

    private static final String IMG_PRE="http://img.meetingshop.cn/";

    @Reference(interfaceClass = FilmServiceAPI.class, check = false)
    private FilmServiceAPI filmServiceAPI;
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
        FilmIndexVO filmIndexVO = new FilmIndexVO();
        // 获取banner信息
        filmIndexVO.setBanners(filmServiceAPI.getBanners());
        // 获取正在热映的电源
        filmIndexVO.setHotFilms(filmServiceAPI.getHotFilms(true, 8));
        // 即将上映的电影
        filmIndexVO.setSoonFilms(filmServiceAPI.getSoonFilms(true, 8));
        // 票房排行榜
        filmIndexVO.setBoxRanking(filmServiceAPI.getBoxRanking());
        // 受欢迎的榜单
        filmIndexVO.setExpectRanking(filmServiceAPI.getExpectRanking());
        // 前100
        filmIndexVO.setTop100(filmServiceAPI.getTop());
        return ResponseVO.success(IMG_PRE, filmIndexVO);
    }

    @RequestMapping(value = "getConditionList", method = RequestMethod.GET)
    public ResponseVO getConditionList(@RequestParam(name = "catId", required = false, defaultValue = "99") String catId,
                                       @RequestParam(name = "sourceId", required = false, defaultValue = "99") String sourceId,
                                       @RequestParam(name = "yearId", required = false, defaultValue = "99") String yearId) {

        FilmConditionVO filmConditionVO = new FilmConditionVO();
        // 类型集合
        boolean flag = false;
        List<CatVO> cats = filmServiceAPI.getCats();
        List<CatVO> catResult = new ArrayList<>();
        CatVO catVO = null;
        for (CatVO cat : cats) {
            if (cat.getCatId().equals("99")) {
                catVO = cat;
                continue;
            }

            if (cat.getCatId().equals(catId)) {
                flag = true;
                cat.setActive(true);
            } else {
                cat.setActive(false);
            }
            catResult.add(cat);
        }
        if (!flag) {
            catVO.setActive(true);
        } else {
            catVO.setActive(false);
        }
        catResult.add(catVO);

        // 片源集合
        flag = false;
        List<SourceVO> sources = filmServiceAPI.getSources();
        List<SourceVO> sourceResult = new ArrayList<>();
        SourceVO sourceVO = null;
        for (SourceVO source : sources) {
            if (source.getSourceId().equals("99")) {
                sourceVO = source;
                continue;
            }

            if (source.getSourceId().equals(sourceId)) {
                flag = true;
                source.setActive(true);
            } else {
                source.setActive(false);
            }
            sourceResult.add(source);
        }
        if (!flag) {
            sourceVO.setActive(true);
        } else {
            sourceVO.setActive(false);
        }
        sourceResult.add(sourceVO);

        // 年代集合
        flag = false;
        List<YearVO> years = filmServiceAPI.getYears();
        List<YearVO> yearResult = new ArrayList<>();
        YearVO yearVO = null;
        for (YearVO year : years) {
            if (year.getYearId().equals("99")) {
                yearVO = year;
                continue;
            }

            if (year.getYearId().equals(yearId)) {
                flag = true;
                year.setActive(true);
            } else {
                year.setActive(false);
            }
            yearResult.add(year);
        }
        if (!flag) {
            yearVO.setActive(true);
        } else {
            yearVO.setActive(false);
        }
        yearResult.add(yearVO);

        filmConditionVO.setCatInfo(catResult);
        filmConditionVO.setSourceInfo(sourceResult);
        filmConditionVO.setYearInfo(yearResult);

        return ResponseVO.success(filmConditionVO);
    }
}
