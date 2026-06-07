package com.erp.module.warehouse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.common.annotation.OperLog;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.common.result.PageResult;
import com.erp.module.warehouse.dto.WarehouseCreateDTO;
import com.erp.module.warehouse.dto.WarehouseQueryDTO;
import com.erp.module.warehouse.dto.WarehouseUpdateDTO;
import com.erp.module.warehouse.entity.WarehouseEntity;
import com.erp.module.warehouse.mapper.WarehouseMapper;
import com.erp.module.warehouse.service.IWarehouseService;
import com.erp.module.warehouse.vo.WarehouseVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class WarehouseServiceImpl extends ServiceImpl<WarehouseMapper, WarehouseEntity>
        implements IWarehouseService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperLog(module = "仓库管理", action = "新增", description = "新增仓库")
    public WarehouseVO create(WarehouseCreateDTO dto) {
        validateWarehouseCodeUniqueness(dto.getWarehouseCode(), null);
        WarehouseEntity entity = new WarehouseEntity();
        BeanUtils.copyProperties(dto, entity);
        save(entity);
        return toVO(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperLog(module = "仓库管理", action = "修改", description = "修改仓库")
    public WarehouseVO update(Long id, WarehouseUpdateDTO dto) {
        WarehouseEntity existing = super.getById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "仓库不存在");
        }
        validateWarehouseCodeUniqueness(dto.getWarehouseCode(), id);
        validateStatusTransition(existing.getStatus(), dto.getStatus());
        BeanUtils.copyProperties(dto, existing);
        existing.setId(id);
        updateById(existing);
        return toVO(existing);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperLog(module = "仓库管理", action = "删除", description = "删除仓库")
    public void delete(Long id) {
        WarehouseEntity existing = super.getById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "仓库不存在");
        }
        removeById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public WarehouseVO getById(Long id) {
        WarehouseEntity entity = super.getById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "仓库不存在");
        }
        return toVO(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<WarehouseVO> pageList(WarehouseQueryDTO query) {
        LambdaQueryWrapper<WarehouseEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getWarehouseName()),
                WarehouseEntity::getWarehouseName, query.getWarehouseName());
        wrapper.eq(StringUtils.hasText(query.getWarehouseType()),
                WarehouseEntity::getWarehouseType, query.getWarehouseType());
        wrapper.eq(query.getStatus() != null,
                WarehouseEntity::getStatus, query.getStatus());

        boolean isAsc = "ASC".equalsIgnoreCase(query.getSortOrder());
        if (StringUtils.hasText(query.getSortField())) {
            wrapper.orderBy(true, isAsc, switch (query.getSortField()) {
                case "warehouseCode" -> WarehouseEntity::getWarehouseCode;
                case "warehouseName" -> WarehouseEntity::getWarehouseName;
                case "createTime" -> WarehouseEntity::getCreateTime;
                case "updateTime" -> WarehouseEntity::getUpdateTime;
                default -> WarehouseEntity::getCreateTime;
            });
        } else {
            wrapper.orderByDesc(WarehouseEntity::getCreateTime);
        }

        IPage<WarehouseEntity> page = page(query.toPage(), wrapper);
        return PageResult.of(page.convert(this::toVO));
    }

    private void validateWarehouseCodeUniqueness(String warehouseCode, Long excludeId) {
        if (!StringUtils.hasText(warehouseCode)) {
            return;
        }
        LambdaQueryWrapper<WarehouseEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WarehouseEntity::getWarehouseCode, warehouseCode);
        if (excludeId != null) {
            wrapper.ne(WarehouseEntity::getId, excludeId);
        }
        if (count(wrapper) > 0) {
            throw new BusinessException(ErrorCode.DATA_ALREADY_EXISTS, "仓库编码已存在");
        }
    }

    private void validateStatusTransition(Integer oldStatus, Integer newStatus) {
        if (newStatus == null || newStatus.equals(oldStatus)) {
            return;
        }
        if (oldStatus != null && oldStatus == 0 && newStatus == 1) {
            return;
        }
        if (oldStatus != null && oldStatus == 1 && newStatus == 0) {
            return;
        }
        throw new BusinessException(ErrorCode.DATA_STATUS_INVALID, "仓库状态变更不合法");
    }

    private WarehouseVO toVO(WarehouseEntity entity) {
        WarehouseVO vo = new WarehouseVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}
