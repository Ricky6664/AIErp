package com.erp.sale.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.service.IServiceX;
import com.erp.sale.dto.SaleQuotationCreateDTO;
import com.erp.sale.dto.SaleQuotationQueryDTO;
import com.erp.sale.dto.SaleQuotationUpdateDTO;
import com.erp.sale.entity.SaleQuotationEntity;
import com.erp.sale.vo.SaleQuotationDetailVO;
import com.erp.sale.vo.SaleQuotationListVO;

/**
 * 销售订单Service接口.
 *
 * @author AI
 */
public interface ISaleOrderService extends IServiceX<SaleQuotationEntity> {

    /**
     * 分页查询列表.
     */
    Page<SaleQuotationListVO> pageList(SaleQuotationQueryDTO query);

    /**
     * 查询详情.
     */
    SaleQuotationDetailVO getDetail(Long id);

    /**
     * 新增.
     */
    Long create(SaleQuotationCreateDTO dto);

    /**
     * 修改.
     */
    Boolean update(SaleQuotationUpdateDTO dto);

    /**
     * 删除.
     */
    Boolean delete(Long id);

    /**
     * 提交审核.
     */
    Boolean submitAudit(Long id, String opinion);
}
