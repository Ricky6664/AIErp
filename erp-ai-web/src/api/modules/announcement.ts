import request from '@/utils/request'
import type {
  AnnouncementQuery,
  AnnouncementCreateDTO,
  AnnouncementUpdateDTO,
  AnnouncementPageResult
} from '@/api/types/announcement'

/** 分页查询公告列表 */
export function getAnnouncementPageList(
  params: AnnouncementQuery
): Promise<AnnouncementPageResult> {
  return request.get('/api/system/announcement', { params }) as Promise<AnnouncementPageResult>
}

/** 新增公告 */
export function createAnnouncement(data: AnnouncementCreateDTO): Promise<number> {
  return request.post('/api/system/announcement', data) as Promise<number>
}

/** 修改公告 */
export function updateAnnouncement(id: number, data: AnnouncementUpdateDTO): Promise<void> {
  return request.put(`/api/system/announcement/${id}`, data) as Promise<void>
}

/** 删除公告 */
export function deleteAnnouncement(id: number): Promise<void> {
  return request.delete(`/api/system/announcement/${id}`) as Promise<void>
}
