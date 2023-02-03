package com.example.pricecompareredis.service;

import com.example.pricecompareredis.vo.Keyword;
import com.example.pricecompareredis.vo.NotFoundException;
import com.example.pricecompareredis.vo.Product;
import com.example.pricecompareredis.vo.ProductGrp;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class LowestPriceServiceImpl implements LowestPriceService{

    private RedisTemplate myProdPriceRedis;

    public Set GetZsetValue(String key)  {
        Set myTempSet = new HashSet();
        myTempSet = myProdPriceRedis.opsForZSet().rangeWithScores(key, 0, 9);
        return myTempSet;
    };

    public Set GetZsetValueWithStatus(String key) throws Exception {
        Set myTempSet = new HashSet();
        myTempSet = myProdPriceRedis.opsForZSet().rangeWithScores(key, 0, 9);
        if (myTempSet.size() < 1 ) {
            throw new Exception("The Key doesn't have any member");
        }
        return myTempSet;
    };

    public Set GetZsetValueWithSpecificException(String key) throws Exception {
        Set myTempSet = new HashSet();
        myTempSet = myProdPriceRedis.opsForZSet().rangeWithScores(key, 0, 9);
        if (myTempSet.size() < 1 ) {
            throw new NotFoundException("The Key doesn't exist in redis", HttpStatus.NOT_FOUND);
        }
        return myTempSet;
    };

    public int SetNewProduct(Product newProduct) {
        int rank = 0;
        myProdPriceRedis.opsForZSet().add(newProduct.getProdGrpId(), newProduct.getProductId(), newProduct.getPrice());
        rank = myProdPriceRedis.opsForZSet().rank(newProduct.getProdGrpId(), newProduct.getProductId()).intValue();
        return rank;
    }

    public int SetNewProductGrp(ProductGrp newProductGrp){

        List<Product> product = newProductGrp.getProductList();
        String productId = product.get(0).getProductId();
        double price = product.get(0).getPrice();
        myProdPriceRedis.opsForZSet().add(newProductGrp.getProdGrpId(), productId, price);
        int productCnt = myProdPriceRedis.opsForZSet().zCard(newProductGrp.getProdGrpId()).intValue();
        return productCnt;
    }

    public void DeleteKey (String key) {
        myProdPriceRedis.delete(key);
    }
    public int SetNewProductGrpToKeyword (String keyword, String prodGrpId, double score){
        myProdPriceRedis.opsForZSet().add(keyword, prodGrpId, score);
        return myProdPriceRedis.opsForZSet().rank(keyword, prodGrpId).intValue();
    }

    public Keyword GetLowestPriceProductByKeyword(String keyword) {
        Keyword returnInfo = new Keyword();
        List<ProductGrp> tempProdGrp = new ArrayList<>();
        // keyword 를 통해 ProductGroup 가져오기 (10개)
        tempProdGrp = GetProdGrpUsingKeyword(keyword);

        // 가져온 정보들을 Return 할 Object 에 넣기
        returnInfo.setKeyword(keyword);
        returnInfo.setProductGrpList(tempProdGrp);
        // 해당 Object return
        return returnInfo;
    }

    public List<ProductGrp> GetProdGrpUsingKeyword(String keyword) {

        List<ProductGrp> returnInfo = new ArrayList<>();

        // input 받은 keyword 로 productGrpId를 조회
        List<String> prodGrpIdList = new ArrayList<>();
        prodGrpIdList = List.copyOf(myProdPriceRedis.opsForZSet().reverseRange(keyword, 0, 9));
        //Product tempProduct = new Product();
        List<Product> tempProdList = new ArrayList<>();

        //10개 prodGrpId로 loop
        for (final String prodGrpId : prodGrpIdList) {
            // Loop 타면서 ProductGrpID 로 Product:price 가져오기 (10개)

            ProductGrp tempProdGrp = new ProductGrp();

            Set prodAndPriceList = new HashSet();
            prodAndPriceList = myProdPriceRedis.opsForZSet().rangeWithScores(prodGrpId, 0, 9);
            Iterator<Object> prodPricObj = prodAndPriceList.iterator();

            // loop 타면서 product obj에 bind (10개)
            while (prodPricObj.hasNext()) {
                ObjectMapper objMapper = new ObjectMapper();
                // {"value":00-10111-}, {"score":11000}
                Map<String, Object> prodPriceMap = objMapper.convertValue(prodPricObj.next(), Map.class);
                Product tempProduct = new Product();
                // Product Obj bind
                tempProduct.setProductId(prodPriceMap.get("value").toString()); // prod_id
                tempProduct.setPrice(Double.valueOf(prodPriceMap.get("score").toString()).intValue()); //es 검색된 score
                tempProduct.setProdGrpId(prodGrpId);

                tempProdList.add(tempProduct);
            }
            // 10개 product price 입력완료
            tempProdGrp.setProdGrpId(prodGrpId);
            tempProdGrp.setProductList(tempProdList);
            returnInfo.add(tempProdGrp);
        }

        return returnInfo;
    }

}
