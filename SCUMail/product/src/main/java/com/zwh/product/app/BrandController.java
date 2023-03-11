package com.zwh.guliproduct.app;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.zwh.common.valid.AddGroup;
import com.zwh.common.valid.UpdateGroup;
import com.zwh.common.valid.UpdateStatusGroup;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.zwh.guliproduct.entity.BrandEntity;
import com.zwh.guliproduct.service.BrandService;
import com.zwh.common.utils.PageUtils;
import com.zwh.common.utils.R;

import javax.annotation.Resource;


/**
 * 品牌
 *
 * @author zhangwenhao
 * @email z2388058390@163.com
 * @date 2022-09-26 19:41:58
 */
@RestController
@RequestMapping("product/brand")
public class BrandController {
    @Resource
    private BrandService brandService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("guliproduct:brand:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = brandService.queryPage(params);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{brandId}")
    //@RequiresPermissions("guliproduct:brand:info")
    public R info(@PathVariable("brandId") Long brandId){
		BrandEntity brand = brandService.getById(brandId);

        return R.ok().put("brand", brand);
    }

    /**
     * 查询所有的品牌信息
     */
    @GetMapping("/infos")
    public R infos(@RequestParam("brandIds")List<Long> brandIds){
        List<BrandEntity> brandEntities = brandService.getBrandsByIds(brandIds);
        return R.ok().put("brand",brandEntities);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("guliproduct:brand:save")
    public R save(@Validated({AddGroup.class}) @RequestBody BrandEntity brand /*BindingResult result*/){
//        if(result.hasErrors()){
//            Map<String,String> map = new HashMap<>();
//            //获取校验的错误结果
//            result.getFieldErrors().forEach((item) -> {
//                //FiledError获取到的错误提示
//                String message = item.getDefaultMessage();
//                //获取错误的属性的名字
//                String field = item.getField();
//                map.put(field,message);
//            });
//            return R.error(400,"提交的数据不合法").put("data",map);
//        }else {
//
//        }
        brandService.save(brand);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update/status")
    //@RequiresPermissions("guliproduct:brand:update")
    public R updateStatus(@Validated({UpdateStatusGroup.class}) @RequestBody BrandEntity brand){
		brandService.updateById(brand);

        return R.ok();
    }

    @RequestMapping("/update")
    //@RequiresPermissions("guliproduct:brand:update")
    public R update(@Validated({UpdateGroup.class}) @RequestBody BrandEntity brand){
        brandService.updateDetail(brand);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("guliproduct:brand:delete")
    public R delete(@RequestBody Long[] brandIds){
		brandService.removeByIds(Arrays.asList(brandIds));

        return R.ok();
    }

}
