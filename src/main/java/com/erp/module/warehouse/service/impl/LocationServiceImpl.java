package com.erp.module.warehouse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.common.annotation.OperLog;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.common.result.PageResult;
import com.erp.module.warehouse.dto.LocationCreateDTO;
import com.erp.module.warehouse.dto.LocationQueryDTO;
import com.erp.module.warehouse.dto.LocationUpdateDTO;
import com.erp.module.warehouse.entity.LocationEntity;
import com.erp.module.warehouse.mapper.LocationMapper;
import com.erp.module.warehouse.service.ILocationService;
import com.erp.module.warehouse.vo.LocationVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class LocationServiceImpl extends ServiceImpl<LocationMapper, LocationEntity>
        implements ILocationService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperLog(module = "库位管理", action = "新增", description = "新增库位")
    public LocationVO create(LocationCreateDTO dto) {
        validateLocationCodeUniqueness(dto.getWarehouseId(), dto.getLocationCode(), null);
        LocationEntity entity = new LocationEntity();
        BeanUtils.copyProperties(dto, entity);
        save(entity);
        return toVO(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperLog(module = "库位管理", action = "修改", description = "修改库位")
    public LocationVO update(Long id, LocationUpdateDTO dto) {
        LocationEntity existing = super.getById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "库位不存在");
        }
        validateLocationCodeUniqueness(dto.getWarehouseId(), dto.getLocationCode(), id);
        validateStatusTransition(existing.getStatus(), dto.getStatus());
        BeanUtils.copyProperties(dto, existing);
        existing.setId(id);
        updateById(existing);
        return toVO(existing);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperLog(module = "库位管理", action = "删除", description = "删除库位")
    public void delete(Long id) {
        LocationEntity existing = super.getById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "库位不存在");
        }
        removeById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public LocationVO getById(Long id) {
        LocationEntity entity = super.getById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "库位不存在");
        }
        return toVO(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<LocationVO> pageList(LocationQueryDTO query) {
        LambdaQueryWrapper<LocationEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(query.getWarehouseId() != null,
                LocationEntity::getWarehouseId, query.getWarehouseId());
        wrapper.like(StringUtils.hasText(query.getLocationCode()),
                LocationEntity::getLocationCode, query.getLocationCode());
        wrapper.like(StringUtils.hasText(query.getLocationName()),
                LocationEntity::getLocationName, query.getLocationName());
        wrapper.eq(StringUtils.hasText(query.getLocationType()),
                LocationEntity::getLocationType, query.getLocationType());
        wrapper.eq(query.getStatus() != null,
                LocationEntity::getStatus, query.getStatus());

        boolean isAsc = "ASC".equalsIgnoreCase(query.getSortOrder());
        if (StringUtils.hasText(query.getSortField())) {
            wrapper.orderBy(true, isAsc, switch (query.getSortField()) {
                case "locationCode" -> LocationEntity::getLocationCode;
                case "locationName" -> LocationEntity::getLocationName;
                case "locationType" -> LocationEntity::getLocationType;
                case "sortOrder" -> LocationEntity::getSortOrder;
                case "createTime" -> LocationEntity::getCreateTime;
                case "updateTime" -> LocationEntity::getUpdateTime;
                default -> LocationEntity::getCreateTime;
            });
        } else {
            wrapper.orderByAsc(LocationEntity::getSortOrder);
        }

        IPage<LocationEntity> page = page(query.toPage(), wrapper);
        return PageResult.of(page.convert(this::toVO));
    }

    private void validateLocationCodeUniqueness(Long warehouseId, String locationCode, Long excludeId) {
        if (!StringUtils.hasText(locationCode) || warehouseId == null) {
            return;
        }
        LambdaQueryWrapper<LocationEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LocationEntity::getWarehouseId, warehouseId);
        wrapper.eq(LocationEntity::getLocationCode, locationCode);
        if (excludeId != null) {
            wrapper.ne(LocationEntity::getId, excludeId);
        }
        if (count(wrapper) > 0) {
            throw new BusinessException(ErrorCode.DATA_ALREADY_EXISTS, "库位编码在该仓库内已存在");
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
        throw new BusinessException(ErrorCode.DATA_STATUS_INVALID, "库位状态变更不合法");
    }

    private LocationVO toVO(LocationEntity entity) {
        LocationVO vo = new LocationVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}
