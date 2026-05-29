package com.erp.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.common.result.PageResult;
import com.erp.common.service.BaseCrudService;
import com.erp.system.dto.SysDataViewDTO;
import com.erp.system.entity.SysDataView;
import com.erp.system.entity.SysDataViewField;
import com.erp.system.mapper.SysDataViewFieldMapper;
import com.erp.system.vo.SysDataViewFieldVO;
import com.erp.system.vo.SysDataViewVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 数据视图 Service.
 *
 * @author AI
 * @since 2026-05-30
 */
public abstract class SysDataViewService
        extends BaseCrudService<SysDataView, SysDataViewDTO.CreateDTO, SysDataViewDTO.UpdateDTO, SysDataViewVO.DetailVO> {

    @Autowired
    protected SysDataViewFieldMapper fieldMapper;

    // ========== 转换方法 ==========

    @Override
    protected SysDataView toEntity(SysDataViewDTO.CreateDTO dto) {
        SysDataView entity = new SysDataView();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }

    @Override
    protected void updateEntity(SysDataViewDTO.UpdateDTO dto, SysDataView entity) {
        if (dto.getViewName() != null) {
            entity.setViewName(dto.getViewName());
        }
        if (dto.getSourceTable() != null) {
            entity.setSourceTable(dto.getSourceTable());
        }
        if (dto.getSourceType() != null) {
            entity.setSourceType(dto.getSourceType());
        }
        if (dto.getSourceSql() != null) {
            entity.setSourceSql(dto.getSourceSql());
        }
        if (dto.getDescription() != null) {
            entity.setDescription(dto.getDescription());
        }
    }

    @Override
    protected SysDataViewVO.DetailVO toVO(SysDataView entity) {
        SysDataViewVO.DetailVO vo = new SysDataViewVO.DetailVO();
        BeanUtils.copyProperties(entity, vo);
        if (entity.getSourceType() != null) {
            vo.setSourceTypeName(entity.getSourceType() == 1 ? "表" : entity.getSourceType() == 2 ? "SQL" : "");
        }
        return vo;
    }

    // ========== CRUD 覆写 ==========

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysDataViewVO.DetailVO create(SysDataViewDTO.CreateDTO dto) {
        validateViewCodeUnique(dto.getViewCode(), null);
        validateSourceSql(dto.getSourceType(), dto.getSourceSql());
        SysDataView entity = toEntity(dto);
        save(entity);
        return toVO(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysDataViewVO.DetailVO update(Long id, SysDataViewDTO.UpdateDTO dto) {
        SysDataView entity = getBaseMapper().selectById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }
        validateSourceSql(dto.getSourceType(), dto.getSourceSql());
        updateEntity(dto, entity);
        updateById(entity);
        return toVO(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Long id) {
        deleteFieldsByViewId(id);
        return removeById(id);
    }

    @Override
    public SysDataViewVO.DetailVO getById(Long id) {
        SysDataView entity = getBaseMapper().selectById(id);
        if (entity == null) {
            return null;
        }
        SysDataViewVO.DetailVO vo = toVO(entity);
        vo.setFields(queryFieldsByViewId(id));
        return vo;
    }

    // ========== 唯一性校验 ==========

    protected void validateViewCodeUnique(String viewCode, Long excludeId) {
        LambdaQueryWrapper<SysDataView> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDataView::getViewCode, viewCode);
        if (excludeId != null) {
            wrapper.ne(SysDataView::getId, excludeId);
        }
        if (getBaseMapper().selectCount(wrapper) > 0) {
            throw new BusinessException(ErrorCode.DATA_ALREADY_EXISTS, "视图编码已存在: " + viewCode);
        }
    }

    // ========== source_sql 校验 ==========

    private static final Set<String> FORBIDDEN_SQL_KEYWORDS = Set.of(
            "DROP", "DELETE", "UPDATE", "INSERT", "ALTER", "TRUNCATE", "CREATE",
            "EXEC", "EXECUTE", "MERGE", "GRANT", "REVOKE"
    );

    protected void validateSourceSql(Integer sourceType, String sourceSql) {
        if (sourceType == null || sourceType != 2 || sourceSql == null || sourceSql.isBlank()) {
            return;
        }
        String upperSql = sourceSql.toUpperCase().trim();
        for (String keyword : FORBIDDEN_SQL_KEYWORDS) {
            if (upperSql.contains(keyword)) {
                throw new BusinessException(ErrorCode.PARAM_INVALID, "SQL包含禁止的关键字: " + keyword);
            }
        }
        if (!upperSql.contains("SELECT") || !upperSql.contains("FROM")) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "SQL必须包含SELECT和FROM子句");
        }
    }

    // ========== 字段名转义（防注入） ==========

    protected String escapeFieldName(String fieldName) {
        if (fieldName == null || fieldName.isBlank()) {
            return fieldName;
        }
        return "\"" + fieldName.replace("\"", "\"\"") + "\"";
    }

    // ========== Field 辅助方法 ==========

    protected void deleteFieldsByViewId(Long viewId) {
        LambdaQueryWrapper<SysDataViewField> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDataViewField::getViewId, viewId);
        fieldMapper.delete(wrapper);
    }

    protected List<SysDataViewFieldVO.DetailVO> queryFieldsByViewId(Long viewId) {
        LambdaQueryWrapper<SysDataViewField> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDataViewField::getViewId, viewId);
        wrapper.orderByAsc(SysDataViewField::getFieldOrder);
        List<SysDataViewField> fields = fieldMapper.selectList(wrapper);
        if (fields == null || fields.isEmpty()) {
            return new ArrayList<>();
        }
        return fields.stream().map(f -> {
            SysDataViewFieldVO.DetailVO fvo = new SysDataViewFieldVO.DetailVO();
            BeanUtils.copyProperties(f, fvo);
            fvo.setFieldTypeName(f.getFieldType());
            return fvo;
        }).collect(Collectors.toList());
    }

    // ========== 自定义方法（子类实现） ==========

    /**
     * 获取视图字段元数据.
     *
     * @param viewCode 视图编码
     * @return 视图详情（含字段列表）
     */
    public abstract SysDataViewVO.DetailVO getViewMeta(String viewCode);

    /**
     * 执行动态查询.
     *
     * @param viewCode    视图编码
     * @param queryParams 查询参数（含分页/排序/过滤）
     * @return 分页结果
     */
    public abstract PageResult<Map<String, Object>> executeView(String viewCode, Map<String, Object> queryParams);
}
