create database if not exists picture;
use picture;


-- drop table if exists user;

create table if not exists user(
    id              bigint auto_increment primary key comment '用户ID',
    userName        varchar(256)       null comment '用户名',
    userAccount     varchar(256)       not null comment '账号',
    userPassword    varchar(256)       not null comment '密码',
    userProfile     varchar(1024)      null comment '用户简介',
    userRole        varchar(256)       not null default 'user' comment '用户角色',
    userAvatar      varchar(1024)      null comment '用户头像',
    editTime        datetime           default current_timestamp not null comment '编辑时间',
    CreateTime      datetime           default current_timestamp not null comment '创建时间',
    UpdateTime      datetime           default current_timestamp not null on update current_timestamp comment '更新时间',
    isDelete        tinyint            default 0 not null comment '是否删除',
    UNIQUE KEY uk_user_account(userAccount),
    INDEX idx_userName(userName)
    ) comment '用户表' collate = utf8mb4_unicode_ci;