package com.zwh.gulisearch.service;

import com.zwh.gulisearch.vo.SearchParam;
import com.zwh.gulisearch.vo.SearchResult;

public interface MallSearchService {
    SearchResult search(SearchParam param);
}
