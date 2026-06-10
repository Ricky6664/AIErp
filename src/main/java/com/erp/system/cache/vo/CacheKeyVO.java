package com.erp.system.cache.vo;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CacheKeyVO {

    private String key;
    private String type;
    private Long ttl;
    private Long size;
}
