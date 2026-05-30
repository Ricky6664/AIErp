import { createApp } from 'vue'
import './style.css'
import '@/styles/element-plus.scss'
import 'vxe-pc-ui/lib/style.css'
import 'vxe-table/lib/style.css'
import { setupVxeTable } from '@/plugins/vxe-table'
import App from './App.vue'

const app = createApp(App)

setupVxeTable(app)

app.mount('#app')
