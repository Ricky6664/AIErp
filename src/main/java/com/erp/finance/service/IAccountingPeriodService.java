package com.erp.finance.service;

import com.erp.common.result.PageResult;
import com.erp.common.service.IServiceX;
import com.erp.finance.dto.AccountingPeriodCreateDTO;
import com.erp.finance.dto.AccountingPeriodQueryDTO;
import com.erp.finance.dto.AccountingPeriodUpdateDTO;
import com.erp.finance.entity.AccountingPeriodEntity;
import com.erp.finance.vo.AccountingPeriodVO;
import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;

/**
 * 会计期间服务接口.
 *
 * @author AI
 * @since 2026-06-07
 */
public interface IAccountingPeriodService extends IServiceX<AccountingPeriodEntity> {

    /**
     * 新增会计期间.
     *
     * @param dto 会计期间创建DTO
     * @return 会计期间VO
     */
    @Transactional(rollbackFor = Exception.class)
    AccountingPeriodVO create(@Valid AccountingPeriodCreateDTO dto);

    /**
     * 修改会计期间.
     *
     * @param id  会计期间ID
     * @param dto 会计期间更新DTO
     * @return 会计期间VO
     */
    @Transactional(rollbackFor = Exception.class)
    AccountingPeriodVO update(Long id, @Valid AccountingPeriodUpdateDTO dto);

    /**
     * 删除会计期间.
     *
     * @param id 会计期间ID
     */
    @Transactional(rollbackFor = Exception.class)
    void delete(Long id);

    /**
     * 根据ID查询会计期间.
     *
     * @param id 会计期间ID
     * @return 会计期间VO
     */
    AccountingPeriodVO getById(Long id);

    /**
     * 分页查询会计期间列表.
     *
     * @param query 查询条件
     * @return 分页结果
     */
    PageResult<AccountingPeriodVO> pageList(AccountingPeriodQueryDTO query);
}
