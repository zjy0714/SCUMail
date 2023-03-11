package com.zwh.gulisearch.vo;

import lombok.Data;

import java.util.List;

@Data
public class AttrVo {
    private Long AttrId;
    private String attrName;
    private List<String> attrValue;
}
