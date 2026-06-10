package com.erp.common.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.erp.common.dto.DetailSubTableDTO;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.common.mapper.BaseMapperX;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 通用单据从表服务抽象基类
 * <p>
 * 管理 doc_detail_location / doc_detail_batch / doc_detail_serial 三类从表，
 * 提供统一的保存、删除、查询方法，确保主从表数据一致性。
 * </p>
 *
 * @param <D> 单据明细实体类型
 * @param <L> 库位从表实体类型
 * @param <B> 批次从表实体类型
 * @param <S> 序列号从表实体类型
 * @author AI
 */
public abstract class DetailSubTableService<D, L, B, S> {

    // ==================== Mapper 获取（子类实现） ====================

    protected abstract BaseMapperX<L> getLocationMapper();

    protected abstract BaseMapperX<B> getBatchMapper();

    protected abstract BaseMapperX<S> getSerialMapper();

    // ==================== 从表数据保存（子类实现） ====================

    protected abstract void doSaveLocations(Long detailId, DetailSubTableDTO dto);

    protected abstract void doSaveBatches(Long detailId, DetailSubTableDTO dto);

    protected abstract void doSaveSerials(Long detailId, DetailSubTableDTO dto);

    // ==================== 公共方法 ====================

    /**
     * 保存从表数据（先删后增，事务保证主从表一致性）
     *
     * @param detailId 明细ID
     * @param dto      从表DTO（包含三类从表数据）
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveSubTables(Long detailId, DetailSubTableDTO dto) {
        if (detailId == null || dto == null) {
            return;
        }
        deleteByDetailId(detailId);
        doSaveLocations(detailId, dto);
        doSaveBatches(detailId, dto);
        doSaveSerials(detailId, dto);
    }

    /**
     * 根据明细ID删除所有从表记录
     *
     * @param detailId 明细ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteByDetailId(Long detailId) {
        if (detailId == null) {
            return;
        }
        deleteLocationsByDetailId(detailId);
        deleteBatchesByDetailId(detailId);
        deleteSerialsByDetailId(detailId);
    }

    /**
     * 根据明细ID查询从表数据
     *
     * @param detailId 明细ID
     * @return 从表DTO（包含三类从表数据）
     */
    @SuppressWarnings("unchecked")
    public DetailSubTableDTO getByDetailId(Long detailId) {
        DetailSubTableDTO dto = new DetailSubTableDTO();
        if (detailId == null) {
            return dto;
        }
        dto.setLocations((List) selectLocationsByDetailId(detailId));
        dto.setBatches((List) selectBatchesByDetailId(detailId));
        dto.setSerials((List) selectSerialsByDetailId(detailId));
        return dto;
    }

    // ==================== 业务辅助方法 ====================

    /**
     * 根据明细ID查询库位从表记录.
     *
     * @param detailId 明细ID
     * @return 库位从表实体列表
     */
    protected List<L> selectLocationsByDetailId(Long detailId) {
        return getLocationMapper().selectList(
                new QueryWrapper<L>().eq("detail_id", detailId).eq("is_deleted", false));
    }

    /**
     * 根据明细ID查询批次从表记录.
     *
     * @param detailId 明细ID
     * @return 批次从表实体列表
     */
    protected List<B> selectBatchesByDetailId(Long detailId) {
        return getBatchMapper().selectList(
                new QueryWrapper<B>().eq("detail_id", detailId).eq("is_deleted", false));
    }

    /**
     * 根据明细ID查询序列号从表记录.
     *
     * @param detailId 明细ID
     * @return 序列号从表实体列表
     */
    protected List<S> selectSerialsByDetailId(Long detailId) {
        return getSerialMapper().selectList(
                new QueryWrapper<S>().eq("detail_id", detailId).eq("is_deleted", false));
    }

    /**
     * 根据明细ID删除库位从表记录.
     *
     * @param detailId 明细ID
     */
    protected void deleteLocationsByDetailId(Long detailId) {
        getLocationMapper().delete(
                new QueryWrapper<L>().eq("detail_id", detailId).eq("is_deleted", false));
    }

    /**
     * 根据明细ID删除批次从表记录.
     *
     * @param detailId 明细ID
     */
    protected void deleteBatchesByDetailId(Long detailId) {
        getBatchMapper().delete(
                new QueryWrapper<B>().eq("detail_id", detailId).eq("is_deleted", false));
    }

    /**
     * 根据明细ID删除序列号从表记录.
     *
     * @param detailId 明细ID
     */
    protected void deleteSerialsByDetailId(Long detailId) {
        getSerialMapper().delete(
                new QueryWrapper<S>().eq("detail_id", detailId).eq("is_deleted", false));
    }

    /**
     * 校验库存数量（主表 quantity = Σ location.quantity）.
     *
     * <p>通过 SQL 聚合查询库位从表 quantity 合计，与主表数量比对。
     * 不相等时抛出 {@link BusinessException}。</p>
     *
     * @param mainQuantity 主表数量
     * @param detailId     明细ID
     * @throws BusinessException 数量不一致时抛出
     */
    protected void validateInventoryQuantity(BigDecimal mainQuantity, Long detailId) {
        if (mainQuantity == null || detailId == null) {
            return;
        }
        QueryWrapper<L> wrapper = new QueryWrapper<L>()
                .eq("detail_id", detailId)
                .eq("is_deleted", false)
                .select("COALESCE(SUM(quantity), 0) AS total_quantity");
        List<Map<String, Object>> maps = getLocationMapper().selectMaps(wrapper);
        if (maps != null && !maps.isEmpty() && maps.get(0) != null) {
            Object totalObj = maps.get(0).get("total_quantity");
            if (totalObj != null) {
                BigDecimal sumQuantity = new BigDecimal(totalObj.toString());
                if (mainQuantity.compareTo(sumQuantity) != 0) {
                    throw new BusinessException(ErrorCode.BUSINESS_ERROR);
                }
            }
        }
    }

    /**
     * 判断从表DTO是否无数据.
     *
     * @param dto 从表DTO
     * @return true 无数据
     */
    protected boolean isSubTableDataEmpty(DetailSubTableDTO dto) {
        return dto == null || dto.isEmpty();
    }
}
