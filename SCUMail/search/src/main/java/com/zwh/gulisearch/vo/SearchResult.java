package com.zwh.gulisearch.vo;

import com.zwh.common.to.es.SkuEsModel;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SearchResult {
    //查询到的所有商品信息
    private List<SkuEsModel> products;
    /**
     * 分页信息
     */
    private Integer pageNum;//当前页码
    private Long total;//总记录数
    private Integer totalPage;//总页码
    private List<Integer> pageNavs;
    private List<BrandVo> brands;//当前查询到的结果，所涉及到的品牌
    private List<AttrVo> attrs;//当前查询到的结果，所涉及到的所有属性
    private List<CatalogVo> catalogs;//当前查询到的结果，所涉及到的所有分类

    //面包屑导航数据
    private List<NavVo> navs = new ArrayList<>();
    private List<Long> attrIds = new ArrayList<>();

}
