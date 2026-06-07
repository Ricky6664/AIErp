package com.erp.module.finance.service;

import com.erp.common.result.PageResult;
import com.erp.common.service.IServiceX;
import com.erp.module.finance.dto.CurrencyRateCreateDTO;
import com.erp.module.finance.dto.CurrencyRateQueryDTO;
import com.erp.module.finance.dto.CurrencyRateUpdateDTO;
import com.erp.module.finance.entity.CurrencyRateEntity;
import com.erp.module.finance.vo.CurrencyRateVO;
import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;

/**
 * 币种汇率Service接口
 *
 * @author AI
 */
public interface ICurrencyRateService extends IServiceX<CurrencyRateEntity> {

    /**
     * 新增币种汇率
     *
     * @param dto 币种汇率创建DTO
     * @return 币种汇率VO
     */
    @Transactional(rollbackFor = Exception.class)
    CurrencyRateVO create(@Valid CurrencyRateCreateDTO dto);

    /**
     * 更新币种汇率
     *
     * @param id  币种汇率ID
     * @param dto 币种汇率更新DTO
     * @return 币种汇率VO
     */
    @Transactional(rollbackFor = Exception.class)
    CurrencyRateVO update(Long id, @Valid CurrencyRateUpdateDTO dto);

    /**
     * 删除币种汇率
     *
     * @param id 币种汇率ID
     */
    @Transactional(rollbackFor = Exception.class)
    void delete(Long id);

    /**
     * 根据ID查询币种汇率详情
     *
     * @param id 币种汇率ID
     * @return 币种汇率VO
     */
    CurrencyRateVO getById(Long id);

    /**
     * 分页查询币种汇率列表
     *
     * @param query 查询条件DTO
     * @return 分页结果
     */
    PageResult<CurrencyRateVO> pageList(CurrencyRateQueryDTO query);
}
