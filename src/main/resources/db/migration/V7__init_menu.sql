-- ============================================================
-- ERP AI 智能管理系统 - 系统菜单初始化
-- 任务: P0-002-003-001-003-002
-- 描述: 创建 sys_menu 表并初始化核心系统菜单数据
-- 数据库: PostgreSQL 15+
-- ============================================================

-- 1. 创建 sys_menu 表
CREATE TABLE IF NOT EXISTS sys_menu (
    id              BIGINT          NOT NULL,
    menu_name       VARCHAR(50)     NOT NULL,
    parent_id       BIGINT          NOT NULL DEFAULT 0,
    order_num       INT             NOT NULL DEFAULT 0,
    path            VARCHAR(200)    DEFAULT '',
    component       VARCHAR(255)    DEFAULT '',
    menu_type       CHAR(1)         NOT NULL DEFAULT 'M',
    perms           VARCHAR(100)    DEFAULT '',
    icon            VARCHAR(100)    DEFAULT '',
    visible         CHAR(1)         NOT NULL DEFAULT '0',
    status          CHAR(1)         NOT NULL DEFAULT '0',
    -- 通用必含字段
    tenant_id       BIGINT          NOT NULL,
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by      BIGINT,
    updated_by      BIGINT,
    is_deleted      BOOLEAN         NOT NULL DEFAULT FALSE,
    owner_dept_id   BIGINT,
    owner_id        BIGINT,
    version         INT             NOT NULL DEFAULT 0,
    CONSTRAINT pk_sys_menu PRIMARY KEY (id)
);

-- 索引
CREATE INDEX IF NOT EXISTS idx_menu_parent ON sys_menu(parent_id) WHERE is_deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_menu_type ON sys_menu(menu_type) WHERE is_deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_menu_tenant ON sys_menu(tenant_id) WHERE is_deleted = FALSE;

-- 注释
COMMENT ON TABLE sys_menu IS '系统菜单表';
COMMENT ON COLUMN sys_menu.id IS '菜单ID（雪花算法）';
COMMENT ON COLUMN sys_menu.menu_name IS '菜单名称';
COMMENT ON COLUMN sys_menu.parent_id IS '父菜单ID，0为顶级';
COMMENT ON COLUMN sys_menu.order_num IS '排序序号';
COMMENT ON COLUMN sys_menu.path IS '前端路由路径';
COMMENT ON COLUMN sys_menu.component IS '组件路径（如system/user/index）';
COMMENT ON COLUMN sys_menu.menu_type IS 'M=目录 C=菜单 F=按钮';
COMMENT ON COLUMN sys_menu.perms IS '权限标识（如system:user:list）';
COMMENT ON COLUMN sys_menu.icon IS 'Element Plus图标名';
COMMENT ON COLUMN sys_menu.visible IS '0=显示 1=隐藏';
COMMENT ON COLUMN sys_menu.status IS '0=正常 1=停用';
COMMENT ON COLUMN sys_menu.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_menu.created_at IS '创建时间';
COMMENT ON COLUMN sys_menu.updated_at IS '更新时间';
COMMENT ON COLUMN sys_menu.created_by IS '创建人ID';
COMMENT ON COLUMN sys_menu.updated_by IS '修改人ID';
COMMENT ON COLUMN sys_menu.is_deleted IS '是否删除';
COMMENT ON COLUMN sys_menu.owner_dept_id IS '所属部门ID';
COMMENT ON COLUMN sys_menu.owner_id IS '数据负责人ID';
COMMENT ON COLUMN sys_menu.version IS '版本号';

COMMENT ON CONSTRAINT pk_sys_menu ON sys_menu IS '主键约束：雪花算法BIGINT';
COMMENT ON INDEX idx_menu_parent IS '查询索引：按父菜单ID';
COMMENT ON INDEX idx_menu_type IS '查询索引：按菜单类型';
COMMENT ON INDEX idx_menu_tenant IS '租户级查询索引';

-- ============================================================
-- 2. 初始化核心系统菜单数据
-- ============================================================

-- 系统管理（目录）
INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, perms, icon, visible, status, tenant_id)
VALUES (1, '系统管理', 0, 1, '/system', '', 'M', '', 'Setting', '0', '0', 0);

-- 用户管理（菜单）
INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, perms, icon, visible, status, tenant_id)
VALUES (100, '用户管理', 1, 1, 'user', 'system/user/index', 'C', 'system:user:list', 'User', '0', '0', 0);

