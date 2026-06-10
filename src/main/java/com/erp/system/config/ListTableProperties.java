
package com.erp.system.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * 列表数据表格列格式配置属性.
 *
 * <p>绑定 application.yml 中 list-table.* 配置项, 提供类型安全的列格式配置访问.
 * 所有配置项均提供合理默认值, 配置错误时启动校验会失败.</p>
 *
 * <p>配置项说明:
 * <ul>
 *   <li>list-table.column-format.date-format — 日期列格式化模式，默认 yyyy-MM-dd</li>
 *   <li>list-table.column-format.datetime-format — 日期时间列格式化模式，默认 yyyy-MM-dd HH:mm:ss</li>
 *   <li>list-table.column-format.number-decimal-places — 数字列小数位数，默认 2</li>
 *   <li>list-table.column-format.number-thousands-separator — 数字列是否显示千分位分隔符，默认 true</li>
 *   <li>list-table.column-default.width — 列默认宽度(px)，默认 120</li>
 *   <li>list-table.column-default.min-width — 列最小宽度(px)，默认 60</li>
 *   <li>list-table.column-default.align — 列默认对齐方式(left/center/right)，默认 left</li>
 *   <li>list-table.column-default.show-overflow — 内容超出时是否显示省略提示，默认 true</li>
 *   <li>list-table.column-persist.mode — 列配置持久化模式(local/remote/both)，默认 local</li>
 *   <li>list-table.column-persist.cache-ttl-seconds — 远程持久化缓存TTL(秒)，默认 1800</li>
 *   <li>list-table.row-height.default-size — 默认行尺寸(mini~xx-large 共7档)，默认 medium</li>
 *   <li>list-table.row-height.{size}-height — 各档行高像素值(px)，共7档独立配置</li>
 *   <li>list-table.font-size.default-size — 默认字体尺寸(mini~xx-large 共7档)，默认 medium</li>
 *   <li>list-table.font-size.{size}-size — 各档字体大小像素值(px)，共7档独立配置</li>
 * </ul>
 * </p>
 *
 * @author AI
 * @since 2026-06-05
 */
@Data
@Component
@Validated
@ConfigurationProperties(prefix = "list-table")
public class ListTableProperties {

    private ColumnFormat columnFormat = new ColumnFormat();

    private ColumnDefault columnDefault = new ColumnDefault();

    private ColumnPersist columnPersist = new ColumnPersist();

    private RowHeight rowHeight = new RowHeight();

    private FontSize fontSize = new FontSize();

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
    }

    @Data
    public static class ColumnPersist {

        private String mode = "local";

        private long cacheTtlSeconds = 1800;
    }

    @Data
    public static class RowHeight {

        private String defaultSize = "medium";

        private int miniHeight = 28;

        private int smallHeight = 36;

        private int mediumHeight = 44;

        private int largeHeight = 52;

        private int looseHeight = 60;

        private int xLargeHeight = 68;

        private int xxLargeHeight = 76;
    }

    @Data
    public static class FontSize {

        private String defaultSize = "medium";

        private int miniSize = 10;

        private int smallSize = 12;

        private int mediumSize = 14;

        private int largeSize = 16;

        private int looseSize = 18;

        private int xLargeSize = 20;

        private int xxLargeSize = 22;
    }
}
