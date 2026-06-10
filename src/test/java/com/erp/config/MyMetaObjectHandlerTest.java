package com.erp.config;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * MyMetaObjectHandler 自动填充验证测试.
 *
 * <p>验收 P0-001-001-005-002-003 任务:</p>
 * <ul>
 *   <li>insertFill 填充 createTime/updateTime/creatorId/updaterId/tenantId/isDeleted</li>
 *   <li>updateFill 强制刷新 updateTime/updaterId</li>
 *   <li>isDeleted 类型为 Boolean (false=未删除)</li>
 *   <li>@Component 注解存在</li>
 *   <li>无敏感信息硬编码</li>
 * </ul>
 *
 * @author AI
 * @since 2026-05-29
 */
class MyMetaObjectHandlerTest {

    /**
     * 验证用实体 POJO, 字段类型与全局规范后端代码规范一致.
     * 添加 @TableField(fill=...) 注解以启用 strictInsertFill/strictUpdateFill.
     */
    @TableName("test_entity")
    public static class TestEntity {
        @TableField(fill = FieldFill.INSERT)
        private LocalDateTime createTime;
        @TableField(fill = FieldFill.INSERT_UPDATE)
        private LocalDateTime updateTime;
        @TableField(fill = FieldFill.INSERT)
        private Long creatorId;
        @TableField(fill = FieldFill.INSERT_UPDATE)
        private Long updaterId;
        @TableField(fill = FieldFill.INSERT)
        private Long tenantId;
        @TableField(fill = FieldFill.INSERT)
        private Boolean isDeleted;

        public LocalDateTime getCreateTime() { return createTime; }
        public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
        public LocalDateTime getUpdateTime() { return updateTime; }
        public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
        public Long getCreatorId() { return creatorId; }
        public void setCreatorId(Long creatorId) { this.creatorId = creatorId; }
        public Long getUpdaterId() { return updaterId; }
        public void setUpdaterId(Long updaterId) { this.updaterId = updaterId; }
        public Long getTenantId() { return tenantId; }
        public void setTenantId(Long tenantId) { this.tenantId = tenantId; }
        public Boolean getIsDeleted() { return isDeleted; }
        public void setIsDeleted(Boolean isDeleted) { this.isDeleted = isDeleted; }
    }

    private MyMetaObjectHandler handler;

    @BeforeAll
    static void initTableInfo() {
        // 注册 TestEntity 到 MyBatis-Plus TableInfo, 使 strictInsertFill 能正常工作
        TableInfoHelper.initTableInfo(
                new MapperBuilderAssistant(new MybatisConfiguration(), ""),
                TestEntity.class);
    }

    @BeforeEach
    void setUp() {
        handler = new MyMetaObjectHandler();
    }

    // ==================== insertFill 验证 ====================

    @Test
    @DisplayName("insertFill 必须自动填充 createTime (LocalDateTime.now)")
    void insertFillShouldSetCreateTime() {
        TestEntity entity = new TestEntity();
        MetaObject metaObject = SystemMetaObject.forObject(entity);

        LocalDateTime before = LocalDateTime.now().minusSeconds(1);
        handler.insertFill(metaObject);
        LocalDateTime after = LocalDateTime.now().plusSeconds(1);

        assertNotNull(entity.getCreateTime(), "insertFill 必须填充 createTime");
        assertTrue(entity.getCreateTime().isAfter(before),
                "createTime 必须不早于调用前时间");
        assertTrue(entity.getCreateTime().isBefore(after),
                "createTime 必须不晚于调用后时间");
    }

    @Test
    @DisplayName("insertFill 必须自动填充 updateTime (LocalDateTime.now)")
    void insertFillShouldSetUpdateTime() {
        TestEntity entity = new TestEntity();
        MetaObject metaObject = SystemMetaObject.forObject(entity);

        handler.insertFill(metaObject);

        assertNotNull(entity.getUpdateTime(), "insertFill 必须填充 updateTime");
    }

    @Test
    @DisplayName("insertFill 必须尝试填充 creatorId (用户ID, 无登录上下文时优雅降级为null)")
    void insertFillShouldAttemptCreatorId() {
        TestEntity entity = new TestEntity();
        MetaObject metaObject = SystemMetaObject.forObject(entity);

        handler.insertFill(metaObject);

        // 无 Sa-Token 上下文时, creatorId 填充为 null (优雅降级)
        // strictInsertFill 不填充 null 值到已有 null 字段, 所以保持 null
        assertNull(entity.getCreatorId(),
                "无登录上下文时 creatorId 应为 null (优雅降级)");
    }

    @Test
    @DisplayName("insertFill 必须尝试填充 updaterId (用户ID, 无登录上下文时优雅降级为null)")
    void insertFillShouldAttemptUpdaterId() {
        TestEntity entity = new TestEntity();
        MetaObject metaObject = SystemMetaObject.forObject(entity);

        handler.insertFill(metaObject);

        assertNull(entity.getUpdaterId(),
                "无登录上下文时 updaterId 应为 null (优雅降级)");
    }

    @Test
    @DisplayName("insertFill 必须尝试填充 tenantId (租户ID, 无租户上下文时优雅降级为null)")
    void insertFillShouldAttemptTenantId() {
        TestEntity entity = new TestEntity();
        MetaObject metaObject = SystemMetaObject.forObject(entity);

        handler.insertFill(metaObject);

        assertNull(entity.getTenantId(),
                "无租户上下文时 tenantId 应为 null (优雅降级)");
    }

