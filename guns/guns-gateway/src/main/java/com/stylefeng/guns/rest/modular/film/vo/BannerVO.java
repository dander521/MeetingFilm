package com.stylefeng.guns.rest.modular.film.vo;

import lombok.Data;
import lombok.Setter;

@Data
public class BannerVO {

    @Setter
    private String bannerId;
    private String bannerAddress;
    private String bannerUrl;


}
