import React from "react";
import {Datagrid, EmailField, FunctionField, List, TextField} from 'react-admin';

const usersList = props => (
  <List {...props}>
    <Datagrid rowClick='edit'>
      <TextField source='name' label="Имя" sortable={false}/>
      <TextField source='lastName' label="Фамилия" sortable={false}/>
      <TextField source='position' label="Должность" sortable={false}/>
      <EmailField source='email' label="Почта" sortable={false}/>
      <FunctionField
        label="Роль"
        render={record => record.role == "User" ? "Пользователь" : "Администратор"}/>
    </Datagrid>
  </List>
);

export default usersList