package com.stylefeng.guns.api.film.vo;

import lombok.Data;

@Data
public class FilmInfo {

    private String filmId;
    private int filmType;
    private String filmName;
    private String filmScore;
    private String imgAddress;
    private int expectNum;
    private String showTime;
    private int boxNum;
    private String score;

}
