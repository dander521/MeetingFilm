package com.stylefeng.guns.api.film.vo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class BannerVO {

    @Setter
    @Getter
    private String bannerId;
    private String bannerAddress;
    private String bannerUrl;


}
