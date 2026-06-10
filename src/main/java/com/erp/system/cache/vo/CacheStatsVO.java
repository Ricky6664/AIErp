package com.erp.system.cache.vo;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CacheStatsVO {

    private Long keyCount;
    private String serverVersion;
    private Long usedMemory;
    private Long uptimeInSeconds;
}
