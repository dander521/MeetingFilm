package com.stylefeng.guns.rest.modular.cinema.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.cinema.CinemaServiceAPI;
import com.stylefeng.guns.api.cinema.vo.*;
import com.stylefeng.guns.rest.common.persistence.dao.*;
import com.stylefeng.guns.rest.common.persistence.model.MoocAreaDictT;
import com.stylefeng.guns.rest.common.persistence.model.MoocBrandDictT;
import com.stylefeng.guns.rest.common.persistence.model.MoocCinemaT;
import com.stylefeng.guns.rest.common.persistence.model.MoocHallDictT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Service(interfaceClass = CinemaServiceAPI.class, executes = 10)
public class DefaultCinemaServiceImpl implements CinemaServiceAPI {

    @Autowired
    private MoocCinemaTMapper moocCinemaTMapper;
    @Autowired
    private MoocAreaDictTMapper moocAreaDictTMapper;
    @Autowired
    private MoocBrandDictTMapper moocBrandDictTMapper;
    @Autowired
    private MoocHallDictTMapper moocHallDictTMapper;
    @Autowired
    private MoocHallFilmInfoTMapper moocHallFilmInfoTMapper;
    @Autowired
    private MoocFieldTMapper moocFieldTMapper;

    // 1、根据CinemaQueryVO，查询影院列表
    @Override
    public Page<CinemaVO> getCinemas(CinemaQueryVO cinemaQueryVO) {

        List<CinemaVO> cinemas = new ArrayList<>();
        Page<MoocCinemaT> page = new Page<>(cinemaQueryVO.getNowPage(), cinemaQueryVO.getPageSize());

        EntityWrapper<MoocCinemaT> entityWrapper = new EntityWrapper<>();
        if (cinemaQueryVO.getBrandId() != 99) {
            entityWrapper.eq("brand_id", cinemaQueryVO.getBrandId());
        }
        if (cinemaQueryVO.getDistrictId() != 99) {
            entityWrapper.eq("area_id", cinemaQueryVO.getDistrictId());
        }
        if (cinemaQueryVO.getHallType() != 99) {
            entityWrapper.like("hall_ids","%#"+cinemaQueryVO.getHallType()+"#%");
        }

        List<MoocCinemaT> moocCinemaTS = moocCinemaTMapper.selectPage(page, entityWrapper);
        for (MoocCinemaT cinemaT: moocCinemaTS) {
            CinemaVO cinemaVO = new CinemaVO();
            cinemaVO.setUuid(cinemaT.getUuid()+"");
            cinemaVO.setMinimumPrice(cinemaT.getMinimumPrice()+"");
            cinemaVO.setCinemaName(cinemaT.getCinemaName());
            cinemaVO.setAddress(cinemaT.getCinemaAddress());
            cinemas.add(cinemaVO);
        }

        // 影院总数
        Integer count = moocCinemaTMapper.selectCount(entityWrapper);

        Page<CinemaVO> result = new Page<>();
        result.setRecords(cinemas);
        result.setTotal(count);
        result.setSize(cinemaQueryVO.getPageSize());

        return result;
    }

    // 2、根据条件获取品牌列表
    @Override
    public List<BrandVO> getBrands(int brandId) {

        boolean flag = false;
        List<BrandVO> brandVOS = new ArrayList<>();

        // 判断brandid是否存在
        MoocBrandDictT moocBrandDictT = moocBrandDictTMapper.selectById(brandId);
        // 判断brandid是否=99
        if (brandId==99 || moocBrandDictT==null || moocBrandDictT.getUuid()==null) {
            flag = true;
        }
        // 查询所有列表
        List<MoocBrandDictT> moocBrandDictTS = moocBrandDictTMapper.selectList(null);
        // flag=true 设置99为isActive
        for (MoocBrandDictT brandDictT : moocBrandDictTS) {
            BrandVO brandVO = new BrandVO();
            brandVO.setBrandId(moocBrandDictT.getUuid()+"");
            brandVO.setBrandName(moocBrandDictT.getShowName());
            if (flag) {
                if (brandDictT.getUuid()==99) {
                    brandVO.setActive(true);
                }
            } else {
                if (brandDictT.getUuid() == brandId) {
                    brandVO.setActive(true);
                }
            }
            brandVOS.add(brandVO);
        }


        return brandVOS;
    }

