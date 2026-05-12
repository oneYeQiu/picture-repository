package com.itjj.picturebackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itjj.picturebackend.model.entity.Picture;
import com.itjj.picturebackend.service.PictureService;
import com.itjj.picturebackend.mapper.PictureMapper;
import org.springframework.stereotype.Service;

/**
* @author 36664
* @description 针对表【picture(图片)】的数据库操作Service实现
* @createDate 2026-05-12 17:20:22
*/
@Service
public class PictureServiceImpl extends ServiceImpl<PictureMapper, Picture>
    implements PictureService{

}




