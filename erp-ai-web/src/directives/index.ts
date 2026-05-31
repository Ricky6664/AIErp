import type { App } from 'vue'
import { permissionDirective } from './permission'
import { debounceDirective } from './debounce'
import { copyDirective } from './copy'
import { vVirtualScroll } from './virtual-scroll'

export function setupDirectives(app: App) {
  app.directive('permission', permissionDirective)
  app.directive('debounce', debounceDirective)
  app.directive('copy', copyDirective)
  app.directive('virtual-scroll', vVirtualScroll)
}
