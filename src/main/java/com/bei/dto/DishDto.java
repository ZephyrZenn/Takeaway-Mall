package com.bei.dto;

import com.bei.model.DishFlavor;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class DishDto {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    private String name;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long categoryId;

    private BigDecimal price;

    private String code;

    private String image;

    private String description;

    private Integer status;

    private List<DishFlavor> flavors;
}
