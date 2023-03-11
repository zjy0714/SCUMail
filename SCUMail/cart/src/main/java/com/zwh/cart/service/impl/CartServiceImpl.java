package com.zwh.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.zwh.cart.feign.ProductFeignService;
import com.zwh.cart.interceptor.CartInterceptor;
import com.zwh.cart.service.CartService;
import com.zwh.cart.to.UserInfoTo;
import com.zwh.cart.vo.CartItemVo;
import com.zwh.cart.vo.CartVo;
import com.zwh.common.utils.R;
import com.zwh.common.vo.SkuInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private ProductFeignService productFeignService;
    @Autowired
    private ThreadPoolExecutor executor;

    public static final String CART_PREFIX = "cart:";

    @Override
    public CartItemVo addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        String item = (String) cartOps.get(skuId.toString());
        if (StringUtils.isEmpty(item)){
            //购物车无商品
            CartItemVo cartItemVo = new CartItemVo();
            //将新商品添加到购物车

            CompletableFuture<Void> getSkuInfoTask = CompletableFuture.runAsync(() -> {
                //远程查询要添加的商品的信息
                R info = productFeignService.info(skuId);
                SkuInfoVo skuInfo = info.getData("skuInfo", new TypeReference<SkuInfoVo>() {
                });

                cartItemVo.setCheck(true);
                cartItemVo.setCount(num);
                cartItemVo.setImage(skuInfo.getSkuDefaultImg());
                cartItemVo.setTitle(skuInfo.getSkuTitle());
                cartItemVo.setSkuId(skuId);
                cartItemVo.setPrice(skuInfo.getPrice());
            },executor);

            //远程查询sku的组合信息
            CompletableFuture<Void> getSkuSaleAttrValues = CompletableFuture.runAsync(() -> {
                List<String> values = productFeignService.getSkuSaleAttrValues(skuId);
                cartItemVo.setSkuAttr(values);
            }, executor);

            CompletableFuture.allOf(getSkuInfoTask,getSkuSaleAttrValues).get();
            String s = JSON.toJSONString(cartItemVo);
            cartOps.put(skuId.toString(),s);
            return cartItemVo;
        }else {
            //购物车有商品
            CartItemVo itemVo = JSON.parseObject(item, CartItemVo.class);
            itemVo.setCount(itemVo.getCount()+num);
            cartOps.put(skuId.toString(),JSON.toJSONString(itemVo));
            return itemVo;
        }
    }

    @Override
    public CartItemVo getCartItem(Long skuId) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        String item = (String) cartOps.get(skuId.toString());
        return JSON.parseObject(item, CartItemVo.class);
    }

    @Override
    public CartVo getCart() throws ExecutionException, InterruptedException {
        CartVo cartVo = new CartVo();
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        if(userInfoTo.getUserId() != null){
            //登录
            String cartKey = CART_PREFIX+userInfoTo.getUserId();
            //如果临时购物车的购物项还没有进行合并
            String tempCartKey = CART_PREFIX+userInfoTo.getUserKey();
            List<CartItemVo> tempCartItems = getCartItems(tempCartKey);
            if(tempCartItems != null){
                //临时购物车有数据
                for (CartItemVo item : tempCartItems) {
                    addToCart(item.getSkuId(), item.getCount());
                }
            }
            //获取登录后的购物车数据
            List<CartItemVo> cartItems = getCartItems(cartKey);
            cartVo.setItems(cartItems);
        }else {
            //未登录
            String cartKey = CART_PREFIX+userInfoTo.getUserKey();
            //获取临时购物车的所有购物项
            List<CartItemVo> cartItems = getCartItems(cartKey);
            cartVo.setItems(cartItems);
        }
        return cartVo;
    }

    /**
     * 获取到要操作的购物车
     * @return
     */
    private BoundHashOperations<String, Object, Object> getCartOps() {
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        String cartKey = "";
        if(userInfoTo.getUserId() != null){
            cartKey = CART_PREFIX + userInfoTo.getUserId();
        }else {
            cartKey = CART_PREFIX + userInfoTo.getUserKey();
        }
        BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(cartKey);
        return operations;
    }

    private List<CartItemVo> getCartItems(String cartKey){
        BoundHashOperations<String, Object, Object> hashOps = redisTemplate.boundHashOps(cartKey);
        List<Object> values = hashOps.values();
        if (values != null && values.size()>0){
            List<CartItemVo> collect = values.stream().map(obj -> {
                String str = obj.toString();
                return JSON.parseObject(str, CartItemVo.class);
            }).collect(Collectors.toList());
            return collect;
        }
        return null;
    }

    /**
     * 清空临时购物车
     */
    @Override
    public void clearTempCart(String cartKey){
        redisTemplate.delete(cartKey);
    }

    @Override
    public void checkItem(Long skuId, Integer check) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        CartItemVo cartItem = getCartItem(skuId);
        cartItem.setCheck(check == 1);
        String s = JSON.toJSONString(cartItem);
        cartOps.put(skuId.toString(),s);
    }

    @Override
    public void changeItemCount(Long skuId, Integer num) {
        CartItemVo cartItem = getCartItem(skuId);
        cartItem.setCount(num);
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        cartOps.put(skuId.toString(),JSON.toJSONString(cartItem));
    }

    @Override
    public void deleteItem(Long skuId) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        cartOps.delete(skuId.toString());
    }

    @Override
    public List<CartItemVo> getUserCartItems() {
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        if(userInfoTo.getUserId() == null){
            return null;
        }else {
            String cartKey = CART_PREFIX + userInfoTo.getUserId();
            List<CartItemVo> cartItems = getCartItems(cartKey);
            if (cartItems != null){
                //获取购物车中被选中的购物项
                return cartItems.stream()
                        .filter(CartItemVo::getCheck)
                        .peek(item -> {
                            //更新为最新价格
                            R price = productFeignService.getPrice(item.getSkuId());
                            String data = (String) price.get("data");
                            item.setPrice(new BigDecimal(data));
                        })
                        .collect(Collectors.toList());
            }
            return null;
        }
    }
}
