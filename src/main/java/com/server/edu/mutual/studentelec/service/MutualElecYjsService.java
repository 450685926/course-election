package com.server.edu.mutual.studentelec.service;

import com.server.edu.election.studentelec.context.ElecRequest;
import com.server.edu.election.studentelec.context.IElecContext;

/**
 *  本科生选课
 * @author xlluoc
 *
 */
public interface MutualElecYjsService {

	/**
             * 进行选课
     * @param request
     * @param context
     * @see [类、类#方法、类#成员]
     */
	IElecContext doELec(ElecRequest request);

}
