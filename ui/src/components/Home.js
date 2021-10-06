import React, {useEffect, useState} from 'react'
import Toast from "react-bootstrap/Toast";
import './Home.css'
import Header from './Header'
import MyCalendar from '../MyCalendar';
import EventForm from "./EventForm";
import FilterForm from "./FilterForm";

import EventService from "../services/event.service";
import UserService from "../services/user.service";
import ItemService from "../services/item.service";
import AuthService from "../services/auth.service";
import {defaultEvent, defaultOrgUser, defaultItem} from "../components/DefaultValues";

import moment from 'moment';
import 'moment/locale/ru';

const Home = () => {

  const [eventList, setEventList] = useState([]);
  const [filteredEventList, setFilteredEventList] = useState([]);

  const [isModalVisible, setIsModalVisible] = React.useState(false);
  const [isNotificationVisible, setIsNotificationVisible] = React.useState(false);
  const [isDeleteButtonVisible, setIsDeleteButtonVisible] = React.useState(false)

  const [editEventData, setEditEventData] = React.useState(defaultEvent);
  const [orgUserData, setOrgUserData] = React.useState(defaultOrgUser);
  const [itemData, setItemData] = React.useState(defaultItem);

  const [successfulAddEvent, setSuccessfulAddEvent] = useState(false);
  const [message, setMessage] = useState("");

  const currentUser = AuthService.getCurrentUser();

  // Данные для фильтров
  const initialItemList = [{value: 0, label: 'Все'}]
  const [itemList, setItemList] = useState(initialItemList);
  const [item, setItem] = useState(itemList[0]);
  const whoseEventList = [
    {value: 0, label: 'Все события'},
    {value: 1, label: 'События с моим участием'},
    {value: 2, label: 'Созданные мной события'},
    {value: 3, label: 'Чужие события'}]
  const [whoseEvent, setWhoseEvent] = useState(whoseEventList[0]);

  // Функция запроса к API для получения событий
  async function getEventData() {

    try {
      const response = await EventService.getEvents();
      const parsedList = response && response.data.map((event) => {
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
      setFilteredEventList(filterByBelonging(filterByItem(parsedList, item), whoseEvent))
    } catch (err) {
      setMessage("Произошла ошибка при получении данных с сервера!");
      setIsNotificationVisible(true)
    }
  }

  const handleAddEvent = (formData) => {
    setMessage("");
    setIsNotificationVisible(false)
    setSuccessfulAddEvent(false);

    EventService.add(
      formData.title,
      formData.startDateTime.replace('T', ' '),
      formData.endDateTime.replace('T', ' '),
      formData.orgUserID,
      formData.members ? formData.members.map(x => x.value) : "[]",
      formData.itemID.value,
      formData.description
    ).then(
      (response) => {
        setMessage(response.data.message);
        setIsNotificationVisible(true)
        setSuccessfulAddEvent(true);
        getEventData();
      },
      (error) => {
        const resMessage =
          (error.response &&
            error.response.data &&
            error.response.data.message) ||
          error.message ||
          error.toString();

        setMessage(resMessage);
        setIsNotificationVisible(true)
        setSuccessfulAddEvent(false);
      }
    );
  };

  const handleDeleteEvent = (e, eventID) => {
    e.preventDefault();
    try {
      EventService.deleteEvent(eventID).then(
        (response) => {
          setMessage(response.data.message);
          setIsNotificationVisible(true)
          setIsModalVisible(false);
          setEditEventData(defaultEvent)
          setOrgUserData(defaultOrgUser)
          setItemData(defaultItem)
          getEventData();
        }
      );

    } catch (err) {
      setMessage("Удаление события не удалось!");
      setIsNotificationVisible(true)
    }
  };

  const showDeleteButton = (orgUserId) => {
    currentUser.userInfo.id === orgUserId || currentUser.userInfo.role === "Admin" ? setIsDeleteButtonVisible(true) : setIsDeleteButtonVisible(false);
  }

  async function getOrgUserInfo(userID) {
    await UserService.getUserByID(userID).then(userData => setOrgUserData(userData.data))
  }

  async function getItemInfo(itemID) {
    await ItemService.getItemByID(itemID).then(itemData => setItemData(itemData.data))
  }

  const handleEventClick = (eventInfo) => {
    getOrgUserInfo(eventInfo.event.extendedProps.orgUserID)
    getItemInfo(eventInfo.event.extendedProps.itemID)
    setEditEventData({
      id: eventInfo.event.id,
      title: eventInfo.event.title,
      start: moment(eventInfo.event.start).format("LLL"),
      end: moment(eventInfo.event.end).format("LLL"),
      orgUserID: eventInfo.event.extendedProps ? eventInfo.event.extendedProps.orgUserID : null,
      members: eventInfo.event.extendedProps ? eventInfo.event.extendedProps.members : null,
      itemID: eventInfo.event.extendedProps ? eventInfo.event.extendedProps.itemID : null,
      description: eventInfo.event.extendedProps ? eventInfo.event.extendedProps.description : null,
    });
    showDeleteButton(eventInfo.event.extendedProps && eventInfo.event.extendedProps.orgUserID);
    setIsModalVisible(true);
  };

  const handleModalClose = () => {
    setIsModalVisible(false);
    setIsDeleteButtonVisible(false);
    setEditEventData(defaultEvent);
    setOrgUserData(defaultOrgUser);
  }

  useEffect(() => {
    document.title = 'Календарь';
    getEventData();
  }, []);



  // Фильтрация по объекту для данных календаря
  const filterByItem = (events, chosenItemID) => {
    switch (chosenItemID.value) {
      case 0: return events; // Все события
      default: return events.filter(event => event.itemID === chosenItemID.value);
    }
  }

  // Фильтрация по принадлежности для данных календаря
  const filterByBelonging = (events, whoseEvent) => {
    const currentUser = AuthService.getCurrentUser();
    switch (whoseEvent.value) {
      case 1:  return events.filter(event => event.orgUserID === currentUser.userInfo.id || event.members.find(user => user.id === currentUser.userInfo.id)); // События с участием пользователя
      case 2:  return events.filter(event => event.orgUserID === currentUser.userInfo.id); // Созданные пользователем события
      case 3:  return events.filter(event => event.orgUserID !== currentUser.userInfo.id && (event.members.find(user => user.id === currentUser.userInfo.id) === undefined)); // Чужие события
      default: return events; // Все события
    }
  }

  // Производит фильтрацию
  const handleFilter = () => {
    setFilteredEventList(filterByBelonging(filterByItem(eventList, item), whoseEvent))
  }


  return (
    <div>
      <Header/>
      <div className="container">
        <div className="row">
          <div className="col-md-3 eventForm">
            <EventForm successful={successfulAddEvent} handleAddEvent={handleAddEvent}/>
          </div>
          <div className="calendar col-md-9 mb-5">
            <FilterForm
                initialItemList={initialItemList}
                item={item} setItem={setItem}
                itemList={itemList}
                setItemList={setItemList}
                whoseEvent={whoseEvent}
                setWhoseEvent={setWhoseEvent}
                whoseEventList={whoseEventList}
                handleFilter={handleFilter}/>


            <MyCalendar
                events={filteredEventList}
                handleEventClick={handleEventClick}
                handleDeleteEvent={handleDeleteEvent}
                handleModalClose={handleModalClose}
                editEventData={editEventData}
                orgUserData={orgUserData}
                itemData={itemData}
                isModalVisible={isModalVisible}
                isDeleteButtonVisible={isDeleteButtonVisible}
            />
          </div>
        </div>
      </div>
      {message ? (
        <Toast className="notificationMessage" onClose={() => setIsNotificationVisible(false)}
               show={isNotificationVisible} delay={5000} autohide>
          <Toast.Header closeButton={false}>
            <strong className="me-auto">Сообщение</strong>
            <small className="text-muted">{moment().toNow(true)}</small>
          </Toast.Header>
          <Toast.Body>{message}</Toast.Body>
        </Toast>
      ) : null}
    </div>
  )
}

export default Home;