import request from '@/utils/request'
import type { PasswordPolicy } from '@/api/types/passwordPolicy'

export function getCurrentPasswordPolicyApi(): Promise<PasswordPolicy> {
  return request.get('/api/system/password-policies/current')
}
