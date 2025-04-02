package com.workshop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.workshop.model.Process;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProcessMapper extends BaseMapper<Process> {
} 