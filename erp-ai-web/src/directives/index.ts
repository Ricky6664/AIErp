import type { App } from 'vue'
import { permissionDirective } from './permission'
import { debounceDirective } from './debounce'

export function setupDirectives(app: App) {
  app.directive('permission', permissionDirective)
  app.directive('debounce', debounceDirective)
}
