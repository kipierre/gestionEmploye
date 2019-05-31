package org.gestionemploye.mapper;

import org.gestionemploye.dto.EventDTO;
import org.gestionemploye.entity.Employee;
import org.gestionemploye.entity.Event;
import org.gestionemploye.entity.Leave;

public class EventMapper {

    public static EventDTO mapLeaveToEventDto(Leave leave) {
        EventDTO eventDTO = new EventDTO();
        Employee employee = leave.getEmployee();
        String middleName = (employee.getMiddleName() !=null)? employee.getMiddleName() : "";
        String fullName = employee.getFirstName() + " " + middleName + " " + employee.getLastName();
        eventDTO.setTitle(fullName+" is on "+leave.getLeaveType().getTypeName());
        eventDTO.setEventType("leave");
        eventDTO.setEventId(leave.getLeaveId());
        eventDTO.setStartDate(leave.getFromDate());
        eventDTO.setEndDate(leave.getToDate());
        eventDTO.setCreatedAt(leave.getCreatedAt());
        return eventDTO;
    }

    public static EventDTO mapEventToEventDto(Event event) {
        EventDTO eventDTO = new EventDTO();
        eventDTO.setEventId(event.getId());
        eventDTO.setTitle(event.getTitle());
        eventDTO.setEventType(event.getEventType());
        eventDTO.setStartDate(event.getStartDate());
        eventDTO.setEndDate(event.getEndDate());
        eventDTO.setCreatedAt(event.getCreatedAt());
        return eventDTO;
    }

    public static Event mapEventToEntity(EventDTO eventDTO){
        Event event = new Event();
        event.setId(eventDTO.getEventId());
        event.setTitle(eventDTO.getTitle());
        event.setStartDate(eventDTO.getStartDate());
        event.setEndDate(eventDTO.getEndDate());
        event.setEventType(eventDTO.getEventType());
        event.setCreatedAt(eventDTO.getCreatedAt());
        return event;
    }


}
