-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `real_name` varchar(50) COMMENT '真实姓名',
  `phone` varchar(20) COMMENT '手机号',
  `open_id` varchar(50) COMMENT '微信openId',
  `role` varchar(20) NOT NULL COMMENT '角色 SUPERVISOR:主管,WORKER:员工,QC:质检',
  `department` varchar(50) COMMENT '所属部门',
  `workshop` varchar(50) COMMENT '所属车间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 工序表
CREATE TABLE IF NOT EXISTS `process` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `process_name` varchar(100) NOT NULL COMMENT '工序名称',
  `description` varchar(500) COMMENT '工序描述',
  `order_num` int NOT NULL COMMENT '工序顺序',
  `department` varchar(50) COMMENT '所属部门',
  `estimated_time` int COMMENT '预计耗时(分钟)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_process_name` (`process_name`, `department`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工序表';

-- 任务表
CREATE TABLE IF NOT EXISTS `task` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `task_no` varchar(50) NOT NULL COMMENT '任务编号',
  `task_name` varchar(100) NOT NULL COMMENT '任务名称',
  `description` text COMMENT '任务描述',
  `status` varchar(20) NOT NULL COMMENT '任务状态',
  `priority` int NOT NULL COMMENT '优先级',
  `current_process_id` bigint(20) COMMENT '当前工序ID',
  `attachment_url` varchar(500) COMMENT '附件URL',
  `assigner_id` bigint(20) COMMENT '分派人ID',
  `worker_id` bigint(20) COMMENT '工人ID',
  `deadline` datetime COMMENT '截止时间',
  `qc_result` varchar(20) COMMENT '质检结果',
  `qc_remark` varchar(500) COMMENT '质检备注',
  `workshop` varchar(50) COMMENT '所属车间',
  `drawing_url` varchar(500) COMMENT '图纸URL',
  `drawing_version` varchar(50) COMMENT '图纸版本',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_task_no` (`task_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务表';

-- 任务流转记录表
CREATE TABLE IF NOT EXISTS `task_flow` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `task_id` bigint(20) NOT NULL COMMENT '任务ID',
  `process_id` bigint(20) COMMENT '工序ID',
  `operator_id` bigint(20) NOT NULL COMMENT '操作人ID',
  `action` varchar(20) NOT NULL COMMENT '操作类型',
  `remark` varchar(500) COMMENT '备注',
  `action_time` datetime NOT NULL COMMENT '操作时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `deleted` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_task_id` (`task_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务流转记录表';

-- 导入日志表
CREATE TABLE IF NOT EXISTS `import_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `file_name` varchar(200) NOT NULL COMMENT '文件名',
  `importer_id` bigint(20) NOT NULL COMMENT '导入人ID',
  `total_count` int NOT NULL DEFAULT '0' COMMENT '总记录数',
  `success_count` int NOT NULL DEFAULT '0' COMMENT '成功数',
  `fail_count` int NOT NULL DEFAULT '0' COMMENT '失败数',
  `error_log` text COMMENT '错误日志',
  `status` varchar(20) NOT NULL COMMENT '状态',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `deleted` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='导入日志表';

-- 通知表
CREATE TABLE IF NOT EXISTS `notification` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL COMMENT '接收用户ID',
  `type` varchar(20) NOT NULL COMMENT '通知类型',
  `title` varchar(100) NOT NULL COMMENT '通知标题',
  `content` varchar(500) NOT NULL COMMENT '通知内容',
  `related_id` bigint(20) COMMENT '相关任务/工序ID',
  `is_read` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否已读',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `deleted` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知表';

-- 其他表的建表语句... 