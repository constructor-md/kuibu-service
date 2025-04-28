
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;
use kuibu;

DROP TABLE IF EXISTS `daily_words`;
CREATE TABLE `daily_words`  (
                                `id` bigint NOT NULL,
                                `sentence` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '句子',
                                `author` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '作者',
                                `source` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '来源',
                                `create_time` datetime NOT NULL COMMENT '记录创建时间',
                                PRIMARY KEY (`id`) USING BTREE,
                                UNIQUE INDEX `sentence_unique_index`(`sentence` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;


DROP TABLE IF EXISTS `goal_info`;
CREATE TABLE `goal_info`  (
                              `id` bigint NOT NULL,
                              `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '目标名称',
                              `status` tinyint(1) NOT NULL COMMENT '目标状态 0 - 进行中 1 - 已完成',
                              `user_id` bigint NOT NULL COMMENT '所属用户id',
                              `create_time` datetime NOT NULL COMMENT '记录创建时间',
                              `update_time` datetime NOT NULL COMMENT '记录更新时间',
                              PRIMARY KEY (`id`) USING BTREE,
                              INDEX `user_id_index`(`user_id` ASC) USING BTREE,
                              INDEX `create_time_index`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;


DROP TABLE IF EXISTS `kuibu_info`;
CREATE TABLE `kuibu_info`  (
                               `id` bigint NOT NULL,
                               `task_id` bigint NOT NULL COMMENT '所属任务id',
                               `count` bigint NOT NULL COMMENT '次数 / 时间毫秒数',
                               `user_id` bigint NOT NULL COMMENT '所属用户id',
                               `create_time` datetime NOT NULL COMMENT '记录创建时间',
                               `update_time` datetime NOT NULL COMMENT '记录更新时间',
                               PRIMARY KEY (`id`) USING BTREE,
                               INDEX `user_id_index`(`user_id` ASC) USING BTREE,
                               INDEX `task_id_index`(`task_id` ASC) USING BTREE,
                               INDEX `create_time_index`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;


DROP TABLE IF EXISTS `request_log`;
CREATE TABLE `request_log`  (
                                `id` bigint NOT NULL,
                                `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '请求路径',
                                `method` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '请求方法',
                                `headers` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '请求头 JSON',
                                `body` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '请求体 JSON',
                                `status` tinyint NOT NULL COMMENT '请求是否成功 0 - 成功 1- 失败',
                                `error_code` int NULL DEFAULT NULL COMMENT '错误码',
                                `error_message` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '错误信息',
                                `stack_trace` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '异常堆栈',
                                `client_ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '客户端ip',
                                `user_agent` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '客户端设备或浏览器信息',
                                `request_time` datetime NULL DEFAULT NULL COMMENT '请求时间',
                                `response_time` datetime NULL DEFAULT NULL COMMENT '响应时间',
                                `duration` bigint NULL DEFAULT NULL COMMENT '接口耗时',
                                `create_time` datetime NULL DEFAULT NULL COMMENT '记录创建时间',
                                `update_time` datetime NULL DEFAULT NULL COMMENT '记录更新时间',
                                `user_id` bigint NULL DEFAULT NULL COMMENT '请求用户id',
                                PRIMARY KEY (`id`) USING BTREE,
                                INDEX `user_id_index`(`user_id` ASC) USING BTREE,
                                INDEX `url_index`(`url` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;


DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config`  (
                               `id` bigint NOT NULL,
                               `sys_key` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                               `sys_value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                               `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                               PRIMARY KEY (`id`) USING BTREE,
                               UNIQUE INDEX `key_index`(`sys_key` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;


DROP TABLE IF EXISTS `task_info`;
CREATE TABLE `task_info`  (
                              `id` bigint NOT NULL,
                              `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '任务名',
                              `type` tinyint(1) NOT NULL COMMENT '任务类型 1 - 计时 2 - 计数',
                              `goal_id` bigint NOT NULL COMMENT '所属目标id',
                              `target_amount` bigint NOT NULL COMMENT '任务达成所需的量',
                              `current_progress` bigint NOT NULL COMMENT '任务当前达成的量',
                              `status` tinyint(1) NOT NULL COMMENT '任务状态 0 - 进行中 1 - 已完成',
                              `user_id` bigint NOT NULL COMMENT '所属用户id',
                              `create_time` datetime NOT NULL COMMENT '记录创建时间',
                              `update_time` datetime NOT NULL COMMENT '记录更新时间',
                              PRIMARY KEY (`id`) USING BTREE,
                              INDEX `goal_id_index`(`goal_id` ASC) USING BTREE,
                              INDEX `user_id_index`(`user_id` ASC) USING BTREE,
                              INDEX `create_time_index`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;


DROP TABLE IF EXISTS `user_info`;
CREATE TABLE `user_info`  (
                              `id` bigint NOT NULL,
                              `open_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '微信openid',
                              `session_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '微信session_key',
                              `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户自定义名',
                              `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户自定义头像',
                              `create_time` datetime NOT NULL COMMENT '记录创建时间',
                              `update_time` datetime NOT NULL COMMENT '记录更新时间',
                              PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;


DROP TABLE IF EXISTS `user_suggestion`;
CREATE TABLE `user_suggestion`  (
                                    `id` bigint NOT NULL,
                                    `text` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户建议文本',
                                    `user_id` bigint NOT NULL COMMENT '所属用户',
                                    `create_time` datetime NOT NULL COMMENT '记录创建时间',
                                    PRIMARY KEY (`id`) USING BTREE,
                                    INDEX `user_id_index`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
