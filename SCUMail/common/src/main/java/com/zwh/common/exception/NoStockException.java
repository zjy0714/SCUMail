package com.zwh.common.exception;

public class NoStockException extends RuntimeException{
    private Long skuId;

    public NoStockException(){
    }

    public NoStockException(Long skuId){
        super(skuId+"没有足够的库存");
    }

    /**
     * 获取
     * @return skuId
     */
    public Long getSkuId() {
        return skuId;
    }

    /**
     * 设置
     * @param skuId
     */
    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public String toString() {
        return "NoStockException{skuId = " + skuId + "}";
    }
}
