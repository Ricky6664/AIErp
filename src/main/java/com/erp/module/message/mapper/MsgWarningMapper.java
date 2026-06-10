package com.erp.module.message.mapper;

import com.erp.common.mapper.BaseMapperX;
import com.erp.module.message.entity.MsgWarningEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 预警记录Mapper.
 *
 * @author AI
 */
@Mapper
public interface MsgWarningMapper extends BaseMapperX<MsgWarningEntity> {

    @Select("SELECT COUNT(*) > 0 FROM msg_warning WHERE rule_id = #{ruleId} AND business_id = #{businessId} AND DATE(create_time) = CURDATE() AND is_deleted = false")
    boolean existsToday(@Param("ruleId") Long ruleId, @Param("businessId") String businessId);

    @Select("SELECT warning_type AS module, COUNT(*) AS cnt FROM msg_warning WHERE is_deleted = false GROUP BY warning_type")
    List<Map<String, Object>> countByModule();

    @Select("SELECT DATE(create_time) AS date, COUNT(*) AS cnt FROM msg_warning WHERE create_time >= #{startDate} AND is_deleted = false GROUP BY DATE(create_time) ORDER BY DATE(create_time)")
    List<Map<String, Object>> selectTrend(@Param("startDate") LocalDate startDate);

    @Select("SELECT warning_type AS name, COUNT(*) AS value FROM msg_warning WHERE is_deleted = false GROUP BY warning_type")
    List<Map<String, Object>> selectDistribution();

    @Select("<script>SELECT * FROM msg_warning WHERE is_deleted = false <if test='module != null and module != \"\"'>AND warning_type = #{module}</if> ORDER BY create_time DESC</script>")
    List<MsgWarningEntity> selectWarningList(@Param("module") String module);
}
