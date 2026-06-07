package com.erp.module.warehouse.service;

import com.erp.common.result.PageResult;
import com.erp.common.service.IServiceX;
import com.erp.module.warehouse.dto.LocationCreateDTO;
import com.erp.module.warehouse.dto.LocationQueryDTO;
import com.erp.module.warehouse.dto.LocationUpdateDTO;
import com.erp.module.warehouse.entity.LocationEntity;
import com.erp.module.warehouse.vo.LocationVO;
import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;

/**
 * 库位管理Service接口.
 *
 * @author AI
 */
public interface ILocationService extends IServiceX<LocationEntity> {

    /**
     * 新增库位.
     *
     * @param dto 库位创建DTO
     * @return 库位VO
     */
    @Transactional(rollbackFor = Exception.class)
    LocationVO create(@Valid LocationCreateDTO dto);

    /**
     * 更新库位.
     *
     * @param id  库位ID
     * @param dto 库位更新DTO
     * @return 库位VO
     */
    @Transactional(rollbackFor = Exception.class)
    LocationVO update(Long id, @Valid LocationUpdateDTO dto);

    /**
     * 删除库位.
     *
     * @param id 库位ID
     */
    @Transactional(rollbackFor = Exception.class)
    void delete(Long id);

    /**
     * 根据ID查询库位详情.
     *
     * @param id 库位ID
     * @return 库位VO
     */
    LocationVO getById(Long id);

    /**
     * 分页查询库位列表.
     *
     * @param query 查询条件DTO
     * @return 分页结果
     */
    PageResult<LocationVO> pageList(LocationQueryDTO query);
}
