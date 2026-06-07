package com.erp.hrm.service;

import com.erp.common.result.PageResult;
import com.erp.common.service.IServiceX;
import com.erp.hrm.dto.RecruitmentCreateDTO;
import com.erp.hrm.dto.RecruitmentQueryDTO;
import com.erp.hrm.dto.RecruitmentUpdateDTO;
import com.erp.hrm.entity.RecruitmentEntity;
import com.erp.hrm.vo.RecruitmentVO;
import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;

/**
 * 招聘管理Service接口.
 *
 * @author AI
 */
public interface IRecruitmentService extends IServiceX<RecruitmentEntity> {

    /**
     * 新增招聘.
     *
     * @param dto 招聘创建DTO
     * @return 招聘VO
     */
    @Transactional(rollbackFor = Exception.class)
    RecruitmentVO create(@Valid RecruitmentCreateDTO dto);

    /**
     * 更新招聘.
     *
     * @param id  招聘ID
     * @param dto 招聘更新DTO
     * @return 招聘VO
     */
    @Transactional(rollbackFor = Exception.class)
    RecruitmentVO update(Long id, @Valid RecruitmentUpdateDTO dto);

    /**
     * 删除招聘.
     *
     * @param id 招聘ID
     */
    @Transactional(rollbackFor = Exception.class)
    void delete(Long id);

    /**
     * 根据ID查询招聘详情.
     *
     * @param id 招聘ID
     * @return 招聘VO
     */
    RecruitmentVO getById(Long id);

    /**
     * 分页查询招聘列表.
     *
     * @param query 查询条件DTO
     * @return 分页结果
     */
    PageResult<RecruitmentVO> pageList(RecruitmentQueryDTO query);
}
