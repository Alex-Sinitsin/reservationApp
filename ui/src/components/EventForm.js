import React, {useState, useRef, useEffect} from "react";
import Form from "react-validation/build/form";
import Input from "react-validation/build/input";
import CheckButton from "react-validation/build/button";

import AuthService from "../services/auth.service";
import EventService from "../services/event.service";
import "./EventForm.css";
import Select from 'react-select'

import UserService from "../services/user.service"
import ItemService from "../services/item.service"


// Требование заполненности поля
const required = value => {
  if (!value) {
    return (
      <div className="invalid-feedback d-block">
        Заполните поле
      </div>
    );
  }
};


const AddEvent = (props) => {

  const { successful, handleAddEvent } = props;

  const form = useRef();
  const checkBtn = useRef();
  const currentUser = AuthService.getCurrentUser();

  const [orgUserID] = useState(currentUser.userInfo.id);
  const [formData, setFormData] = React.useState({
    title: "",
    startDateTime: null,
    endDateTime: null,
    orgUserID: orgUserID,
    members: [],
    itemID: null,
    description: ""
  })

  const [membersList, setMembersList] = useState("");

  const [itemList, setItemList] = useState([]);

  const onChangeTitle = (e) => {
    const title = e.target.value;
    let formDataCopy = {...formData}
    formDataCopy.title = title;
    setFormData(formDataCopy);
  };

  const onChangeStartDateTime = (e) => {
    const startDateTime = e.target.value;
    let formDataCopy = {...formData}
    formDataCopy.startDateTime = startDateTime;
    setFormData(formDataCopy);
  };

  const onChangeEndDateTime = (e) => {
    const endDateTime = e.target.value;
    let formDataCopy = {...formData}
    formDataCopy.endDateTime = endDateTime;
    setFormData(formDataCopy);
  };

  const onChangeDescription = (e) => {
    const description = e.target.value;
    let formDataCopy = {...formData}
    formDataCopy.description = description;
    setFormData(formDataCopy);
  };

  const handleChangeItem = (selectedItem) => {
    let formDataCopy = {...formData}
    formDataCopy.itemID = selectedItem;
    setFormData(formDataCopy);
  }

  const handleChangeMembers = (selectedMembers) => {
    let formDataCopy = {...formData}
    formDataCopy.members = selectedMembers;
    setFormData(formDataCopy);
  }

  // Получение данных об Items в Select
  async function getItemData() {
    try {
      const response = await ItemService.getItems();
      const parsedList = response.data && response.data.map((item) => {

        return {
          value: item.id,
          label: `${item.name}`
        }
      })

      setItemList(parsedList);
    } catch (err) {
      console.log(err, "API ERROR");
    }
  }

  useEffect(() => {
    getItemData();
  }, []);

  useEffect(() => {
    setFormData({
      title: "",
      startDateTime: null,
      endDateTime: null,
      orgUserID: orgUserID,
      members: [],
      itemID: null,
      description: ""
    })
  }, [successful]);


  // Получение данных о Users в Select
  async function getMembersData() {
    try {
      const response = await UserService.getUsers()

      let userList = response.data && response.data.filter(user => user.id !== currentUser.userInfo.id)

      const parsedList = userList && userList.map((user) => {

        return {
          value: user.id,
          label: `${user.name} ${user.lastName} - ${user.position}`
        }
      })

      setMembersList(parsedList);
    } catch (err) {
      console.log(err, "API ERROR");
    }
  }

  useEffect(() => {
    getMembersData();
  }, []);


  return (
    <div className="card card-container">
      <Form onSubmit={(e) => handleAddEvent(e, form, checkBtn, formData)} ref={form}>
        <div>
          <div className="title-form">Создание события</div>

          <div className="form-group">
            <label htmlFor="itemID">Помещение/Предмет: <span className="required-field">*</span></label>
            <Select
              value={formData.itemID}
              onChange={handleChangeItem}
              options={itemList}
              placeholder={null}
              components={{IndicatorSeparator: () => null}}
              validations={[required]}
            />
          </div>


          <div className="form-group">
            <label htmlFor="title">Название события: <span className="required-field">*</span></label>
            <Input
              type="text"
              className="form-control"
              name="title"
              value={formData.title}
              onChange={onChangeTitle}
              validations={[required]}
            />
          </div>

          <div className="form-group">
            <label htmlFor="startDateTime">Время начала: <span className="required-field">*</span></label>
            <Input
              type="datetime-local"
              className="form-control"
              name="startDateTime"
              value={formData.startDateTime}
              onChange={onChangeStartDateTime}
              validations={[required]}
            />
          </div>

          <div className="form-group">
            <label htmlFor="endDateTime">Время окончания: <span className="required-field">*</span></label>
            <Input
              type="datetime-local"
              className="form-control"
              name="endDateTime"
              value={formData.endDateTime}
              onChange={onChangeEndDateTime}
              validations={[required]}
            />
          </div>

          <div className="form-group">
            <label htmlFor="members">Участники:</label>
            <Select
              isMulti
              name="members"
              value={formData.members}
              options={membersList}
              placeholder={null}
              onChange={handleChangeMembers}
              components={{IndicatorSeparator: () => null}}
              className="basic-multi-select"
              classNamePrefix="select"
            />
          </div>

          <div className="form-group">
            <label htmlFor="description">Описание:</label>
            <Input
              type="description"
              className="form-control"
              name="description"
              value={formData.description}
              onChange={onChangeDescription}
            />
          </div>


          <div className="form-group">
            <button className="btn eventSubmitBtn btn-block btn-primary">Добавить событие</button>
          </div>
        </div>

        <CheckButton style={{display: "none"}} ref={checkBtn}/>
      </Form>
    </div>
  );
}

export default AddEvent;