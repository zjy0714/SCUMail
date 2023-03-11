package com.zwh.order.vo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
//订单确认页需要用的数据
public class OrderConfirmVo {
    //收货地址
    private List<MemberAddressVo> address;
    //所有选中的购物项
    private List<OrderItemVo> items;
    //发票信息

    //优惠券信息
    private Integer integration;
    //订单总额
    private BigDecimal total;
    //应付价格
    private BigDecimal payPrice;
    //是否有库存
    Map<Long,Boolean> stocks;
    //订单唯一令牌
    @Getter @Setter
    private String orderToken;
    //商品总数量
    public Integer getCount(){
        Integer count = 0;
        if (this.items != null){
            for (OrderItemVo item : items) {
                count += item.getCount();
            }
        }
        return count;
    }

    public OrderConfirmVo() {
    }

    public OrderConfirmVo(List<MemberAddressVo> address, List<OrderItemVo> items, Integer integration, BigDecimal total, BigDecimal payPrice) {
        this.address = address;
        this.items = items;
        this.integration = integration;
        this.total = total;
        this.payPrice = payPrice;
    }

    /**
     * 获取
     * @return address
     */
    public List<MemberAddressVo> getAddress() {
        return address;
    }

    /**
     * 设置
     * @param address
     */
    public void setAddress(List<MemberAddressVo> address) {
        this.address = address;
    }

    /**
     * 获取
     * @return items
     */
    public List<OrderItemVo> getItems() {
        return items;
    }

    /**
     * 设置
     * @param items
     */
    public void setItems(List<OrderItemVo> items) {
        this.items = items;
    }

    /**
     * 获取
     * @return integration
     */
    public Integer getIntegration() {
        return integration;
    }

    /**
     * 设置
     * @param integration
     */
    public void setIntegration(Integer integration) {
        this.integration = integration;
    }

    /**
     * 获取
     * @return total
     */
    public BigDecimal getTotal() {
        BigDecimal sum = new BigDecimal("0");
        if (items != null){
            for (OrderItemVo item : items) {
                BigDecimal multiply = item.getPrice().multiply(new BigDecimal(item.getCount().toString()));
                sum = sum.add(multiply);
            }
        }
        return sum;
    }

    /**
     * 获取
     * @return payPrice
     */
    public BigDecimal getPayPrice() {
        return getTotal();
    }

    public String toString() {
        return "OrderConfirmVo{address = " + address + ", items = " + items + ", integration = " + integration + ", total = " + total + ", payPrice = " + payPrice + "}";
    }
}
