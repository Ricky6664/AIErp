package com.erp.hrm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.erp.hrm.entity.EmployeeEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 员工档案Mapper.
 *
 * @author AI
 */
@Mapper
public interface EmployeeMapper extends BaseMapper<EmployeeEntity> {
}
