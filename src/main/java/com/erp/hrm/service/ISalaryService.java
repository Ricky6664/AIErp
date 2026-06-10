package com.erp.hrm.service;

import com.erp.common.result.PageResult;
import com.erp.common.service.IServiceX;
import com.erp.hrm.dto.SalaryCreateDTO;
import com.erp.hrm.dto.SalaryQueryDTO;
import com.erp.hrm.dto.SalaryUpdateDTO;
import com.erp.hrm.entity.SalaryEntity;
import com.erp.hrm.vo.SalaryVO;
import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;

/**
 * 薪资管理Service接口.
 *
 * @author AI
 */
public interface ISalaryService extends IServiceX<SalaryEntity> {

    /**
     * 新增薪资记录.
     *
     * @param dto 薪资创建DTO
     * @return 薪资VO
     */
    @Transactional(rollbackFor = Exception.class)
    SalaryVO create(@Valid SalaryCreateDTO dto);

    /**
     * 更新薪资记录.
     *
     * @param id  薪资ID
     * @param dto 薪资更新DTO
     * @return 薪资VO
     */
    @Transactional(rollbackFor = Exception.class)
    SalaryVO update(Long id, @Valid SalaryUpdateDTO dto);

    /**
     * 删除薪资记录.
     *
     * @param id 薪资ID
     */
    @Transactional(rollbackFor = Exception.class)
    void delete(Long id);

    /**
     * 根据ID查询薪资详情.
     *
     * @param id 薪资ID
     * @return 薪资VO
     */
    SalaryVO getById(Long id);

    /**
     * 分页查询薪资列表.
     *
     * @param query 查询条件DTO
     * @return 分页结果
     */
    PageResult<SalaryVO> pageList(SalaryQueryDTO query);
}
