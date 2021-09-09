import React, {useEffect, useRef, useState} from "react";

import FullCalendar from "@fullcalendar/react";

import ruLocale from '@fullcalendar/core/locales/ru';
import listPlugin from '@fullcalendar/list';
import dayGridPlugin from '@fullcalendar/daygrid';
import timeGridPlugin from '@fullcalendar/timegrid'
import interactionPlugin from '@fullcalendar/interaction'
import bootstrapPlugin from '@fullcalendar/bootstrap'

import EventService from "./services/event.service";
import UserService from "./services/user.service";
import ItemService from "./services/item.service";
import AuthService from "./services/auth.service";

import {Modal, ModalBody, ModalFooter} from "react-bootstrap";
import ModalHeader from "react-bootstrap/ModalHeader";

import moment from 'moment';
import 'moment/locale/ru';


const MyCalendar = () => {

  const defaultEvent = {
    id: "",
    title: "",
    start: new Date(),
    end: new Date(),
    orgUserId: "",
    members: [],
    itemID: 0,
    description: "",
  }

  const defaultOrgUser = {
    id: "",
    name: "",
    lastName: "",
    position: "",
    email: "",
    role: "",
  }

  const defaultItem = {
    id: 0,
    name: "",
  }

  const [eventList, setEventList] = useState([]);

  const currentUser = AuthService.getCurrentUser();

  const [modal, setModal] = React.useState(false);

  const [eventStateData, setEventStateData] = React.useState(defaultEvent);
  const [orgUserInfo, setOrgUser] = React.useState(defaultOrgUser);
  const [itemInfo, setItemInfo] = React.useState(defaultItem);

  async function getOrgUserInfo(userID) {
    await UserService.getUserByID(userID).then(userData => setOrgUser(userData.data))
  }

  async function getItemInfo(itemID) {
    await ItemService.getItemByID(itemID).then(itemData => setItemInfo(itemData.data))
  }

  const handleEventClick = (eventInfo) => {
    getOrgUserInfo(eventInfo.event.extendedProps.orgUserID)
    getItemInfo(eventInfo.event.extendedProps.itemID)
    setEventStateData({
      id: eventInfo.event.id,
      title: eventInfo.event.title,
      start: moment(eventInfo.event.start).format("LLL"),
      end: moment(eventInfo.event.end).format("LLL"),
      orgUserID: eventInfo.event.extendedProps ? eventInfo.event.extendedProps.orgUserID : null,
      members: eventInfo.event.extendedProps ? eventInfo.event.extendedProps.members : null,
      itemID: eventInfo.event.extendedProps ? eventInfo.event.extendedProps.itemID : null,
      description: eventInfo.event.extendedProps ? eventInfo.event.extendedProps.description : null,
    });
    setModal(true);
  };

  const handleModalClose = () => {
    setModal(false);
    setEventStateData(defaultEvent);
    setOrgUser(defaultOrgUser);
  }

  const deleteEvent = (e, id) => {
    e.preventDefault();
    try {
      EventService.deleteEvent(id);
      handleModalClose();
    } catch (err) {
      console.log(err, "Удаление не удалось");
    }
  };

  const calendarRef = useRef(null);

  async function getEventData() {
    try {
      const response = await EventService.getEvents();
      const parsedList = response.data && response.data.map((event) => {
        return {
          id: event.id,
          title: event.title,
          start: event.startDateTime,
          end: event.endDateTime,
          orgUserID: event.orgUserId,
          members: event.members.users,
          itemID: event.itemId,
          description: event.description,
          color: event.members.users.find(user => user.id === currentUser.userInfo.id) ? 'orange' : event.orgUserId === currentUser.userInfo.id ? 'red' : {} //Изменяет цвет, если в событии участвует user
        }
      })

      setEventList(parsedList);
    } catch (err) {
      console.log(err, "API ERROR");
    }
  }

  // Получение данных о событиях
  useEffect(() => {
    getEventData();
  }, []);

  return (
    <div id="calendar" className="container" ref={calendarRef}>
      <FullCalendar
        themeSystem="bootstrap"
        firstDay={1}
        businessHours={{
          daysOfWeek: [1, 2, 3, 4, 5],
        }}
        navLinks={true}
        nowIndicator={true}
        height={750}
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
        footerToolbar={{
          right: "deleteEvent"
        }}
        locales={ruLocale}
        locale='ru'
        plugins={[dayGridPlugin, listPlugin, timeGridPlugin, interactionPlugin, bootstrapPlugin]}
        events={eventList}
        editable={false}
        droppable={false}
        eventResizableFromStart={true}
        allDaySlot={false}
        eventClick={handleEventClick}
      />
      {setEventStateData && (
        <Modal
          show={modal}
          onHide={handleModalClose}
        >
          <ModalHeader>
            <h4 className="col-12 modal-title text-center">{eventStateData.title}</h4>
          </ModalHeader>
          <ModalBody>
            <div>
              <p><b>Что занимается:</b> {itemInfo ? itemInfo.name : ""}</p>
              <p><b>Организатор:</b> {orgUserInfo.name} {orgUserInfo.lastName}</p>
              <p><b>Начало события:</b> {eventStateData.start.toString()}</p>
              <p><b>Конец события:</b> {eventStateData.end.toString()}</p>
              <p><b>Участники:</b></p>
              <ul>
                {eventStateData.members != [] ?
                  eventStateData.members.map(member => {
                      return (
                        <li key={member.id}>{member.name} {member.lastName}</li>
                      )
                    }
                  ) : null}
              </ul>
              <p><b>Описание:</b> {eventStateData.description}</p>
            </div>
          </ModalBody>
          <ModalFooter>
            <button type="button" className="btn btn-danger" onClick={e => deleteEvent(e,eventStateData.id)}>Удалить событие</button>
            <button type="button" className="btn btn-primary" onClick={handleModalClose}>Закрыть</button>
          </ModalFooter>
        </Modal>
      )};
    </div>
  );
}

export default MyCalendar