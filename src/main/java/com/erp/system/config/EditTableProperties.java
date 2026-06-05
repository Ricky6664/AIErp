
package com.erp.system.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * 录入数据表格列格式配置属性.
 *
 * <p>绑定 application.yml 中 edit-table.* 配置项, 提供类型安全的列格式配置访问.
 * 所有配置项均提供合理默认值, 配置错误时启动校验会失败.</p>
 *
 * <p>配置项说明:
 * <ul>
 *   <li>edit-table.column-format.date-format — 日期列格式化模式，默认 yyyy-MM-dd</li>
 *   <li>edit-table.column-format.datetime-format — 日期时间列格式化模式，默认 yyyy-MM-dd HH:mm:ss</li>
 *   <li>edit-table.column-format.number-decimal-places — 数字列小数位数，默认 2</li>
 *   <li>edit-table.column-format.number-thousands-separator — 数字列是否显示千分位分隔符，默认 true</li>
 *   <li>edit-table.column-default.width — 列默认宽度(px)，默认 120</li>
 *   <li>edit-table.column-default.min-width — 列最小宽度(px)，默认 60</li>
 *   <li>edit-table.column-default.align — 列默认对齐方式(left/center/right)，默认 left</li>
 *   <li>edit-table.column-default.show-overflow — 内容超出时是否显示省略提示，默认 true</li>
 *   <li>edit-table.column-default.editable — 列是否默认可编辑，默认 false</li>
 *   <li>edit-table.column-persist.mode — 列配置持久化模式(local/remote/both)，默认 local</li>
 *   <li>edit-table.column-persist.cache-ttl-seconds — 远程持久化缓存TTL(秒)，默认 1800</li>
 *   <li>edit-table.edit.trigger — 编辑触发方式(click/dblclick/manual)，默认 click</li>
 *   <li>edit-table.edit.auto-focus — 编辑时是否自动聚焦，默认 true</li>
 *   <li>edit-table.edit.show-status — 是否显示编辑状态图标，默认 true</li>
 *   <li>edit-table.edit.mode — 编辑模式(cell/row)，默认 cell</li>
 * </ul>
 * </p>
 *
 * @author AI
 * @since 2026-06-06
 */
@Data
@Component
@Validated
@ConfigurationProperties(prefix = "edit-table")
public class EditTableProperties {

    private ColumnFormat columnFormat = new ColumnFormat();

    private ColumnDefault columnDefault = new ColumnDefault();

    private ColumnPersist columnPersist = new ColumnPersist();

    private Edit edit = new Edit();

    @Data
    public static class ColumnFormat {

        private String dateFormat = "yyyy-MM-dd";

        private String datetimeFormat = "yyyy-MM-dd HH:mm:ss";

        private int numberDecimalPlaces = 2;

        private boolean numberThousandsSeparator = true;
    }

    @Data
    public static class ColumnDefault {

        private int width = 120;

        private int minWidth = 60;

        private String align = "left";

        private boolean showOverflow = true;

        private boolean editable = false;
    }

    @Data
    public static class ColumnPersist {

        private String mode = "local";

        private long cacheTtlSeconds = 1800;
    }

    @Data
    public static class Edit {

        private String trigger = "click";

        private boolean autoFocus = true;

        private boolean showStatus = true;

        private String mode = "cell";
    }
}
