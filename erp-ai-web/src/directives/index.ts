import type { App } from 'vue'
import { permissionDirective, roleDirective } from './permission'
import { debounceDirective } from './debounce'
import { copyDirective } from './copy'
import { vVirtualScroll } from './virtual-scroll'
import { vLazyImg } from './lazy-img'

export function setupDirectives(app: App) {
  app.directive('permission', permissionDirective)
  app.directive('role', roleDirective)
  app.directive('debounce', debounceDirective)
  app.directive('copy', copyDirective)
  app.directive('virtual-scroll', vVirtualScroll)
  app.directive('lazy-img', vLazyImg)
}
