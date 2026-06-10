package com.erp.system.announcement.service;

import com.erp.common.result.PageResult;
import com.erp.common.service.IServiceX;
import com.erp.system.announcement.dto.AnnouncementCreateDTO;
import com.erp.system.announcement.dto.AnnouncementQueryDTO;
import com.erp.system.announcement.dto.AnnouncementUpdateDTO;
import com.erp.system.announcement.entity.AnnouncementEntity;
import com.erp.system.announcement.vo.AnnouncementVO;
import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 系统公告服务接口.
 *
 * @author AI
 */
public interface IAnnouncementService extends IServiceX<AnnouncementEntity> {

    @Transactional(rollbackFor = Exception.class)
    AnnouncementVO create(@Valid AnnouncementCreateDTO dto);

    @Transactional(rollbackFor = Exception.class)
    AnnouncementVO update(Long id, @Valid AnnouncementUpdateDTO dto);

    @Transactional(rollbackFor = Exception.class)
    void delete(Long id);

    PageResult<AnnouncementVO> pageList(AnnouncementQueryDTO query);

    List<AnnouncementVO> getUnreadList();

    @Transactional(rollbackFor = Exception.class)
    void markAsRead(Long announcementId);
}
