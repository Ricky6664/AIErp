import { createApp } from 'vue'
import '../app.css'
import './style.css'
import '@/styles/variables.css'
import '@/styles/element-override.scss'
import '@/styles/element-plus.scss'
import '@/styles/tools/_layout.scss'
import '@/styles/tools/_text.scss'
import 'vxe-pc-ui/lib/style.css'
import 'vxe-table/lib/style.css'
import { setupVxeTable } from '@/plugins/vxe-table'
import pinia from '@/stores'
import { i18n, setLanguage, provideEpLocale, initialLocale } from '@/i18n'
import { setupDirectives } from '@/directives'
import App from './App.vue'

document.title = import.meta.env.VITE_APP_TITLE as string

if (import.meta.env.VITE_USE_MOCK === 'true') {
  import('@/mock')
    .then(({ setupMock }) => {
      setupMock()
    })
    .catch(() => {
      // mock module not yet created, skip
    })
}

const app = createApp(App)

setupVxeTable(app)
app.use(pinia)
app.use(i18n)
provideEpLocale(app)
setupDirectives(app)

setLanguage(initialLocale)

app.mount('#app')
