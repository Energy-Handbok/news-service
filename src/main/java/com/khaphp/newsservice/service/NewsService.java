package com.khaphp.newsservice.service;

import com.khaphp.common.dto.ResponseObject;
import com.khaphp.newsservice.dto.NewsDTOcreate;
import com.khaphp.newsservice.dto.NewsDTOupdate;
import org.springframework.web.multipart.MultipartFile;

public interface NewsService {

    ResponseObject<Object> getAll(int pageSize, int pageIndex);
    ResponseObject<Object> getDetail(String id);
    ResponseObject<Object> create(NewsDTOcreate object);
    ResponseObject<Object> update(NewsDTOupdate object);
    ResponseObject<Object> updateImage(String id, MultipartFile file);
    ResponseObject<Object> delete(String id);
}
