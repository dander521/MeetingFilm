package com.stylefeng.guns.rest.modular.film.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.film.vo.*;
import com.stylefeng.guns.api.film.FilmServiceAPI;
import com.stylefeng.guns.core.util.DateUtil;
import com.stylefeng.guns.rest.common.persistence.dao.*;
import com.stylefeng.guns.rest.common.persistence.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Service(interfaceClass = FilmServiceAPI.class, loadbalance = "roundrobin")
public class DefaultFilmServiceImpl implements FilmServiceAPI {

    @Autowired
    private MoocBannerTMapper moocBannerTMapper;

    @Autowired
    private MoocFilmTMapper moocFilmTMapper;

    @Autowired
    private MoocCatDictTMapper moocCatDictTMapper;

    @Autowired
    private MoocSourceDictTMapper moocSourceDictTMapper;

    @Autowired
    private MoocYearDictTMapper moocYearDictTMapper;

    @Override
    public List<BannerVO> getBanners() {

        List<MoocBannerT> moocBanners = moocBannerTMapper.selectList(null);

        List<BannerVO> result = new ArrayList<>();
        for (MoocBannerT moocBannerT : moocBanners) {
            BannerVO bannerVO = new BannerVO();

            bannerVO.setBannerId(moocBannerT.getUuid()+"");
            bannerVO.setBannerUrl(moocBannerT.getBannerUrl());
            bannerVO.setBannerAddress(moocBannerT.getBannerAddress());

            result.add(bannerVO);
        }

        return result;
    }

    private List<FilmInfo> getFilmInfos(List<MoocFilmT> moocFilmTS) {
        List<FilmInfo> filmInfos = new ArrayList<>();

        for (MoocFilmT moocFilmT: moocFilmTS) {
            FilmInfo filmInfo = new FilmInfo();

            filmInfo.setScore(moocFilmT.getFilmScore());
            filmInfo.setImgAddress(moocFilmT.getImgAddress());
            filmInfo.setFilmType(moocFilmT.getFilmType());
            filmInfo.setFilmScore(moocFilmT.getFilmScore());
            filmInfo.setFilmName(moocFilmT.getFilmName());
            filmInfo.setFilmId(moocFilmT.getUuid()+"");
            filmInfo.setExpectNum(moocFilmT.getFilmPresalenum());
            filmInfo.setBoxNum(moocFilmT.getFilmBoxOffice());
            filmInfo.setShowTime(DateUtil.getDay(moocFilmT.getFilmTime()));

            filmInfos.add(filmInfo);
        }

        return filmInfos;
    }

    @Override
    public FilmVO getHotFilms(boolean isLimit, int nums,int nowPage,int sortId,int sourceId,int yearId,int catId) {
        FilmVO filmVO = new FilmVO();
        // 热映影片条件
        EntityWrapper<MoocFilmT> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("film_status", "1");
        // 判断是否是首页需要的内容
        if (isLimit) {
            // 是：限制条数、限制内容为热映
            Page<MoocFilmT> page = null;

            switch (sortId) {
                case 1:
                    page = new Page<>(1,nums,"film_box_office");
                    break;
                case 2:
                    page = new Page<>(1,nums,"film_time");
                    break;
                case 3:
                    page = new Page<>(1,nums,"film_score");
                    break;
                    default:
                        page = new Page<>(1,nums,"film_box_office");
                        break;
            }


            List<MoocFilmT> moocFilms = moocFilmTMapper.selectPage(page, entityWrapper);
            List<FilmInfo> filmInfos = getFilmInfos(moocFilms);
            filmVO.setFilmNum(moocFilms.size());
            filmVO.setFilmInfo(filmInfos);
        } else {
            // 否：列表页
            Page<MoocFilmT> page = new Page<>(nowPage,nums);

            if (sourceId != 99) {
                entityWrapper.eq("film_source", sourceId);
            }

            if (yearId != 99) {
                entityWrapper.eq("file_date", yearId);
            }

            if (catId != 99) {
                String catStr = "%#"+catId+"#%";
                entityWrapper.like("film_cats",catStr);
            }

            List<MoocFilmT> moocFilms = moocFilmTMapper.selectPage(page, entityWrapper);
            List<FilmInfo> filmInfos = getFilmInfos(moocFilms);
            filmVO.setFilmNum(moocFilms.size());
            int totalCounts = moocFilmTMapper.selectCount(entityWrapper);
            int totalPage = (totalCounts/nums)+1;
            filmVO.setFilmInfo(filmInfos);
            filmVO.setTotalPage(totalPage);
            filmVO.setNowPage(nowPage);
        }



        return filmVO;
    }

