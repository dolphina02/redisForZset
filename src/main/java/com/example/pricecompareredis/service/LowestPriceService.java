package com.example.pricecompareredis.service;

import com.example.pricecompareredis.vo.Keyword;
import com.example.pricecompareredis.vo.Product;
import com.example.pricecompareredis.vo.ProductGrp;

import java.util.Set;

public interface LowestPriceService {
    Set GetZsetValue(String key);
    Set GetZsetValueWithStatus(String key) throws Exception;

    Set GetZsetValueWithSpecificException(String key) throws Exception;
    int SetNewProduct(Product newProduct);

    int SetNewProductGrp(ProductGrp newProductGrp);

    int SetNewProductGrpToKeyword (String keyword, String prodGrpId, double score);

    Keyword GetLowestPriceProductByKeyword(String keyword);

}
