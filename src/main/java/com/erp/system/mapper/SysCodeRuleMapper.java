package com.erp.system.mapper;

import com.erp.common.mapper.BaseMapperX;
import com.erp.system.entity.SysCodeRule;
import com.erp.system.vo.SysCodeRuleVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 编码规则配置表 Mapper 接口.
 *
 * @author AI
 * @since 2026-05-29
 */
@Mapper
public interface SysCodeRuleMapper extends BaseMapperX<SysCodeRule> {

    /**
     * 联查编码规则主表及其所有段配置.
     */
    List<SysCodeRuleVO.DetailVO> selectRuleWithSegments(@Param("ruleId") Long ruleId);

    /**
     * 按规则编码查询.
     */
    SysCodeRule selectByRuleCode(@Param("ruleCode") String ruleCode);

    /**
     * 乐观锁更新 current_value.
     *
     * @return 受影响行数, 0 表示版本冲突
     */
    int updateCurrentVersion(@Param("id") Long id, @Param("newValue") Long newValue, @Param("oldValue") Long oldValue);
}
