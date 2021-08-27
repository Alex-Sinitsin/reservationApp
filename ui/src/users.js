import React from 'react';
import { List, Datagrid, EmailField, TextField, ReferenceField, EditButton, Edit, SimpleForm, TextInput, ReferenceInput, SelectInput, Create, Filter } from 'react-admin';

const UserFilter = (props) => (
  <Filter {...props}>
        <ReferenceInput label='Name' source='id' reference='users' allowEmpty>
          <SelectInput optionText='name' />
        </ReferenceInput>
  </Filter>
);

export const UserList = props => (
<List filters={<UserFilter />} {...props}>
    <Datagrid rowClick='edit'>
      <TextField source='id' />
      <TextField source='name' />
      <TextField source='username' />
      <TextField source='postname' />
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
      <TextInput source='postname' />
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
      <TextInput source='postname' />
      <TextInput source='email' />
      <TextInput source='phone' />
    </SimpleForm>
  </Create>
);
