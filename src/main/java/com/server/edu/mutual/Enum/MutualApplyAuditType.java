package com.server.edu.mutual.Enum;

import java.util.Objects;

public enum MutualApplyAuditType {
	/**行政学院审核 1*/
	DEPARTMENT(1),
	/**开课学院审核 2*/
	CULTURE(2);
    private final int type;
    
    private MutualApplyAuditType(int type) {
        this.type = type;
    }
    
    public int type() {
    	return this.type;
    }
    
    public boolean eq(int type) {
        return Objects.equals(this.type, type);
    }
}
