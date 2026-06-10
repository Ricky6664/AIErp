package com.erp.module.crm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.erp.module.crm.entity.TagDefinitionEntity;
import org.apache.ibatis.annotations.Select;

/**
 * 标签定义Mapper接口.
 *
 * @author AI
 */
public interface TagDefinitionMapper extends BaseMapper<TagDefinitionEntity> {

    @Select("SELECT COUNT(*) FROM crm_customer_tag_rel WHERE tag_id = #{tagId}")
    Long countTagReferences(Long tagId);
}
