import { ref, onUnmounted } from 'vue'

export function useWebSocket(url: string) {
  const ws = ref<WebSocket | null>(null)
  const messageHandlers: ((data: any) => void)[] = []
  let reconnectTimer: ReturnType<typeof setTimeout> | null = null
  let heartbeatTimer: ReturnType<typeof setInterval> | null = null
  let manualClose = false

  const connect = () => {
    if (manualClose) return
    try {
      const protocol = location.protocol === 'https:' ? 'wss:' : 'ws:'
      const token = localStorage.getItem('token')
      ws.value = new WebSocket(`${protocol}//${location.host}${url}?token=${token}`)

      ws.value.onopen = () => {
        heartbeatTimer = setInterval(() => {
          if (ws.value?.readyState === WebSocket.OPEN) {
            ws.value.send(JSON.stringify({ type: 'ping' }))
          }
        }, 30000)
      }

      ws.value.onmessage = (event) => {
        try {
          const data = JSON.parse(event.data)
          if (data.type === 'pong') return
          messageHandlers.forEach((fn) => fn(data))
        } catch {
          // ignore parse errors
        }
      }

      ws.value.onclose = () => {
        if (heartbeatTimer) {
          clearInterval(heartbeatTimer)
          heartbeatTimer = null
        }
        if (!manualClose) {
          reconnectTimer = setTimeout(connect, 5000)
        }
      }

      ws.value.onerror = () => {
        ws.value?.close()
      }
    } catch {
      if (!manualClose) {
        reconnectTimer = setTimeout(connect, 5000)
      }
    }
  }

  const onMessage = (handler: (data: any) => void) => {
    messageHandlers.push(handler)
  }

  const disconnect = () => {
    manualClose = true
    if (reconnectTimer) {
      clearTimeout(reconnectTimer)
      reconnectTimer = null
    }
    if (heartbeatTimer) {
      clearInterval(heartbeatTimer)
      heartbeatTimer = null
    }
    ws.value?.close()
    ws.value = null
  }

  onUnmounted(() => {
    disconnect()
  })

  return { connect, disconnect, onMessage }
}
