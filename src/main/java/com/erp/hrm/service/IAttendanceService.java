package com.erp.hrm.service;

import com.erp.common.result.PageResult;
import com.erp.common.service.IServiceX;
import com.erp.hrm.dto.AttendanceCreateDTO;
import com.erp.hrm.dto.AttendanceQueryDTO;
import com.erp.hrm.dto.AttendanceUpdateDTO;
import com.erp.hrm.entity.AttendanceEntity;
import com.erp.hrm.vo.AttendanceVO;
import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;

/**
 * 考勤管理Service接口.
 *
 * @author AI
 */
public interface IAttendanceService extends IServiceX<AttendanceEntity> {

    /**
     * 新增考勤记录.
     *
     * @param dto 考勤创建DTO
     * @return 考勤VO
     */
    @Transactional(rollbackFor = Exception.class)
    AttendanceVO create(@Valid AttendanceCreateDTO dto);

    /**
     * 更新考勤记录.
     *
     * @param id  考勤ID
     * @param dto 考勤更新DTO
     * @return 考勤VO
     */
    @Transactional(rollbackFor = Exception.class)
    AttendanceVO update(Long id, @Valid AttendanceUpdateDTO dto);

    /**
     * 删除考勤记录.
     *
     * @param id 考勤ID
     */
    @Transactional(rollbackFor = Exception.class)
    void delete(Long id);

    /**
     * 根据ID查询考勤详情.
     *
     * @param id 考勤ID
     * @return 考勤VO
     */
    AttendanceVO getById(Long id);

    /**
     * 分页查询考勤列表.
     *
     * @param query 查询条件DTO
     * @return 分页结果
     */
    PageResult<AttendanceVO> pageList(AttendanceQueryDTO query);
}