-- 用户管理按钮权限
INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, perms, icon, visible, status, tenant_id)
VALUES (1001, '用户查询', 100, 1, '', '', 'F', 'system:user:query', '', '0', '0', 0);
INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, perms, icon, visible, status, tenant_id)
VALUES (1002, '用户新增', 100, 2, '', '', 'F', 'system:user:add', '', '0', '0', 0);
INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, perms, icon, visible, status, tenant_id)
VALUES (1003, '用户修改', 100, 3, '', '', 'F', 'system:user:edit', '', '0', '0', 0);
INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, perms, icon, visible, status, tenant_id)
VALUES (1004, '用户删除', 100, 4, '', '', 'F', 'system:user:remove', '', '0', '0', 0);
INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, perms, icon, visible, status, tenant_id)
VALUES (1005, '用户导出', 100, 5, '', '', 'F', 'system:user:export', '', '0', '0', 0);

-- 角色管理（菜单）
INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, perms, icon, visible, status, tenant_id)
VALUES (101, '角色管理', 1, 2, 'role', 'system/role/index', 'C', 'system:role:list', 'Avatar', '0', '0', 0);

-- 角色管理按钮权限
INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, perms, icon, visible, status, tenant_id)
VALUES (1011, '角色查询', 101, 1, '', '', 'F', 'system:role:query', '', '0', '0', 0);
INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, perms, icon, visible, status, tenant_id)
VALUES (1012, '角色新增', 101, 2, '', '', 'F', 'system:role:add', '', '0', '0', 0);
INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, perms, icon, visible, status, tenant_id)
VALUES (1013, '角色修改', 101, 3, '', '', 'F', 'system:role:edit', '', '0', '0', 0);
INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, perms, icon, visible, status, tenant_id)
VALUES (1014, '角色删除', 101, 4, '', '', 'F', 'system:role:remove', '', '0', '0', 0);

-- 菜单管理（菜单）
INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, perms, icon, visible, status, tenant_id)
VALUES (102, '菜单管理', 1, 3, 'menu', 'system/menu/index', 'C', 'system:menu:list', 'Menu', '0', '0', 0);

-- 菜单管理按钮权限
INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, perms, icon, visible, status, tenant_id)
VALUES (1021, '菜单查询', 102, 1, '', '', 'F', 'system:menu:query', '', '0', '0', 0);
INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, perms, icon, visible, status, tenant_id)
VALUES (1022, '菜单新增', 102, 2, '', '', 'F', 'system:menu:add', '', '0', '0', 0);
INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, perms, icon, visible, status, tenant_id)
VALUES (1023, '菜单修改', 102, 3, '', '', 'F', 'system:menu:edit', '', '0', '0', 0);
INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, perms, icon, visible, status, tenant_id)
VALUES (1024, '菜单删除', 102, 4, '', '', 'F', 'system:menu:remove', '', '0', '0', 0);

-- 部门管理（菜单）
INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, perms, icon, visible, status, tenant_id)
VALUES (103, '部门管理', 1, 4, 'dept', 'system/dept/index', 'C', 'system:dept:list', 'OfficeBuilding', '0', '0', 0);

-- 部门管理按钮权限
INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, perms, icon, visible, status, tenant_id)
VALUES (1031, '部门查询', 103, 1, '', '', 'F', 'system:dept:query', '', '0', '0', 0);
INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, perms, icon, visible, status, tenant_id)
VALUES (1032, '部门新增', 103, 2, '', '', 'F', 'system:dept:add', '', '0', '0', 0);
INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, perms, icon, visible, status, tenant_id)
VALUES (1033, '部门修改', 103, 3, '', '', 'F', 'system:dept:edit', '', '0', '0', 0);
INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, perms, icon, visible, status, tenant_id)
VALUES (1034, '部门删除', 103, 4, '', '', 'F', 'system:dept:remove', '', '0', '0', 0);

-- 岗位管理（菜单）
INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, perms, icon, visible, status, tenant_id)
VALUES (104, '岗位管理', 1, 5, 'post', 'system/post/index', 'C', 'system:post:list', 'Stamp', '0', '0', 0);

-- 岗位管理按钮权限
INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, perms, icon, visible, status, tenant_id)
VALUES (1041, '岗位查询', 104, 1, '', '', 'F', 'system:post:query', '', '0', '0', 0);
INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, perms, icon, visible, status, tenant_id)
VALUES (1042, '岗位新增', 104, 2, '', '', 'F', 'system:post:add', '', '0', '0', 0);
INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, perms, icon, visible, status, tenant_id)
VALUES (1043, '岗位修改', 104, 3, '', '', 'F', 'system:post:edit', '', '0', '0', 0);
INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, perms, icon, visible, status, tenant_id)
VALUES (1044, '岗位删除', 104, 4, '', '', 'F', 'system:post:remove', '', '0', '0', 0);

-- 字典管理（菜单）
INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, perms, icon, visible, status, tenant_id)
VALUES (105, '字典管理', 1, 6, 'dict', 'system/dict/index', 'C', 'system:dict:list', 'Collection', '0', '0', 0);

