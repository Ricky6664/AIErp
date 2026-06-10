const validation = {
  required: '${label}不能为空',
  format: {
    phone: '请输入正确的${label}格式',
    email: '请输入正确的${label}格式',
    idCard: '请输入正确的${label}格式',
    url: '请输入正确的${label}格式'
  },
  length: {
    min: '${label}长度不能少于${min}个字符',
    max: '${label}长度不能超过${max}个字符',
    range: '${label}长度须在${min}-${max}个字符之间'
  },
  range: {
    min: '${label}不能小于${min}',
    max: '${label}不能大于${max}',
    between: '${label}须在${min}-${max}之间'
  },
  custom: {
    duplicate: '${label}已存在，请勿重复添加',
    invalid: '${label}格式不正确'
  }
}

export default validation

export type ValidationLocale = typeof validation
