package com.itjj.picturebackend.service;

import com.itjj.picturebackend.model.dto.picture.PictureUploadRequest;
import com.itjj.picturebackend.model.entity.Picture;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itjj.picturebackend.model.entity.User;
import com.itjj.picturebackend.model.vo.PictureVO;
import org.springframework.web.multipart.MultipartFile;

/**
* @author 36664
* @description 针对表【picture(图片)】的数据库操作Service
* @createDate 2026-05-12 17:20:22
*/
public interface PictureService extends IService<Picture> {

    /**
     * 上传图片
     *
     * @param multipartFile
     * @param pictureUploadRequest
     * @param loginUser
     * @return
     */
    PictureVO uploadPicture(MultipartFile multipartFile,
                            PictureUploadRequest pictureUploadRequest,
                            User loginUser);


}
