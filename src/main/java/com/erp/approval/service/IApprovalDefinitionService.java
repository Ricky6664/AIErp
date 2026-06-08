package com.erp.approval.service;

import com.erp.approval.dto.DefinitionCreateDTO;
import com.erp.approval.dto.DefinitionQueryDTO;
import com.erp.approval.dto.DefinitionUpdateDTO;
import com.erp.approval.entity.ApprovalDefinitionEntity;
import com.erp.approval.vo.DefinitionDetailVO;
import com.erp.approval.vo.DefinitionListVO;
import com.erp.common.result.PageResult;
import com.erp.common.service.IServiceX;
import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;

/**
 * 审批定义Service接口.
 *
 * @author AI
 */
public interface IApprovalDefinitionService extends IServiceX<ApprovalDefinitionEntity> {

    /**
     * 分页查询审批定义列表.
     *
     * @param query 查询条件DTO
     * @return 分页结果
     */
    PageResult<DefinitionListVO> pageList(DefinitionQueryDTO query);

    /**
     * 根据ID查询审批定义详情.
     *
     * @param id 审批定义ID
     * @return 审批定义详情VO
     */
    DefinitionDetailVO getById(Long id);

    /**
     * 新增审批定义.
     *
     * @param dto 审批定义创建DTO
     * @return 审批定义ID
     */
    @Transactional(rollbackFor = Exception.class)
    Long create(@Valid DefinitionCreateDTO dto);

    /**
     * 修改审批定义.
     *
     * @param id  审批定义ID
     * @param dto 审批定义更新DTO
     */
    @Transactional(rollbackFor = Exception.class)
    void update(Long id, @Valid DefinitionUpdateDTO dto);

    /**
     * 删除审批定义.
     *
     * @param id 审批定义ID
     */
    @Transactional(rollbackFor = Exception.class)
    void delete(Long id);
}
