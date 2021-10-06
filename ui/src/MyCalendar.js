import React, {useEffect, useRef, useState} from "react";

import FullCalendar from "@fullcalendar/react";

import ruLocale from '@fullcalendar/core/locales/ru';
import listPlugin from '@fullcalendar/list';
import dayGridPlugin from '@fullcalendar/daygrid';
import timeGridPlugin from '@fullcalendar/timegrid'
import interactionPlugin from '@fullcalendar/interaction'
import bootstrapPlugin from '@fullcalendar/bootstrap'

import CalendarEventModal from "./components/Home/CalendarEventModal"


const MyCalendar = (props) => {

  const { events, editEventData, orgUserData, itemData, handleDeleteEvent, isDeleteButtonVisible, isModalVisible, handleModalClose, handleEventClick } = props

  const calendarRef = useRef(null);

  return (
    <div id="calendar" className="container" ref={calendarRef}>
      <FullCalendar
        themeSystem="bootstrap"
        firstDay={1}
        businessHours={{
          daysOfWeek: [1, 2, 3, 4, 5],
        }}
        slotMinTime="09:00:00"
        slotMaxTime="18:00:00"
        navLinks={true}
        nowIndicator={true}
        height={770}
        slotDuration={'00:30:00'}
        defaultView="dayGridMonth"
        headerToolbar={{
          left: 'prev,today,next',
          center: 'title',
          right: "dayGridMonth,timeGridWeek,timeGridDay,listMonth"
        }}
        buttonText={{
          prev: '<<',
          next: '>>',
          listMonth: 'Расписание'
        }}
        dayMaxEvents={true}
        locales={ruLocale}
        locale='ru'
        plugins={[dayGridPlugin, listPlugin, timeGridPlugin, interactionPlugin, bootstrapPlugin]}
        events={events}
        editable={false}
        droppable={false}
        eventResizableFromStart={true}
        allDaySlot={false}
        eventClick={handleEventClick}
        displayEventEnd={true}
        eventTimeFormat={
          {
            hour: '2-digit',
            minute: '2-digit',
            meridiem: false,
            hour12: false
          }}
      />
      {editEventData && (
        <CalendarEventModal handleModalClose={handleModalClose} handleDeleteEvent={handleDeleteEvent} eventData={editEventData} itemData={itemData} orgUserData={orgUserData} isModalVisible={isModalVisible} isDeleteButtonVisible={isDeleteButtonVisible}/>
      )}
    </div>
  );
}

export default MyCalendar