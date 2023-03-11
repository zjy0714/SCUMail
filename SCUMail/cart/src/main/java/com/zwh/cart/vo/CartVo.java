package com.zwh.cart.vo;

import java.math.BigDecimal;
import java.util.List;

/**
 * 完整的购物车
 */
public class CartVo {
    private List<CartItemVo> items;
    private Integer countNum;//商品数量
    private Integer countType;//商品类型数量
    private BigDecimal totalAmount;//商品总价
    private BigDecimal reduce = new BigDecimal("0.00");//折扣额

    public CartVo() {
    }

    public CartVo(List<CartItemVo> items, Integer countNum, Integer countType, BigDecimal totalAmount, BigDecimal reduce) {
        this.items = items;
        this.countNum = countNum;
        this.countType = countType;
        this.totalAmount = totalAmount;
        this.reduce = reduce;
    }

    /**
     * 获取
     * @return items
     */
    public List<CartItemVo> getItems() {
        return items;
    }

    /**
     * 设置
     * @param items
     */
    public void setItems(List<CartItemVo> items) {
        this.items = items;
    }

    /**
     * 获取
     * @return countNum
     */
    public Integer getCountNum() {
        int count = 0;
        if(items!=null && items.size()>0){
            for (CartItemVo item : items) {
                count += item.getCount();
            }
        }
        return count;
    }

    /**
     * 设置
     * @param countNum
     */
    public void setCountNum(Integer countNum) {
        this.countNum = countNum;
    }

    /**
     * 获取
     * @return countType
     */
    public Integer getCountType() {
        int count = 0;
        if (items!=null && items.size()>0){
            for (CartItemVo item : items) {
                count++;
            }
        }
        return count;
    }

    /**
     * 获取
     * @return totalAmount
     */
    public BigDecimal getTotalAmount() {
        BigDecimal amount = new BigDecimal("0");
        if(items!=null && items.size()>0){
            for (CartItemVo item : items) {
                if (item.getCheck()){
                    amount = amount.add(item.getTotalPrice());
                }
            }
        }
        return amount.subtract(this.getReduce());
    }

    /**
     * 设置
     * @param totalAmount
     */
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    /**
     * 获取
     * @return reduce
     */
    public BigDecimal getReduce() {
        return reduce;
    }

    /**
     * 设置
     * @param reduce
     */
    public void setReduce(BigDecimal reduce) {
        this.reduce = reduce;
    }

    public String toString() {
        return "CartVo{items = " + items + ", countNum = " + countNum + ", countType = " + countType + ", totalAmount = " + totalAmount + ", reduce = " + reduce + "}";
    }
}
