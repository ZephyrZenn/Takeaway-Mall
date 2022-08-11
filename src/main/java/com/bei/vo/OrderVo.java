package com.bei.vo;

import com.bei.model.OrderDetail;
import com.bei.model.Orders;
import lombok.Data;

import java.util.List;

@Data
public class OrderVo extends Orders {

    private List<OrderDetail> orderDetails;

    private Integer sumNum;
}
