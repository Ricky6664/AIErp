import { createApp } from 'vue'
import './style.css'
import '@/styles/element-plus.scss'
import 'vxe-pc-ui/lib/style.css'
import 'vxe-table/lib/style.css'
import { setupVxeTable } from '@/plugins/vxe-table'
import pinia from '@/stores'
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
setupDirectives(app)

app.mount('#app')
