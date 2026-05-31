const validation = {
  required: '{field} is required',
  maxLength: '{field} cannot exceed {max} characters',
  minLength: '{field} must be at least {min} characters',
  email: 'Please enter a valid email address',
  phone: 'Please enter a valid phone number',
  url: 'Please enter a valid URL',
  number: 'Please enter a valid number',
  integer: 'Please enter a valid integer',
  positive: 'Please enter a positive number',
  max: '{field} cannot be greater than {max}',
  min: '{field} cannot be less than {min}',
  duplicate: '{field} already exists',
  formatError: '{field} format is incorrect'
}

export default validation

export type ValidationLocale = typeof validation
