package com.erp.module.finance.service;

import com.erp.common.result.PageResult;
import com.erp.common.service.IServiceX;
import com.erp.module.finance.dto.BankAccountCreateDTO;
import com.erp.module.finance.dto.BankAccountQueryDTO;
import com.erp.module.finance.dto.BankAccountUpdateDTO;
import com.erp.module.finance.entity.BankAccountEntity;
import com.erp.module.finance.vo.BankAccountVO;
import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;

/**
 * 银行账户Service接口
 *
 * @author AI
 */
public interface IBankAccountService extends IServiceX<BankAccountEntity> {

    /**
     * 新增银行账户
     *
     * @param dto 银行账户创建DTO
     * @return 银行账户VO
     */
    @Transactional(rollbackFor = Exception.class)
    BankAccountVO create(@Valid BankAccountCreateDTO dto);

    /**
     * 更新银行账户
     *
     * @param id  银行账户ID
     * @param dto 银行账户更新DTO
     * @return 银行账户VO
     */
    @Transactional(rollbackFor = Exception.class)
    BankAccountVO update(Long id, @Valid BankAccountUpdateDTO dto);

    /**
     * 删除银行账户
     *
     * @param id 银行账户ID
     */
    @Transactional(rollbackFor = Exception.class)
    void delete(Long id);

    /**
     * 根据ID查询银行账户详情
     *
     * @param id 银行账户ID
     * @return 银行账户VO
     */
    BankAccountVO getById(Long id);

    /**
     * 分页查询银行账户列表
     *
     * @param query 查询条件DTO
     * @return 分页结果
     */
    PageResult<BankAccountVO> pageList(BankAccountQueryDTO query);
}
