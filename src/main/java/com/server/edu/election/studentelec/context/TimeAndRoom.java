package com.server.edu.election.studentelec.context;

import com.server.edu.dictionary.annotation.Code2Text;
import com.server.edu.dictionary.annotation.CodeI18n;
import com.server.edu.dictionary.translator.ClassRoomTranslator;

@CodeI18n
public class TimeAndRoom {
    @Code2Text(translator = ClassRoomTranslator.class)
    private String roomId;
    
    private Long timeId; //教学班排课时间ID
    
    private String timeAndRoom;

	public String getRoomId() {
		return roomId;
	}

	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}

	public Long getTimeId() {
		return timeId;
	}

	public void setTimeId(Long timeId) {
		this.timeId = timeId;
	}

	public String getTimeAndRoom() {
		return timeAndRoom;
	}

	public void setTimeAndRoom(String timeAndRoom) {
		this.timeAndRoom = timeAndRoom;
	}
    
    
}
