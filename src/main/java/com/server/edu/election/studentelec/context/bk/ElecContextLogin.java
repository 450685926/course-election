package com.server.edu.election.studentelec.context.bk;
import com.server.edu.election.studentelec.context.ElecRequest;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.context.IElecContext;

public class ElecContextLogin implements IElecContext{
	
    private ElecRequest request;
    
    private ElecRespose respose;
    
    public ElecContextLogin(
            ElecRequest elecRequest,ElecRespose respose) {
         this.request = elecRequest;
         this.respose = respose;
    }

	public ElecRequest getRequest() {
		return request;
	}

	public void setRespose(ElecRespose respose) {
		this.respose = respose;
	}

	@Override
	public void saveToCache() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void saveResponse() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setRequest(ElecRequest request) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ElecRespose getRespose() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long getCalendarId() {
		// TODO Auto-generated method stub
		return null;
	}


}
