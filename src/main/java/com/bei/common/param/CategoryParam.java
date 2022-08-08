package com.bei.common.param;

import lombok.Data;

@Data
public class CategoryParam {
    private Long id;
    private String name;
    private Integer sort;
    private Integer type;
}
