const validation = {
  required: '${label} is required',
  format: {
    phone: 'Please enter a valid ${label}',
    email: 'Please enter a valid ${label}',
    idCard: 'Please enter a valid ${label}',
    url: 'Please enter a valid ${label}'
  },
  length: {
    min: '${label} must be at least ${min} characters',
    max: '${label} cannot exceed ${max} characters',
    range: '${label} must be between ${min}-${max} characters'
  },
  range: {
    min: '${label} cannot be less than ${min}',
    max: '${label} cannot be greater than ${max}',
    between: '${label} must be between ${min}-${max}'
  },
  custom: {
    duplicate: '${label} already exists',
    invalid: '${label} format is incorrect'
  }
}

export default validation

export type ValidationLocale = typeof validation
