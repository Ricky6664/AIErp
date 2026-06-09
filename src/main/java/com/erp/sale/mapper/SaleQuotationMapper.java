package com.erp.sale.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.mapper.BaseMapperX;
import com.erp.sale.entity.SaleQuotationEntity;
import com.erp.sale.dto.SaleQuotationQueryDTO;
import com.erp.sale.vo.SaleQuotationListVO;
import com.erp.sale.vo.SaleQuotationDetailVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 报价单Mapper.
 *
 * @author AI
 */
@Mapper
public interface SaleQuotationMapper extends BaseMapperX<SaleQuotationEntity> {

    IPage<SaleQuotationListVO> selectPageList(
        @Param("page") Page<?> page,
        @Param("query") SaleQuotationQueryDTO query
    );

    SaleQuotationDetailVO selectDetailById(@Param("id") Long id);
}