    // 3、获取行政区域列表
    @Override
    public List<AreaVO> getAreas(int areaId) {
        boolean flag = false;
        List<AreaVO> areaVOS = new ArrayList<>();

        // 判断brandid是否存在
        MoocAreaDictT moocAreaDictT = moocAreaDictTMapper.selectById(areaId);
        // 判断brandid是否=99
        if (areaId==99 || moocAreaDictT==null || moocAreaDictT.getUuid()==null) {
            flag = true;
        }
        // 查询所有列表
        List<MoocAreaDictT> moocAreaDictTS = moocAreaDictTMapper.selectList(null);
        // flag=true 设置99为isActive
        for (MoocAreaDictT areaDictT : moocAreaDictTS) {
            AreaVO areaVO = new AreaVO();
            areaVO.setAreaId(areaDictT.getUuid()+"");
            areaVO.setAreaName(areaDictT.getShowName());
            if (flag) {
                if (areaDictT.getUuid()==99) {
                    areaVO.setActive(true);
                }
            } else {
                if (areaDictT.getUuid() == areaId) {
                    areaVO.setActive(true);
                }
            }
            areaVOS.add(areaVO);
        }


        return areaVOS;
    }

    // 4、获取影厅类型列表
    @Override
    public List<HallTypeVO> getHallTypes(int hallType) {
        boolean flag = false;
        List<HallTypeVO> hallTypeVOS = new ArrayList<>();

        // 判断brandid是否存在
        MoocHallDictT moocHallDictT = moocHallDictTMapper.selectById(hallType);
        // 判断brandid是否=99
        if (hallType==99 || moocHallDictT==null || moocHallDictT.getUuid()==null) {
            flag = true;
        }
        // 查询所有列表
        List<MoocHallDictT> moocHallDictTS = moocHallDictTMapper.selectList(null);
        // flag=true 设置99为isActive
        for (MoocHallDictT hallDictT : moocHallDictTS) {
            HallTypeVO hallTypeVO = new HallTypeVO();
            hallTypeVO.setHalltypeId(hallDictT.getUuid()+"");
            hallTypeVO.setHalltypeName(hallDictT.getShowName());
            if (flag) {
                if (hallDictT.getUuid()==99) {
                    hallTypeVO.setActive(true);
                }
            } else {
                if (hallDictT.getUuid() == hallType) {
                    hallTypeVO.setActive(true);
                }
            }
            hallTypeVOS.add(hallTypeVO);
        }


        return hallTypeVOS;
    }

    // 5、根据影院编号，获取影院信息
    @Override
    public CinemaInfoVO getCinemaInfoById(int cinemaId) {

        MoocCinemaT cinemaT = moocCinemaTMapper.selectById(cinemaId);

        CinemaInfoVO cinemaInfoVO = new CinemaInfoVO();
        cinemaInfoVO.setImgUrl(cinemaT.getImgAddress());
        cinemaInfoVO.setCinemaPhone(cinemaT.getCinemaPhone());
        cinemaInfoVO.setCinemaName(cinemaT.getCinemaName());
        cinemaInfoVO.setCinemaId(cinemaT.getUuid()+"");
        cinemaInfoVO.setCinemaAdress(cinemaT.getCinemaAddress());

        return cinemaInfoVO;
    }

    // 6、获取所有电影的信息和对应的放映场次信息，根据影院编号
    @Override
    public List<FilmInfoVO> getFilmInfoByCinemaId(int cinemaId) {

        List<FilmInfoVO> filmInfoVOS = moocFieldTMapper.getFilmInfos(cinemaId);

        return filmInfoVOS;
    }

    // 7、根据放映场次id获取放映信息
    @Override
    public HallInfoVO getFilmFieldInfo(int fieldId) {

        HallInfoVO hallInfoVO = moocFieldTMapper.getHallInfo(fieldId);
        return hallInfoVO;
    }

    // 8、根据放映场次查询播放的电影编号，然后根据电影编号获取对应的电影信息
    @Override
    public FilmInfoVO getFilmInfoByFieldId(int fieldId) {

        FilmInfoVO filmInfoVO = moocFieldTMapper.getFilmInfoById(fieldId);
        return filmInfoVO;
    }

}