-- 字典管理按钮权限
INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, perms, icon, visible, status, tenant_id)
VALUES (1051, '字典查询', 105, 1, '', '', 'F', 'system:dict:query', '', '0', '0', 0);
INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, perms, icon, visible, status, tenant_id)
VALUES (1052, '字典新增', 105, 2, '', '', 'F', 'system:dict:add', '', '0', '0', 0);
INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, perms, icon, visible, status, tenant_id)
VALUES (1053, '字典修改', 105, 3, '', '', 'F', 'system:dict:edit', '', '0', '0', 0);
INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, perms, icon, visible, status, tenant_id)
VALUES (1054, '字典删除', 105, 4, '', '', 'F', 'system:dict:remove', '', '0', '0', 0);

-- 组织架构（目录）
INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, perms, icon, visible, status, tenant_id)
VALUES (2, '组织架构', 0, 2, '/org', '', 'M', '', 'Share', '0', '0', 0);

-- 公司管理（菜单）
INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, perms, icon, visible, status, tenant_id)
VALUES (200, '公司管理', 2, 1, 'company', 'org/company/index', 'C', 'org:company:list', 'Office', '0', '0', 0);

INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, perms, icon, visible, status, tenant_id)
VALUES (2001, '公司查询', 200, 1, '', '', 'F', 'org:company:query', '', '0', '0', 0);
INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, perms, icon, visible, status, tenant_id)
VALUES (2002, '公司新增', 200, 2, '', '', 'F', 'org:company:add', '', '0', '0', 0);
INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, perms, icon, visible, status, tenant_id)
VALUES (2003, '公司修改', 200, 3, '', '', 'F', 'org:company:edit', '', '0', '0', 0);
INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, perms, icon, visible, status, tenant_id)
VALUES (2004, '公司删除', 200, 4, '', '', 'F', 'org:company:remove', '', '0', '0', 0);

-- 基础数据（目录）
INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, perms, icon, visible, status, tenant_id)
VALUES (3, '基础数据', 0, 3, '/basic', '', 'M', '', 'DataAnalysis', '0', '0', 0);

-- 编码规则（菜单）
INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, perms, icon, visible, status, tenant_id)
VALUES (300, '编码规则', 3, 1, 'codeRule', 'basic/codeRule/index', 'C', 'basic:codeRule:list', 'Tickets', '0', '0', 0);

INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, perms, icon, visible, status, tenant_id)
VALUES (3001, '编码规则查询', 300, 1, '', '', 'F', 'basic:codeRule:query', '', '0', '0', 0);
INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, perms, icon, visible, status, tenant_id)
VALUES (3002, '编码规则新增', 300, 2, '', '', 'F', 'basic:codeRule:add', '', '0', '0', 0);
INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, perms, icon, visible, status, tenant_id)
VALUES (3003, '编码规则修改', 300, 3, '', '', 'F', 'basic:codeRule:edit', '', '0', '0', 0);
INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, perms, icon, visible, status, tenant_id)
VALUES (3004, '编码规则删除', 300, 4, '', '', 'F', 'basic:codeRule:remove', '', '0', '0', 0);

-- 系统参数（菜单）
INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, perms, icon, visible, status, tenant_id)
VALUES (301, '系统参数', 3, 2, 'param', 'basic/param/index', 'C', 'basic:param:list', 'Setting', '0', '0', 0);

INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, perms, icon, visible, status, tenant_id)
VALUES (3011, '参数查询', 301, 1, '', '', 'F', 'basic:param:query', '', '0', '0', 0);
INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, perms, icon, visible, status, tenant_id)
VALUES (3012, '参数新增', 301, 2, '', '', 'F', 'basic:param:add', '', '0', '0', 0);
INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, perms, icon, visible, status, tenant_id)
VALUES (3013, '参数修改', 301, 3, '', '', 'F', 'basic:param:edit', '', '0', '0', 0);
INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, perms, icon, visible, status, tenant_id)
VALUES (3014, '参数删除', 301, 4, '', '', 'F', 'basic:param:remove', '', '0', '0', 0);

-- ============================================================
-- 添加索引与约束
-- 索引清单:
--   1. pk_sys_menu — 主键约束 (id)
--   2. idx_menu_parent — 父菜单查询索引 (parent_id)
--   3. idx_menu_type — 菜单类型查询索引 (menu_type)
--   4. idx_menu_tenant — 租户查询索引 (tenant_id)
-- ============================================================

-- 回滚脚本（如需回滚，执行以下语句）:
-- DROP TABLE IF EXISTS sys_menu CASCADE;
-- ============================================================
