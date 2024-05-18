package com.khaphp.newsservice.service;

import com.khaphp.common.dto.ResponseObject;
import com.khaphp.common.entity.UserSystem;
import com.khaphp.newsservice.call.UserServiceCall;
import com.khaphp.newsservice.dto.NewsDTOcreate;
import com.khaphp.newsservice.dto.NewsDTOupdate;
import com.khaphp.newsservice.entity.News;
import com.khaphp.newsservice.exception.ObjectNotFound;
import com.khaphp.newsservice.repo.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsServiceImpl implements NewsService {
    public static final String SUCCESS_MSG = "Success";
    public static final String OBJECT_NOT_FOUND_MSG = "object not found";
    public static final String EXCEPTION_MSG = "Exception: ";
    private final NewsRepository newsRepository;
    private final UserServiceCall userServiceCall;
    private final ModelMapper modelMapper;
    private final FileStore fileStore;

    @Value("${aws.s3.link_bucket}")
    private String linkBucket;

    @Value("${logo.energy_handbook.name}")
    private String logoName;
    @Override
    public ResponseObject<Object> getAll(int pageSize, int pageIndex) {
        Page<News> objListPage = null;
        List<News> objList = null;
        int totalPage = 0;
        //paging
        if(pageSize > 0 && pageIndex > 0){
            objListPage = newsRepository.findAll(PageRequest.of(pageIndex - 1, pageSize));  //vì current page ở code nó start = 0, hay bên ngoài la 2pga đầu tiên hay 1
            if(objListPage != null){
                totalPage = objListPage.getTotalPages();
                objList = objListPage.getContent();
            }
        }else{ //get all
            objList = newsRepository.findAll();
            pageIndex = 1;
        }
        objList.forEach(object -> object.setImg(linkBucket + object.getImg()));
        return ResponseObject.builder()
                .code(200).message(SUCCESS_MSG)
                .pageSize(objList.size()).pageIndex(pageIndex).totalPage(totalPage)
                .data(objList)
                .build();
    }

    @Override
    public ResponseObject<Object> getDetail(String id) {
        try{
            News object = newsRepository.findById(id).orElse(null);
            if(object == null) {
                throw new ObjectNotFound(OBJECT_NOT_FOUND_MSG);
            }
            object.setImg(linkBucket + object.getImg());
            return ResponseObject.builder()
                    .code(200)
                    .message("Found")
                    .data(object)
                    .build();
        }catch (Exception e){
            return ResponseObject.builder()
                    .code(400)
                    .message(EXCEPTION_MSG + e.getMessage())
                    .build();
        }
    }

    @Override
    public ResponseObject<Object> create(NewsDTOcreate object) {
        try{
            UserSystem userSystem = userServiceCall.getObject(object.getEmployeeId());
            if(userSystem == null){
                throw new ObjectNotFound("user not found");
            }
            News news = modelMapper.map(object, News.class);
            news.setUpdateDate(new Date(System.currentTimeMillis()));
            news.setEmployeeId(userSystem.getId());
            news.setImg(logoName);
            newsRepository.save(news);
            return ResponseObject.builder()
                    .code(200)
                    .message(SUCCESS_MSG)
                    .data(news)
                    .build();
        }catch (Exception e){
            return ResponseObject.builder()
                    .code(400)
                    .message(EXCEPTION_MSG + e.getMessage())
                    .build();
        }
    }

    @Override
    public ResponseObject<Object> update(NewsDTOupdate object) {
        try{
            News object1 = newsRepository.findById(object.getId()).orElse(null);
            if(object1 == null) {
                throw new ObjectNotFound(OBJECT_NOT_FOUND_MSG);
            }
            object1.setTitle(object.getTitle());
            object1.setBody(object.getBody());
            object1.setUpdateDate(new Date(System.currentTimeMillis()));
            newsRepository.save(object1);
            return ResponseObject.builder()
                    .code(200)
                    .message(SUCCESS_MSG)
                    .build();
        }catch (Exception e){
            return ResponseObject.builder()
                    .code(400)
                    .message(EXCEPTION_MSG + e.getMessage())
                    .build();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseObject<Object> updateImage(String id, MultipartFile file) {
        try{
            News object = newsRepository.findById(id).orElse(null);
            if(object == null) {
                throw new ObjectNotFound(OBJECT_NOT_FOUND_MSG);
            }
            if(!object.getImg().equals(logoName)){
                fileStore.deleteImage(object.getImg());
            }
            //upload new img
            object.setImg(fileStore.uploadImg(file));
            newsRepository.save(object);
            return ResponseObject.builder()
                    .code(200)
                    .message(SUCCESS_MSG)
                    .build();
        }catch (Exception e){
            return ResponseObject.builder()
                    .code(400)
                    .message(EXCEPTION_MSG + e.getMessage())
                    .build();
        }
    }

    @Override
    public ResponseObject<Object> delete(String id) {
        try{
            News object = newsRepository.findById(id).orElse(null);
            if(object == null) {
                throw new ObjectNotFound(OBJECT_NOT_FOUND_MSG);
            }
            if(!object.getImg().equals(logoName)){
                fileStore.deleteImage(object.getImg());
            }
            newsRepository.delete(object);
            return ResponseObject.builder()
                    .code(200)
                    .message(SUCCESS_MSG)
                    .build();
        }catch (Exception e){
            return ResponseObject.builder()
                    .code(400)
                    .message(EXCEPTION_MSG + e.getMessage())
                    .build();
        }
    }
}
