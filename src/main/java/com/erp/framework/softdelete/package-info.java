/**
 * 软删除插件包.
 * <p>
 * 存放软删除相关配置和插件, 基于 MyBatis-Plus @TableLogic 注解实现.
 * 逻辑删除字段 is_deleted(0=未删除, 1=已删除), 自动拦截 DELETE 语句转换为 UPDATE.
 * </p>
 *
 * @author AI
 */
package com.erp.framework.softdelete;
