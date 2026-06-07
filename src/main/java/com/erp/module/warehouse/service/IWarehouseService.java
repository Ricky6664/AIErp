package com.erp.module.warehouse.service;

import com.erp.common.result.PageResult;
import com.erp.common.service.IServiceX;
import com.erp.module.warehouse.dto.WarehouseCreateDTO;
import com.erp.module.warehouse.dto.WarehouseQueryDTO;
import com.erp.module.warehouse.dto.WarehouseUpdateDTO;
import com.erp.module.warehouse.entity.WarehouseEntity;
import com.erp.module.warehouse.vo.WarehouseVO;
import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;

/**
 * 仓库定义Service接口
 *
 * @author AI
 */
public interface IWarehouseService extends IServiceX<WarehouseEntity> {

    /**
     * 新增仓库
     *
     * @param dto 仓库创建DTO
     * @return 仓库VO
     */
    @Transactional(rollbackFor = Exception.class)
    WarehouseVO create(@Valid WarehouseCreateDTO dto);

    /**
     * 更新仓库
     *
     * @param id  仓库ID
     * @param dto 仓库更新DTO
     * @return 仓库VO
     */
    @Transactional(rollbackFor = Exception.class)
    WarehouseVO update(Long id, @Valid WarehouseUpdateDTO dto);

    /**
     * 删除仓库
     *
     * @param id 仓库ID
     */
    @Transactional(rollbackFor = Exception.class)
    void delete(Long id);

    /**
     * 根据ID查询仓库详情
     *
     * @param id 仓库ID
     * @return 仓库VO
     */
    WarehouseVO getById(Long id);

    /**
     * 分页查询仓库列表
     *
     * @param query 查询条件DTO
     * @return 分页结果
     */
    PageResult<WarehouseVO> pageList(WarehouseQueryDTO query);
}
