package com.itjj.picturebackend.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itjj.picturebackend.exception.BusinessException;
import com.itjj.picturebackend.exception.ErrorCode;
import com.itjj.picturebackend.exception.ThrowUtils;
import com.itjj.picturebackend.manager.FileManager;
import com.itjj.picturebackend.manager.upload.PictureUploadTemplate;
import com.itjj.picturebackend.mapper.PictureMapper;
import com.itjj.picturebackend.model.dto.file.UploadPictureResult;
import com.itjj.picturebackend.model.dto.picture.*;
import com.itjj.picturebackend.model.entity.Picture;
import com.itjj.picturebackend.model.entity.User;
import com.itjj.picturebackend.model.enums.PictureReviewStatusEnum;
import com.itjj.picturebackend.model.vo.PictureVO;
import com.itjj.picturebackend.service.PictureService;
import com.itjj.picturebackend.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
* @author 36664
* @description 针对表【picture(图片)】的数据库操作Service实现
* @createDate 2026-05-12 17:20:22
*/
@Service
public class PictureServiceImpl extends ServiceImpl<PictureMapper, Picture>
    implements PictureService{


    @Resource
    private FileManager fileManager;

    @Resource
    private UserService userService;


    @Resource
    private UploadFactory uploadFactory;

    /**
     * 上传图片接口
     *
     * @param object               文件/url
     * @param pictureUploadRequest 请求类
     * @param loginUser            当前登录的 user
     * @return picture 的封装对象
     */
    @Override
    public PictureVO uploadPicture(Object object, PictureUploadRequest pictureUploadRequest, User loginUser) {
        checkParam(pictureUploadRequest, loginUser, object);
        Long pictureId = pictureUploadRequest.getId();
        // 使用 factory
        PictureUploadTemplate uploadTemplate = uploadFactory.getUploadFactory(object instanceof MultipartFile ? FileUploadEnum.FILE.getType() : FileUploadEnum.URL.getType());
        String uploadPathPrefix = String.format("public/%s", loginUser.getId());
        UploadPictureResult uploadPictureResult = uploadTemplate.uploadPicture(object, uploadPathPrefix);
        Picture picture = new Picture();
        if (pictureId == null) {
            // 新增
            picture = buildPicture(uploadPictureResult, null, loginUser.getId());
        }
        if (pictureId != null) {
            // 更新
            Picture oldPicture = pictureDAO.getByPictureId(pictureId);
            ThrowUtils.throwIf(ObjectUtils.isEmpty(oldPicture), ErrorCode.NOT_FOUND_ERROR, "图片不存在");
            // 校验权限 如果不是自己或者 admin 就抛出异常
            if (!oldPicture.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }
            picture = buildPicture(uploadPictureResult, oldPicture, loginUser.getId());
        }
        // 补充审核参数
        this.fillReviewParams(picture, loginUser);
        // 更新或者插入
        ThrowUtils.throwIf(!pictureDAO.saveOrUpdate(picture), ErrorCode.SYSTEM_ERROR, "保存失败");
        return PictureVO.objToVo(picture);
    }


    /**
     *  获取查询条件
     *
     * @param pictureQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest) {
        QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
        if (pictureQueryRequest == null) {
            return queryWrapper;
        }
        // 从对象中取值
        Long id = pictureQueryRequest.getId();
        String name = pictureQueryRequest.getName();
        String introduction = pictureQueryRequest.getIntroduction();
        String category = pictureQueryRequest.getCategory();
        List<String> tags = pictureQueryRequest.getTags();
        Long picSize = pictureQueryRequest.getPicSize();
        Integer picWidth = pictureQueryRequest.getPicWidth();
        Integer picHeight = pictureQueryRequest.getPicHeight();
        Double picScale = pictureQueryRequest.getPicScale();
        String picFormat = pictureQueryRequest.getPicFormat();
        String searchText = pictureQueryRequest.getSearchText();
        Long userId = pictureQueryRequest.getUserId();
        String sortField = pictureQueryRequest.getSortField();
        String sortOrder = pictureQueryRequest.getSortOrder();

        Integer reviewStatus = pictureQueryRequest.getReviewStatus();
        String reviewMessage = pictureQueryRequest.getReviewMessage();
        Long reviewerId = pictureQueryRequest.getReviewerId();

        // 从多字段中搜索
        if (StrUtil.isNotBlank(searchText)) {
            // 需要拼接查询条件
            queryWrapper.and(qw -> qw.like("name", searchText)
                    .or()
                    .like("introduction", searchText)
            );
        }
        queryWrapper.eq(ObjUtil.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjUtil.isNotEmpty(userId), "userId", userId);
        queryWrapper.like(StrUtil.isNotBlank(name), "name", name);
        queryWrapper.like(StrUtil.isNotBlank(introduction), "introduction", introduction);
        queryWrapper.like(StrUtil.isNotBlank(picFormat), "picFormat", picFormat);
        queryWrapper.eq(StrUtil.isNotBlank(category), "category", category);
        queryWrapper.eq(ObjUtil.isNotEmpty(picWidth), "picWidth", picWidth);
        queryWrapper.eq(ObjUtil.isNotEmpty(picHeight), "picHeight", picHeight);
        queryWrapper.eq(ObjUtil.isNotEmpty(picSize), "picSize", picSize);
        queryWrapper.eq(ObjUtil.isNotEmpty(picScale), "picScale", picScale);

        queryWrapper.eq(ObjUtil.isNotEmpty(reviewStatus), "reviewStatus", reviewStatus);
        queryWrapper.like(StrUtil.isNotBlank(reviewMessage), "reviewMessage", reviewMessage);
        queryWrapper.eq(ObjUtil.isNotEmpty(reviewerId), "reviewerId", reviewerId);

        // JSON 数组查询
        if (CollUtil.isNotEmpty(tags)) {
            for (String tag : tags) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        // 排序
        queryWrapper.orderBy(StrUtil.isNotEmpty(sortField), sortOrder.equals("ascend"), sortField);
        return queryWrapper;
    }

    /**
     *  对 单条数据 脱敏
     *  获取图片包装类 vo （单条）
     *
     * @param picture
     * @param request
     * @return
     */
    @Override
    public PictureVO getPictureVO(Picture picture, HttpServletRequest request) {
        // picture 对象转为 pictureVO
        PictureVO pictureVO = PictureVO.objToVo(picture);
        // 获取userVO
        User user = userService.getById(picture.getUserId());
        pictureVO.setUser(userService.getUserVO(user));
        return pictureVO;
    }

    /**
     * 分页获取图片封装
     * @param picturePage 是数据库实体的分页结果
     * @param request request 是前端请求对象
     * @return  出参：Page<PictureVO> 是给前端返回的视图对象分页结果
     */
    @Override
    public Page<PictureVO> getPictureVOPage(Page<Picture> picturePage, HttpServletRequest request) {
        List<Picture> pictureList = picturePage.getRecords();
        Page<PictureVO> pictureVOPage = new Page<>(picturePage.getCurrent(), picturePage.getSize(), picturePage.getTotal());
        if (CollUtil.isEmpty(pictureList)) {
            return pictureVOPage;
        }
        // 对象列表 => 封装对象列表
        List<PictureVO> pictureVOList = pictureList.stream().map(PictureVO::objToVo).collect(Collectors.toList());
        // 1. 关联查询用户信息
        Set<Long> userIdSet = pictureList.stream().map(Picture::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 2. 填充信息
        pictureVOList.forEach(pictureVO -> {
            Long userId = pictureVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            pictureVO.setUser(userService.getUserVO(user));
        });
        pictureVOPage.setRecords(pictureVOList);
        return pictureVOPage;
    }


    /**
     * 校验图片实体
     * @param picture 图片实体
     *                校验图片实体是否符合要求
     *                校验失败则抛出异常
     *                校验成功则返回无
     */
    @Override
    public void validPicture(Picture picture) {
        ThrowUtils.throwIf(picture == null, ErrorCode.PARAMS_ERROR);
        // 从对象中取值
        Long id = picture.getId();
        String url = picture.getUrl();
        String introduction = picture.getIntroduction();
        // 修改数据时，id 不能为空，有参数则校验
        ThrowUtils.throwIf(ObjUtil.isNull(id), ErrorCode.PARAMS_ERROR, "id 不能为空");
        if (StrUtil.isNotBlank(url)) {
            ThrowUtils.throwIf(url.length() > 1024, ErrorCode.PARAMS_ERROR, "url 过长");
        }
        if (StrUtil.isNotBlank(introduction)) {
            ThrowUtils.throwIf(introduction.length() > 800, ErrorCode.PARAMS_ERROR, "简介过长");
        }
    }


    /**
     * 图片审核
     * @param pictureReviewRequest 图片审核请求
     * @param loginUser 登录用户
     */
    @Override
    public void doPictureReview(PictureReviewRequest pictureReviewRequest, User loginUser) {
        // 1. 校验参数
        Long id = pictureReviewRequest.getId();
        Integer reviewStatus = pictureReviewRequest.getReviewStatus();
        PictureReviewStatusEnum reviewStatusEnum = PictureReviewStatusEnum.getEnumByValue(reviewStatus);
        if (id == null || reviewStatusEnum == null || PictureReviewStatusEnum.REVIEWING.equals(reviewStatusEnum)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 2. 判断图片是否存在
        Picture oldPicture = this.getById(id);
        ThrowUtils.throwIf(oldPicture == null, ErrorCode.NOT_FOUND_ERROR);
        // 3. 已是该状态，不能重复审核
        if (oldPicture.getReviewStatus().equals(reviewStatus)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请勿重复审核");
        }
        // 4. 更新审核状态， 数据库操作
        Picture updatePicture = new Picture();
        BeanUtils.copyProperties(pictureReviewRequest, updatePicture);
        updatePicture.setReviewerId(loginUser.getId());
        updatePicture.setReviewTime(new Date());
        boolean result = this.updateById(updatePicture);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
    }

    /**
     * 填充审核参数
     * @param picture 图片实体
     * @param loginUser 登录用户
     */
    @Override
    public void fillReviewParams(Picture picture, User loginUser) {
        if (userService.isAdmin(loginUser)) {
            // 管理员自动过审
            picture.setReviewStatus(PictureReviewStatusEnum.PASS.getValue());
            picture.setReviewerId(loginUser.getId());
            picture.setReviewMessage("管理员自动过审");
            picture.setReviewTime(new Date());
        } else {
            // 非管理员，创建或编辑都要改为待审核
            picture.setReviewStatus(PictureReviewStatusEnum.REVIEWING.getValue());
        }
    }


}




