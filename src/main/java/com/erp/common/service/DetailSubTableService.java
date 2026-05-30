package com.erp.common.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.erp.common.dto.DetailSubTableDTO;
import com.erp.common.mapper.BaseMapperX;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

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
        getLocationMapper().delete(
                new QueryWrapper<L>().eq("detail_id", detailId).eq("is_deleted", false));
        getBatchMapper().delete(
                new QueryWrapper<B>().eq("detail_id", detailId).eq("is_deleted", false));
        getSerialMapper().delete(
                new QueryWrapper<S>().eq("detail_id", detailId).eq("is_deleted", false));
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
        dto.setLocations((List) getLocationMapper().selectList(
                new QueryWrapper<L>().eq("detail_id", detailId).eq("is_deleted", false)));
        dto.setBatches((List) getBatchMapper().selectList(
                new QueryWrapper<B>().eq("detail_id", detailId).eq("is_deleted", false)));
        dto.setSerials((List) getSerialMapper().selectList(
                new QueryWrapper<S>().eq("detail_id", detailId).eq("is_deleted", false)));
        return dto;
    }

    // ==================== 库存校验 ====================

    /**
     * 校验库存数量（主表 quantity = Σ location.quantity）
     *
     * @param mainQuantity 主表数量
     * @param detailId     明细ID
     */
    protected void validateInventoryQuantity(BigDecimal mainQuantity, Long detailId) {
        // 子类按需覆写：查询所有库位记录，汇总 quantity 与 mainQuantity 比对
    }
}
