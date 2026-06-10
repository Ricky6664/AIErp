package com.erp.module.message.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 参与人VO.
 *
 * @author AI
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantVO {

    private Long userId;

    private String userName;
}
