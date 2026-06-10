package com.erp.finance.service;

import com.erp.common.result.PageResult;
import com.erp.common.service.IServiceX;
import com.erp.finance.dto.VoucherWordCreateDTO;
import com.erp.finance.dto.VoucherWordQueryDTO;
import com.erp.finance.dto.VoucherWordUpdateDTO;
import com.erp.finance.entity.VoucherWordEntity;
import com.erp.finance.vo.VoucherWordVO;
import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;

/**
 * 凭证字服务接口.
 *
 * @author AI
 * @since 2026-06-07
 */
public interface IVoucherWordService extends IServiceX<VoucherWordEntity> {

    /**
     * 新增凭证字.
     *
     * @param dto 凭证字创建DTO
     * @return 凭证字VO
     */
    @Transactional(rollbackFor = Exception.class)
    VoucherWordVO create(@Valid VoucherWordCreateDTO dto);

    /**
     * 修改凭证字.
     *
     * @param id  凭证字ID
     * @param dto 凭证字更新DTO
     * @return 凭证字VO
     */
    @Transactional(rollbackFor = Exception.class)
    VoucherWordVO update(Long id, @Valid VoucherWordUpdateDTO dto);

    /**
     * 删除凭证字.
     *
     * @param id 凭证字ID
     */
    @Transactional(rollbackFor = Exception.class)
    void delete(Long id);

    /**
     * 切换凭证字启用状态.
     *
     * @param id     凭证字ID
     * @param status 新状态
     */
    @Transactional(rollbackFor = Exception.class)
    void updateStatus(Long id, Integer status);

    /**
     * 根据ID查询凭证字.
     *
     * @param id 凭证字ID
     * @return 凭证字VO
     */
    VoucherWordVO getById(Long id);

    /**
     * 分页查询凭证字列表.
     *
     * @param query 查询条件
     * @return 分页结果
     */
    PageResult<VoucherWordVO> pageList(VoucherWordQueryDTO query);
}
