package com.bei.vo;

import com.bei.model.DishFlavor;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class DishVo {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    private String name;

    private String categoryName;

    private BigDecimal price;

    private String code;

    private String image;

    private String description;

    private Integer status;

    private String updateTime;

    private List<DishFlavor> flavors;
}
