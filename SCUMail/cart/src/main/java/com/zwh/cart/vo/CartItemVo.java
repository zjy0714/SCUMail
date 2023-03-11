package com.zwh.cart.vo;

import io.swagger.models.auth.In;

import java.math.BigDecimal;
import java.util.List;

/**
 * 购物项内容
 */
public class CartItemVo {
    private Long skuId;
    private Boolean check=true;
    private String title;
    private String image;
    private List<String> skuAttr;
    private BigDecimal price;
    private Integer count;
    private BigDecimal totalPrice;

    public CartItemVo() {
    }

    public CartItemVo(Long skuId, Boolean check, String title, String image, List<String> skuAttr, BigDecimal price, Integer count, BigDecimal totalPrice) {
        this.skuId = skuId;
        this.check = check;
        this.title = title;
        this.image = image;
        this.skuAttr = skuAttr;
        this.price = price;
        this.count = count;
        this.totalPrice = totalPrice;
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

    /**
     * 获取
     * @return check
     */
    public Boolean getCheck() {
        return check;
    }

    /**
     * 设置
     * @param check
     */
    public void setCheck(Boolean check) {
        this.check = check;
    }

    /**
     * 获取
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * 设置
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 获取
     * @return image
     */
    public String getImage() {
        return image;
    }

    /**
     * 设置
     * @param image
     */
    public void setImage(String image) {
        this.image = image;
    }

    /**
     * 获取
     * @return skuAttr
     */
    public List<String> getSkuAttr() {
        return skuAttr;
    }

    /**
     * 设置
     * @param skuAttr
     */
    public void setSkuAttr(List<String> skuAttr) {
        this.skuAttr = skuAttr;
    }

    /**
     * 获取
     * @return price
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * 设置
     * @param price
     */
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    /**
     * 获取
     * @return count
     */
    public Integer getCount() {
        return count;
    }

    /**
     * 设置
     * @param count
     */
    public void setCount(Integer count) {
        this.count = count;
    }

    /**
     * 获取
     * @return totalPrice
     */
    public BigDecimal getTotalPrice() {
        return this.price.multiply(new BigDecimal(count));
    }

    /**
     * 设置
     * @param totalPrice
     */
    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String toString() {
        return "CartItemVo{skuId = " + skuId + ", check = " + check + ", title = " + title + ", image = " + image + ", skuAttr = " + skuAttr + ", price = " + price + ", count = " + count + ", totalPrice = " + totalPrice + "}";
    }
}
