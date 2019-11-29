package com.server.edu.mutual.Enum;

import java.util.Objects;

public enum MutualApplyAuditStatus {
	/**未审核 0*/
	UN_AUDITED(0),
	/**行政学院审核通过 1*/
	DEPART_AUDITED_APPROVED(1),
	/**行政学院审核不通过 2*/
	DEPART_AUDITED_UN_APPROVED(2),
	/**审核通过 3*/
	AUDITED_APPROVED(3),
	/**审核不通过 4*/
	AUDITED_UN_APPROVED(4);
	
    private final int status;
    
    private MutualApplyAuditStatus(int status) {
        this.status = status;
    }
    
    public int status()
    {
        return this.status;
    }
    
    public boolean eq(int status) {
        return Objects.equals(this.status, status);
    }
	

}
