package com.stylefeng.guns.api.film;

import com.stylefeng.guns.api.film.vo.*;

import java.util.List;

public interface FilmServiceAPI {

    // 获取banners
    List<BannerVO> getBanners();

    // 获取热映影片
    FilmVO getHotFilms(boolean isLimit, int nums);

    // 获取即将上映影片【受欢迎程度排序】
    FilmVO getSoonFilms(boolean isLimit, int nums);

    // 获取票房排行
    List<FilmInfo> getBoxRanking();

    // 获取人气排行
    List<FilmInfo> getExpectRanking();

    // 获取Top100
    List<FilmInfo> getTop();


    List<CatVO> getCats();

    List<SourceVO> getSources();

    List<YearVO> getYears();
}