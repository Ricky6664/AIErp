package com.erp.auth.mapper;

import com.erp.auth.vo.AuthConfigWorkbenchVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 认证配置工作台聚合查询 Mapper.
 *
 * @author AI
 * @since 2026-06-04
 */
@Mapper
public interface AuthConfigWorkbenchMapper {

    AuthConfigWorkbenchVO selectWorkbenchStats(@Param("tenantId") Long tenantId,
                                               @Param("startTime") LocalDateTime startTime,
                                               @Param("endTime") LocalDateTime endTime);

    List<AuthConfigWorkbenchVO.LoginMethodDistVO> selectLoginMethodDistribution(@Param("tenantId") Long tenantId,
                                                                                 @Param("startTime") LocalDateTime startTime,
                                                                                 @Param("endTime") LocalDateTime endTime);

    List<AuthConfigWorkbenchVO.DailyLoginStatVO> selectDailyLoginStats(@Param("tenantId") Long tenantId,
                                                                        @Param("startTime") LocalDateTime startTime,
                                                                        @Param("endTime") LocalDateTime endTime);

    List<AuthConfigWorkbenchVO.RecentLoginVO> selectRecentLogins(@Param("tenantId") Long tenantId,
                                                                  @Param("limit") int limit);
}
