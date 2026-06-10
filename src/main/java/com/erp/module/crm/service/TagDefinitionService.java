package com.erp.module.crm.service;

import com.erp.common.result.PageResult;
import com.erp.common.service.IServiceX;
import com.erp.module.crm.dto.TagDefinitionDTO;
import com.erp.module.crm.dto.TagDefinitionQueryDTO;
import com.erp.module.crm.entity.TagDefinitionEntity;
import com.erp.module.crm.vo.TagDefinitionVO;
import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;

/**
 * 标签定义Service接口.
 *
 * @author AI
 */
public interface TagDefinitionService extends IServiceX<TagDefinitionEntity> {

    /**
     * 分页查询标签定义列表.
     *
     * @param query 查询条件DTO
     * @return 分页结果
     */
    PageResult<TagDefinitionVO> list(TagDefinitionQueryDTO query);

    /**
     * 根据ID查询标签定义详情.
     *
     * @param id 标签定义ID
     * @return 标签定义VO
     */
    TagDefinitionVO getById(Long id);

    /**
     * 新增标签定义.
     *
     * @param dto 标签定义DTO
     */
    @Transactional(rollbackFor = Exception.class)
    void save(@Valid TagDefinitionDTO dto);

    /**
     * 修改标签定义.
     *
     * @param id  标签定义ID
     * @param dto 标签定义DTO
     */
    @Transactional(rollbackFor = Exception.class)
    void update(Long id, @Valid TagDefinitionDTO dto);

    /**
     * 删除标签定义.
     *
     * @param id 标签定义ID
     */
    @Transactional(rollbackFor = Exception.class)
    void delete(Long id);
}
