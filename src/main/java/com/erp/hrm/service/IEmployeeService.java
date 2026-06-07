package com.erp.hrm.service;

import com.erp.common.result.PageResult;
import com.erp.common.service.IServiceX;
import com.erp.hrm.dto.EmployeeCreateDTO;
import com.erp.hrm.dto.EmployeeQueryDTO;
import com.erp.hrm.dto.EmployeeUpdateDTO;
import com.erp.hrm.entity.EmployeeEntity;
import com.erp.hrm.vo.EmployeeVO;
import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;

/**
 * 员工档案Service接口.
 *
 * @author AI
 */
public interface IEmployeeService extends IServiceX<EmployeeEntity> {

    /**
     * 新增员工档案.
     *
     * @param dto 员工档案创建DTO
     * @return 员工档案VO
     */
    @Transactional(rollbackFor = Exception.class)
    EmployeeVO create(@Valid EmployeeCreateDTO dto);

    /**
     * 更新员工档案.
     *
     * @param id  员工ID
     * @param dto 员工档案更新DTO
     * @return 员工档案VO
     */
    @Transactional(rollbackFor = Exception.class)
    EmployeeVO update(Long id, @Valid EmployeeUpdateDTO dto);

    /**
     * 删除员工档案.
     *
     * @param id 员工ID
     */
    @Transactional(rollbackFor = Exception.class)
    void delete(Long id);

    /**
     * 根据ID查询员工档案详情.
     *
     * @param id 员工ID
     * @return 员工档案VO
     */
    EmployeeVO getById(Long id);

    /**
     * 分页查询员工档案列表.
     *
     * @param query 查询条件DTO
     * @return 分页结果
     */
    PageResult<EmployeeVO> pageList(EmployeeQueryDTO query);
}
