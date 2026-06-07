package com.erp.module.finance.service;

import com.erp.common.result.PageResult;
import com.erp.common.service.IServiceX;
import com.erp.module.finance.dto.AccountCreateDTO;
import com.erp.module.finance.dto.AccountQueryDTO;
import com.erp.module.finance.dto.AccountUpdateDTO;
import com.erp.module.finance.entity.AccountEntity;
import com.erp.module.finance.vo.AccountVO;
import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;

/**
 * 会计科目Service接口
 *
 * @author AI
 */
public interface IAccountService extends IServiceX<AccountEntity> {

    /**
     * 新增会计科目
     *
     * @param dto 会计科目创建DTO
     * @return 会计科目VO
     */
    @Transactional(rollbackFor = Exception.class)
    AccountVO create(@Valid AccountCreateDTO dto);

    /**
     * 更新会计科目
     *
     * @param id  会计科目ID
     * @param dto 会计科目更新DTO
     * @return 会计科目VO
     */
    @Transactional(rollbackFor = Exception.class)
    AccountVO update(Long id, @Valid AccountUpdateDTO dto);

    /**
     * 删除会计科目
     *
     * @param id 会计科目ID
     */
    @Transactional(rollbackFor = Exception.class)
    void delete(Long id);

    /**
     * 根据ID查询会计科目详情
     *
     * @param id 会计科目ID
     * @return 会计科目VO
     */
    AccountVO getById(Long id);

    /**
     * 分页查询会计科目列表
     *
     * @param query 查询条件DTO
     * @return 分页结果
     */
    PageResult<AccountVO> pageList(AccountQueryDTO query);
}
