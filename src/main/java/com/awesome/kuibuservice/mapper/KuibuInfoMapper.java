package com.awesome.kuibuservice.mapper;

import com.awesome.kuibuservice.model.dto.TaskDateGroupDto;
import com.awesome.kuibuservice.model.entity.KuibuInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Collection;
import java.util.List;

/**
* @author 82611
* @description 针对表【kuibu_info】的数据库操作Mapper
* @createDate 2025-03-20 13:34:47
* @Entity com.awesome.kuibuservice.model.entity.KuibuInfo
*/
public interface KuibuInfoMapper extends BaseMapper<KuibuInfo> {

    List<TaskDateGroupDto> selectDataGroupByDate(@Param("startDate") String startDate, @Param("taskIds") Collection<Long> taskIds);

}




