package com.stylefeng.guns.api.film.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class FilmInfo implements Serializable {

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
