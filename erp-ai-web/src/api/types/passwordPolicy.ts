export interface PasswordPolicy {
  id?: number
  policyName?: string
  minLength: number
  requireUppercase: boolean
  requireLowercase: boolean
  requireNumber: boolean
  requireSpecialChar: boolean
  expireDays?: number
  maxAttempts?: number
  lockMinutes?: number
  passwordHistoryCount?: number
  isEnabled?: boolean
  description?: string
}

export interface PasswordPolicyCheckResult {
  valid: boolean
  errors: string[]
}
