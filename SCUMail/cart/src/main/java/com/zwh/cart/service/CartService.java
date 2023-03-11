package com.zwh.cart.service;

import com.zwh.cart.vo.CartItemVo;
import com.zwh.cart.vo.CartVo;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface CartService {
    CartItemVo addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException;

    CartItemVo getCartItem(Long skuId);

    CartVo getCart() throws ExecutionException, InterruptedException;

    /**
     * 清空临时购物车
     * @param cartKey
     */
    void clearTempCart(String cartKey);

    /**
     * 勾选购物项
     * @param skuId
     * @param check
     */
    void checkItem(Long skuId, Integer check);

    /**
     * 修改购物项数量
     * @param skuId
     * @param num
     */
    void changeItemCount(Long skuId, Integer num);

    /**
     * 删除购物项
     * @param skuId
     */
    void deleteItem(Long skuId);

    List<CartItemVo> getUserCartItems();
}
