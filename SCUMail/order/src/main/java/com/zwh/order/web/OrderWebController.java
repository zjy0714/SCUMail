package com.zwh.order.web;

import com.zwh.order.service.OrderService;
import com.zwh.order.vo.OrderConfirmVo;
import com.zwh.order.vo.OrderSubmitVo;
import com.zwh.order.vo.SubmitOrderResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.concurrent.ExecutionException;

@Controller
public class OrderWebController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/toTrade")
    public String toTrade(Model model) throws ExecutionException, InterruptedException {
        OrderConfirmVo orderConfirmVo = orderService.confirmOrder();
        model.addAttribute("orderConfirmData",orderConfirmVo);
        return "confirm";
    }

    /**
     * 下单
     * @param vo
     * @return
     */
    @PostMapping("submitOrder")
    public String submitOrder(OrderSubmitVo vo, Model model, RedirectAttributes redirectAttributes){
        SubmitOrderResponseVo responseVo = orderService.submitOrder(vo);

        if(responseVo.getCode() == 0){
            //下单成功来到支付选项页
            model.addAttribute("submitOrderResp",responseVo);
            return "pay";
        }else {
            //下单失败回到确认订单页重新确认信息
            String msg = "下单失败；";
            switch (responseVo.getCode()){
                case 1: msg += "订单信息过期，请重新提交";
                break;
                case 2: msg += "订单商品价格发生变化，请确认后再提交";
                break;
                case 3: msg += "库存不足";
                break;
            }
            redirectAttributes.addFlashAttribute("msg",msg);
            return "redirect:http://order.gulimail.com/toTrade";
        }

    }
}