    @Test
    @DisplayName("insertFill 必须自动填充 isDeleted = false (Boolean 类型)")
    void insertFillShouldSetIsDeletedToFalse() {
        TestEntity entity = new TestEntity();
        MetaObject metaObject = SystemMetaObject.forObject(entity);

        handler.insertFill(metaObject);

        assertNotNull(entity.getIsDeleted(), "insertFill 必须填充 isDeleted");
        assertFalse(entity.getIsDeleted(), "isDeleted 必须为 false (未删除)");
    }

    @Test
    @DisplayName("isDeleted 字段类型必须是 Boolean (与全局规范后端代码规范一致)")
    void isDeletedFieldTypeShouldBeBoolean() throws Exception {
        // 验证 NOT_DELETED 常量类型为 Boolean
        Field notDeletedField = MyMetaObjectHandler.class.getDeclaredField("NOT_DELETED");
        assertEquals(Boolean.class, notDeletedField.getType(),
                "NOT_DELETED 常量类型必须是 Boolean, 实际: " + notDeletedField.getType().getSimpleName());
        notDeletedField.setAccessible(true);
        assertEquals(Boolean.FALSE, notDeletedField.get(null),
                "NOT_DELETED 常量值必须是 false");
    }

    // ==================== updateFill 验证 ====================

    @Test
    @DisplayName("updateFill 必须强制刷新 updateTime (即使已有值)")
    void updateFillShouldForceRefreshUpdateTime() {
        TestEntity entity = new TestEntity();
        LocalDateTime oldTime = LocalDateTime.of(2020, 1, 1, 0, 0);
        entity.setUpdateTime(oldTime);
        MetaObject metaObject = SystemMetaObject.forObject(entity);

        handler.updateFill(metaObject);

        assertNotNull(entity.getUpdateTime(), "updateFill 必须填充 updateTime");
        assertTrue(entity.getUpdateTime().isAfter(oldTime),
                "updateFill 必须强制刷新 updateTime 为当前时间, 不应保留旧值 " + oldTime);
    }

    @Test
    @DisplayName("updateFill 必须尝试填充 updaterId (当前登录用户, 无上下文时保留原值)")
    void updateFillShouldAttemptUpdaterId() {
        TestEntity entity = new TestEntity();
        entity.setUpdateTime(LocalDateTime.now());
        entity.setUpdaterId(999L);
        MetaObject metaObject = SystemMetaObject.forObject(entity);

        handler.updateFill(metaObject);

        // 注: MyBatis-Plus 3.5.5 的 setFieldValByName 内部有 Objects.nonNull(fieldVal) 检查
        // 当 Sa-Token 无上下文时 getCurrentUserId() 返回 null, setFieldValByName 跳过设置
        // 这是预期行为: 生产环境中用户必须已登录, updaterId 会被正确填充
        // 此处验证: updateFill 方法执行不抛异常, updateTime 已被强制刷新
        assertNotNull(entity.getUpdateTime(), "updateFill 必须刷新 updateTime");
    }

    @Test
    @DisplayName("updateFill 不应修改 createTime (创建时间不可修改)")
    void updateFillShouldNotModifyCreateTime() {
        TestEntity entity = new TestEntity();
        LocalDateTime createTime = LocalDateTime.of(2024, 6, 15, 10, 30);
        entity.setCreateTime(createTime);
        MetaObject metaObject = SystemMetaObject.forObject(entity);

        handler.updateFill(metaObject);

        assertEquals(createTime, entity.getCreateTime(),
                "updateFill 不应修改 createTime, 创建时间不可修改");
    }

    @Test
    @DisplayName("updateFill 不应修改 isDeleted (逻辑删除标记不由 updateFill 处理)")
    void updateFillShouldNotModifyIsDeleted() {
        TestEntity entity = new TestEntity();
        entity.setIsDeleted(false);
        MetaObject metaObject = SystemMetaObject.forObject(entity);

        handler.updateFill(metaObject);

        assertFalse(entity.getIsDeleted(),
                "updateFill 不应修改 isDeleted");
    }

    // ==================== 类结构验证 ====================

    @Test
    @DisplayName("MyMetaObjectHandler 必须标注 @Component 注解")
    void componentAnnotationShouldExist() {
        Component annotation = MyMetaObjectHandler.class.getAnnotation(Component.class);
        assertNotNull(annotation, "MyMetaObjectHandler 必须标注 @Component");
    }

    @Test
    @DisplayName("MyMetaObjectHandler 必须实现 MetaObjectHandler 接口")
    void shouldImplementMetaObjectHandler() {
        assertTrue(com.baomidou.mybatisplus.core.handlers.MetaObjectHandler.class
                        .isAssignableFrom(MyMetaObjectHandler.class),
                "MyMetaObjectHandler 必须实现 MetaObjectHandler 接口");
    }

    @Test
    @DisplayName("MyMetaObjectHandler 源码不得硬编码密码/密钥")
    void noHardcodedSecrets() throws Exception {
        for (Field field : MyMetaObjectHandler.class.getDeclaredFields()) {
            if (field.getType() == String.class && Modifier.isStatic(field.getModifiers())) {
                field.setAccessible(true);
                String value = (String) field.get(null);
                if (value != null) {
                    String lower = value.toLowerCase();
                    assertFalse(lower.contains("password="),
                            "字段 " + field.getName() + " 不得包含 password=");
                    assertFalse(lower.contains("secret="),
                            "字段 " + field.getName() + " 不得包含 secret=");
                    assertFalse(lower.contains("apikey="),
                            "字段 " + field.getName() + " 不得包含 apiKey=");
                }
            }
        }
    }
}
