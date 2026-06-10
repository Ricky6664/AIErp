package com.erp.module.org.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 组织架构工作台聚合数据VO.
 *
 * @author AI
 * @since 2026-06-08
 */
@Data
@Schema(description = "组织架构工作台聚合数据")
public class WorkbenchVO {

    @Schema(description = "公司总数")
    private Long companyCount;

    @Schema(description = "部门总数")
    private Long departmentCount;

    @Schema(description = "岗位总数")
    private Long positionCount;

    @Schema(description = "在职员工总数")
    private Long employeeCount;

    @Schema(description = "部门类型分布")
    private List<TypeDistribution> deptTypeDistribution;

    @Schema(description = "各公司部门数量")
    private List<CompanyDeptCount> companyDeptCount;

    @Schema(description = "各部门人员分布")
    private List<DeptStaffCount> deptStaffDistribution;

    @Data
    @Schema(description = "部门类型分布项")
    public static class TypeDistribution {

        @Schema(description = "部门类型", example = "business")
        private String deptType;

        @Schema(description = "数量")
        private Long count;
    }

    @Data
    @Schema(description = "各公司部门数量项")
    public static class CompanyDeptCount {

        @Schema(description = "公司ID")
        private Long companyId;

        @Schema(description = "公司名称")
        private String companyName;

        @Schema(description = "部门数量")
        private Long deptCount;
    }

    @Data
    @Schema(description = "各部门人员分布项")
    public static class DeptStaffCount {

        @Schema(description = "部门ID")
        private Long deptId;

        @Schema(description = "部门名称")
        private String deptName;

        @Schema(description = "员工数量")
        private Long staffCount;

        @Schema(description = "占比", example = "25.5")
        private String percentage;
    }
}
