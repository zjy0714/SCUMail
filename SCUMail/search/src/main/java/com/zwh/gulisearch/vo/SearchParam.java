package com.zwh.gulisearch.vo;

import lombok.Data;

import java.util.List;

@Data
public class SearchParam {
    private String keyword;//页面传来的全文匹配关键字
    private Long category3Id;//三级分类ID
    private String sort;//排序条件
    /**
     * 筛选条件
     */
    private Integer hasStock = 1;//是否有货
    private String skuPrice;//价格区域查询
    private List<Long> brandId;//按照品牌名字进行查询，可以多选
    private List<String> attrs;//按照属性查询，可以多选

    private Integer pageNum = 1;//页码

    private String _queryString;//原生的查询条件
}
