import React from "react";
import {SimpleForm, TextInput, PasswordInput, Create, required, email, minLength} from 'react-admin';

const validateName = required();
const validateLastName = required();
const validatePosition = required();
const validateEmail = [required(), email()];
const validatePassword = [required(), minLength(8)];
const validateConfirmPassword = (value, allValues) => {
    if (!value) return 'ra.validation.required';
    if (value.length < 8) return {
      message: 'ra.validation.minLength',
      args: {min: 8}
    }
    if (allValues.password != value
    )
      return 'Введенные пароли не совпадают';

    return undefined;
  }
;

const UserCreate = props => (
  <Create {...props} title="Добавление нового пользователя">
    <SimpleForm>
      <TextInput source='name' label="Имя" validate={validateName}/>
      <TextInput source='lastName' label="Фамилия" validate={validateLastName}/>
      <TextInput source='position' label="Должность" validate={validatePosition}/>
      <TextInput source='email' label="Почта" validate={validateEmail}/>
      <PasswordInput source='password' label="Пароль" validate={validatePassword}/>
      <PasswordInput source='confirmPassword' label="Подтверждение пароля" validate={validateConfirmPassword}/>
    </SimpleForm>
  </Create>
);

export default UserCreate