    @Override
    public FilmVO getSoonFilms(boolean isLimit, int nums,int nowPage,int sortId,int sourceId,int yearId,int catId) {
        FilmVO filmVO = new FilmVO();
        // 热映影片条件
        EntityWrapper<MoocFilmT> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("film_status", "2");
        // 判断是否是首页需要的内容
        if (isLimit) {
            // 是：限制条数、限制内容为热映
            Page<MoocFilmT> page = null;

            switch (sortId) {
                case 1:
                    page = new Page<>(1,nums,"film_preSaleNum");
                    break;
                case 2:
                    page = new Page<>(1,nums,"film_time");
                    break;
                case 3:
                    page = new Page<>(1,nums,"film_score");
                    break;
                default:
                    page = new Page<>(1,nums,"film_preSaleNum");
                    break;
            }
            List<MoocFilmT> moocFilms = moocFilmTMapper.selectPage(page, entityWrapper);
            List<FilmInfo> filmInfos = getFilmInfos(moocFilms);
            filmVO.setFilmNum(moocFilms.size());
            filmVO.setFilmInfo(filmInfos);
        } else {
            // 否：列表页
            Page<MoocFilmT> page = new Page<>(nowPage,nums);
            if (sourceId != 99) {
                entityWrapper.eq("film_source", sourceId);
            }

            if (yearId != 99) {
                entityWrapper.eq("file_date", yearId);
            }

            if (catId != 99) {
                String catStr = "%#"+catId+"#%";
                entityWrapper.like("film_cats",catStr);
            }
            List<MoocFilmT> moocFilms = moocFilmTMapper.selectPage(page, entityWrapper);
            List<FilmInfo> filmInfos = getFilmInfos(moocFilms);
            filmVO.setFilmNum(moocFilms.size());
            int totalCounts = moocFilmTMapper.selectCount(entityWrapper);
            int totalPage = (totalCounts/nums)+1;
            filmVO.setFilmInfo(filmInfos);
            filmVO.setTotalPage(totalPage);
            filmVO.setNowPage(nowPage);
        }

        return filmVO;
    }

    @Override
    public FilmVO getClassicFilms(int nums, int nowPage, int sortId, int sourceId, int yearId, int catId) {

        FilmVO filmVO = new FilmVO();
        // 热映影片条件
        EntityWrapper<MoocFilmT> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("film_status", "3");

        Page<MoocFilmT> page = null;

        switch (sortId) {
            case 1:
                page = new Page<>(1,nums,"film_box_office");
                break;
            case 2:
                page = new Page<>(1,nums,"film_time");
                break;
            case 3:
                page = new Page<>(1,nums,"film_box_office");
                break;
            default:
                page = new Page<>(1,nums,"film_box_office");
                break;
        }
        if (sourceId != 99) {
            entityWrapper.eq("film_source", sourceId);
        }

        if (yearId != 99) {
            entityWrapper.eq("file_date", yearId);
        }

        if (catId != 99) {
            String catStr = "%#"+catId+"#%";
            entityWrapper.like("film_cats",catStr);
        }
        List<MoocFilmT> moocFilms = moocFilmTMapper.selectPage(page, entityWrapper);
        List<FilmInfo> filmInfos = getFilmInfos(moocFilms);
        filmVO.setFilmNum(moocFilms.size());
        int totalCounts = moocFilmTMapper.selectCount(entityWrapper);
        int totalPage = (totalCounts/nums)+1;
        filmVO.setFilmInfo(filmInfos);
        filmVO.setTotalPage(totalPage);
        filmVO.setNowPage(nowPage);

        return filmVO;
    }

    @Override
    public List<FilmInfo> getBoxRanking() {

        // 条件 -> 正在上映的 票房前十名
        EntityWrapper<MoocFilmT> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("film_status", "1");

        Page<MoocFilmT> page = new Page<>(1,10, "film_box_office");
        List<MoocFilmT> moocFilmTS = moocFilmTMapper.selectPage(page, entityWrapper);
        List<FilmInfo> filmInfos = getFilmInfos(moocFilmTS);

        return filmInfos;
    }

    @Override
    public List<FilmInfo> getExpectRanking() {

        // 条件 -> 即将上映的 预售前十名
        EntityWrapper<MoocFilmT> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("film_status", "2");

        Page<MoocFilmT> page = new Page<>(1,10, "film_preSaleNum");
        List<MoocFilmT> moocFilmTS = moocFilmTMapper.selectPage(page, entityWrapper);
        List<FilmInfo> filmInfos = getFilmInfos(moocFilmTS);

        return filmInfos;
    }

    @Override
    public List<FilmInfo> getTop() {

        // 条件 -> 正在上映的 评分前100名
        EntityWrapper<MoocFilmT> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("film_status", "1");

        Page<MoocFilmT> page = new Page<>(1,10, "film_score");
        List<MoocFilmT> moocFilmTS = moocFilmTMapper.selectPage(page, entityWrapper);
        List<FilmInfo> filmInfos = getFilmInfos(moocFilmTS);

        return filmInfos;
    }

    @Override
    public List<CatVO> getCats() {
        List<CatVO> cats = new ArrayList<>();
        List<MoocCatDictT> moocCats = moocCatDictTMapper.selectList(null);
        for (MoocCatDictT moocCatDicT: moocCats) {
            CatVO catVO = new CatVO();
            catVO.setCatId(moocCatDicT.getUuid()+"");
            catVO.setCatName(moocCatDicT.getShowName());
            cats.add(catVO);
        }

        return cats;
    }

    @Override
    public List<SourceVO> getSources() {
        List<SourceVO> sources = new ArrayList<>();
        List<MoocSourceDictT> sourceDicts = moocSourceDictTMapper.selectList(null);
        for (MoocSourceDictT moocSourceDicT: sourceDicts) {
            SourceVO sourceVO = new SourceVO();
            sourceVO.setSourceId(moocSourceDicT.getUuid()+"");
            sourceVO.setSourceName(moocSourceDicT.getShowName());
            sources.add(sourceVO);
        }

        return sources;
    }

    @Override
    public List<YearVO> getYears() {
        List<YearVO> years = new ArrayList<>();
        List<MoocYearDictT> moocYears = moocYearDictTMapper.selectList(null);
        for (MoocYearDictT moocYearDicT: moocYears) {
            YearVO yearVO = new YearVO();
            yearVO.setYearId(moocYearDicT.getUuid()+"");
            yearVO.setYearName(moocYearDicT.getShowName());
            years.add(yearVO);
        }

        return years;
    }
}
