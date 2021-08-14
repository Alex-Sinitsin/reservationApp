import React from 'react';
import { Create, List, Datagrid, TextField, EmailField, Edit, SimpleForm, TextInput } from 'react-admin';

export const UserList = props => (
  <List {...props}>
    <Datagrid rowClick='edit'>
      <TextField source='id' />
      <TextField source='name' />
      <TextField source='username' />
      <EmailField source='email' />
      <TextField source='phone' />
    </Datagrid>
  </List>
);


export const UserEdit = props =>(
  <Edit {...props}>
    <SimpleForm>
      <TextInput disabled source='id' />
      <TextInput source='name' />
      <TextInput source='username' />
      <TextInput source='email' />
      <TextInput source='phone' />
    </SimpleForm>
  </Edit>
);


export const UserCreate = props => (
  <Create {...props}>
    <SimpleForm>
      <TextInput source='name' />
      <TextInput source='username' />
      <TextInput source='email' />
      <TextInput source='phone' />
    </SimpleForm>
  </Create>
);
