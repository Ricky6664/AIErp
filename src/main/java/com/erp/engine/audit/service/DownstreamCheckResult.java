package com.erp.engine.audit.service;

import lombok.Data;

/**
 * 下游单据检查结果.
 *
 * @author AI
 */
@Data
public class DownstreamCheckResult {

    private boolean hasDownstream;

    private String downstreamDocNo;

    public static DownstreamCheckResult none() {
        DownstreamCheckResult r = new DownstreamCheckResult();
        r.setHasDownstream(false);
        return r;
    }

    public static DownstreamCheckResult exists(String docNo) {
        DownstreamCheckResult r = new DownstreamCheckResult();
        r.setHasDownstream(true);
        r.setDownstreamDocNo(docNo);
        return r;
    }
}
