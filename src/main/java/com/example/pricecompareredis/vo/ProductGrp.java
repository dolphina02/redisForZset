package com.example.pricecompareredis.vo;

import lombok.Data;
import lombok.NonNull;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Data
public class ProductGrp {

    private String prodGrpId; // FPG0001 --> 하기스 3단계 기저귀

    private List<Product> productList; // [{d1fc1031-da1c-40da-9cd1-e9fef3f2a336, 25000}, {}...]
}
