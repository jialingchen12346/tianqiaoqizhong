package com.workshop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.workshop.model.TaskFlow;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TaskFlowMapper extends BaseMapper<TaskFlow> {
} 