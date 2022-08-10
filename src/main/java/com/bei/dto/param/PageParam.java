package com.bei.dto.param;

import lombok.Data;

@Data
public class PageParam {
    int page;
    int pageSize;
    String name;
}
