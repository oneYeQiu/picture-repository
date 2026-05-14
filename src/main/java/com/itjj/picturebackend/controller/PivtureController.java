package com.itjj.picturebackend.controller;

import com.itjj.picturebackend.annotation.AuthCheck;
import com.itjj.picturebackend.common.BaseResponse;
import com.itjj.picturebackend.common.ResultUtils;
import com.itjj.picturebackend.constant.UserConstant;
import com.itjj.picturebackend.model.dto.picture.PictureUploadRequest;
import com.itjj.picturebackend.model.entity.User;
import com.itjj.picturebackend.model.vo.PictureVO;
import com.itjj.picturebackend.service.PictureService;
import com.itjj.picturebackend.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/picture")
public class PivtureController {

    @Resource
    private UserService userService;

    @Resource
    private PictureService pictureService;

    /**
     * 上传图片（可重新上传）
     */
    @PostMapping("/upload")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<PictureVO> uploadPicture(
            @RequestPart("file") MultipartFile multipartFile,
            PictureUploadRequest pictureUploadRequest,
            HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        PictureVO pictureVO = pictureService.uploadPicture(multipartFile, pictureUploadRequest, loginUser);
        return ResultUtils.success(pictureVO);
    }


}
