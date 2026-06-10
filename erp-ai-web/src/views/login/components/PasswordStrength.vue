<template>
  <div v-if="password" class="password-strength">
    <el-progress
      :percentage="percentage"
      :color="color"
      :show-text="false"
      :stroke-width="6"
      class="strength-bar"
    />
    <span class="strength-label" :style="{ color }">{{ strengthLabel }}</span>

    <div class="requirement-list">
      <div
        v-for="req in requirements"
        :key="req.key"
        class="requirement-item"
        :class="{ pass: req.pass }"
      >
        <span class="req-icon">{{ req.pass ? '✔' : '✘' }}</span>
        <span class="req-text">{{ req.label }}</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, watch } from 'vue'
import type { PasswordPolicy } from '@/api/types/passwordPolicy'

type StrengthLevel = 'weak' | 'medium' | 'strong'

interface Requirement {
  key: string
  label: string
  pass: boolean
}

const props = withDefaults(
  defineProps<{
    password: string
    policy?: PasswordPolicy
  }>(),
  {
    policy: () => ({
      minLength: 8,
      requireUppercase: true,
      requireLowercase: true,
      requireNumber: true,
      requireSpecialChar: true
    })
  }
)

const emit = defineEmits<{
  'update:strength': [level: StrengthLevel]
}>()

const SPECIAL_CHARS = '!@#$%^&*()_+-=[]{}|;\':",./<>?'

function hasUppercase(val: string): boolean {
  return /[A-Z]/.test(val)
}

function hasLowercase(val: string): boolean {
  return /[a-z]/.test(val)
}

function hasDigit(val: string): boolean {
  return /[0-9]/.test(val)
}

function hasSpecialChar(val: string): boolean {
  return SPECIAL_CHARS.split('').some((ch) => val.includes(ch))
}

const score = computed(() => {
  const val = props.password
  if (!val) return 0
  let s = 0
  if (hasUppercase(val)) s++
  if (hasLowercase(val)) s++
  if (hasDigit(val)) s++
  if (hasSpecialChar(val)) s++
  if (val.length >= 8) s++
  if (val.length >= 12) s++
  return s
})

const strengthLevel = computed<StrengthLevel>(() => {
  const s = score.value
  if (s >= 5) return 'strong'
  if (s >= 3) return 'medium'
  return 'weak'
})

const percentage = computed(() => {
  const map: Record<StrengthLevel, number> = { weak: 33, medium: 66, strong: 100 }
  return map[strengthLevel.value]
})

const color = computed(() => {
  const map: Record<StrengthLevel, string> = {
    weak: '#F56C6C',
    medium: '#E6A23C',
    strong: '#67C23A'
  }
  return map[strengthLevel.value]
})

const strengthLabel = computed(() => {
  const map: Record<StrengthLevel, string> = { weak: '弱', medium: '中', strong: '强' }
  return map[strengthLevel.value]
})

const requirements = computed<Requirement[]>(() => {
  const val = props.password
  const pol = props.policy
  const reqs: Requirement[] = []

  if (pol.minLength) {
    reqs.push({
      key: 'minLength',
      label: `至少${pol.minLength}位字符`,
      pass: val.length >= pol.minLength
    })
  }

  const types: { key: string; label: string; pass: boolean }[] = []
  if (pol.requireUppercase)
    types.push({ key: 'upper', label: '包含大写字母', pass: hasUppercase(val) })
  if (pol.requireLowercase)
    types.push({ key: 'lower', label: '包含小写字母', pass: hasLowercase(val) })
  if (pol.requireNumber) types.push({ key: 'digit', label: '包含数字', pass: hasDigit(val) })
  if (pol.requireSpecialChar)
    types.push({ key: 'special', label: '包含特殊字符', pass: hasSpecialChar(val) })

  reqs.push(...types)

  if (types.length === 4) {
    const satisfiedCount = types.filter((t) => t.pass).length
    reqs.push({
      key: 'rule-3of4',
      label: `四种字符类型至少满足三种（当前满足${satisfiedCount}种）`,
      pass: satisfiedCount >= 3
    })
  }

  return reqs
})

watch(strengthLevel, (level) => {
  emit('update:strength', level)
})
</script>

<style scoped>
.password-strength {
  margin-top: 4px;
}

.strength-bar {
  width: 100%;
}

.strength-bar :deep(.el-progress-bar__outer) {
  border-radius: 3px;
}

.strength-label {
  display: inline-block;
  margin-top: 4px;
  font-size: 12px;
  line-height: 1.5;
}

.requirement-list {
  margin-top: 8px;
  display: flex;
  flex-wrap: wrap;
  gap: 4px 16px;
}

.requirement-item {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

.requirement-item.pass {
  color: var(--el-color-success);
}

.requirement-item:not(.pass) {
  color: var(--el-text-color-placeholder);
}

.req-icon {
  font-size: 11px;
  width: 14px;
  text-align: center;
}

.req-text {
  white-space: nowrap;
}
</style>
