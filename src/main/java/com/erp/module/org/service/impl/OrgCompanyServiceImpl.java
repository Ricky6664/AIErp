package com.erp.module.org.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.erp.common.annotation.OperLog;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.common.result.PageResult;
import com.erp.common.service.ServiceImplX;
import com.erp.module.org.dto.CompanyCreateDTO;
import com.erp.module.org.dto.CompanyQueryDTO;
import com.erp.module.org.dto.CompanyUpdateDTO;
import com.erp.module.org.entity.OrgCompany;
import com.erp.module.org.entity.OrgDepartment;
import com.erp.module.org.mapper.OrgCompanyMapper;
import com.erp.module.org.mapper.OrgDepartmentMapper;
import com.erp.module.org.service.OrgCompanyService;
import com.erp.module.org.vo.CompanyDetailVO;
import com.erp.module.org.vo.CompanyListVO;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class OrgCompanyServiceImpl extends ServiceImplX<OrgCompanyMapper, OrgCompany>
        implements OrgCompanyService {

    @Resource
    private OrgDepartmentMapper departmentMapper;

    @Override
    @Transactional(readOnly = true)
    public PageResult<CompanyListVO> page(CompanyQueryDTO query) {
        LambdaQueryWrapper<OrgCompany> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.and(w -> w
                    .like(OrgCompany::getCompanyName, query.getKeyword())
                    .or()
                    .like(OrgCompany::getCompanyShortName, query.getKeyword()));
        }
        wrapper.eq(query.getEnabled() != null, OrgCompany::getEnableFlag, query.getEnabled());
        wrapper.orderByDesc(OrgCompany::getCreateTime);

        IPage<OrgCompany> page = page(query.toPage(), wrapper);
        return PageResult.of(page.convert(this::toListVO));
    }

    @Override
    @Transactional(readOnly = true)
    public CompanyDetailVO getById(Long id) {
        OrgCompany entity = super.getById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "公司不存在");
        }
        return toDetailVO(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperLog(module = "组织架构", action = "新增", description = "新增公司")
    public Long create(CompanyCreateDTO dto) {
        validateCompanyNameUniqueness(dto.getCompanyName(), null);
        validateCreditCode(dto.getCreditCode(), null);

        OrgCompany entity = new OrgCompany();
        BeanUtils.copyProperties(dto, entity);
        entity.setEnableFlag(true);
        save(entity);
        return entity.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperLog(module = "组织架构", action = "修改", description = "修改公司")
    public void update(Long id, CompanyUpdateDTO dto) {
        OrgCompany entity = super.getById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "公司不存在");
        }
        validateCompanyNameUniqueness(dto.getCompanyName(), id);
        validateCreditCode(dto.getCreditCode(), id);

        BeanUtils.copyProperties(dto, entity);
        entity.setId(id);
        updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperLog(module = "组织架构", action = "删除", description = "删除公司")
    public void delete(Long id) {
        OrgCompany entity = super.getById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "公司不存在");
        }

        LambdaQueryWrapper<OrgDepartment> deptWrapper = new LambdaQueryWrapper<>();
        deptWrapper.eq(OrgDepartment::getCompanyId, id);
        if (departmentMapper.selectCount(deptWrapper) > 0) {
            throw new BusinessException(ErrorCode.DATA_ALREADY_EXISTS, "该公司下存在部门，无法删除");
        }
        removeById(id);
    }

    private void validateCompanyNameUniqueness(String companyName, Long excludeId) {
        if (!StringUtils.hasText(companyName)) {
            return;
        }
        LambdaQueryWrapper<OrgCompany> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrgCompany::getCompanyName, companyName);
        if (excludeId != null) {
            wrapper.ne(OrgCompany::getId, excludeId);
        }
        if (count(wrapper) > 0) {
            throw new BusinessException(ErrorCode.DATA_ALREADY_EXISTS, "公司名称已存在");
        }
    }

    private void validateCreditCode(String creditCode, Long excludeId) {
        if (!StringUtils.hasText(creditCode)) {
            return;
        }
        if (!creditCode.matches("^[0-9A-HJ-NP-RTUW-Y]{2}\\d{6}[0-9A-HJ-NP-RTUW-Y]{10}$")) {
            throw new BusinessException(ErrorCode.PARAM_FORMAT_ERROR, "统一社会信用代码格式不正确");
        }
        LambdaQueryWrapper<OrgCompany> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrgCompany::getCreditCode, creditCode);
        if (excludeId != null) {
            wrapper.ne(OrgCompany::getId, excludeId);
        }
        if (count(wrapper) > 0) {
            throw new BusinessException(ErrorCode.DATA_ALREADY_EXISTS, "统一社会信用代码已存在");
        }
    }

    private CompanyListVO toListVO(OrgCompany entity) {
        CompanyListVO vo = new CompanyListVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    private CompanyDetailVO toDetailVO(OrgCompany entity) {
        CompanyDetailVO vo = new CompanyDetailVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}
