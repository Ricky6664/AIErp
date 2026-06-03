/** 白名单路径 - 无需登录即可访问 */
export const WHITE_LIST: string[] = ['/login', '/404', '/403', '/no-permission']

/** 登录页路径 */
export const LOGIN_PATH = '/login'

/** 首页路径 */
export const HOME_PATH = '/home'

/** 404页面路径(用于catch-all路由) */
export const NOT_FOUND_PATH = '/404'

/** Token存储Key */
export const TOKEN_KEY = 'satoken'

/** RefreshToken存储Key */
export const REFRESH_TOKEN_KEY = 'refresh_token'

/** 动态路由加载标记Key */
export const ROUTES_LOADED_KEY = 'routes_loaded'
