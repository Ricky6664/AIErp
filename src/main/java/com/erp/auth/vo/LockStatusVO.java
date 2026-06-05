package com.erp.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户锁定状态")
public class LockStatusVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "是否已锁定", example = "true")
    private boolean locked;

    @Schema(description = "剩余锁定秒数", example = "845")
    private long remainingSeconds;

    @Schema(description = "剩余锁定分钟数", example = "14")
    private long remainingMinutes;
}
