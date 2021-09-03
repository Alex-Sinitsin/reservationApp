import React from 'react';
import { List, Datagrid, EmailField, TextField, Edit, SimpleForm, TextInput, ReferenceInput, SelectInput, Create, Filter } from 'react-admin';

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
      <TextField source='postname' />
      <EmailField source='email' />
  </Datagrid>
</List>
);


export const UserEdit = props =>(
  <Edit {...props}>
    <SimpleForm>
      <TextInput disabled source='id' />
      <TextInput source='name' />
      <TextInput source='postname' />
      <TextInput source='email' />
    </SimpleForm>
  </Edit>
);


export const UserCreate = props => (
  <Create {...props}>
    <SimpleForm>
      <TextInput source='name' />
      <TextInput source='postname' />
      <TextInput source='email' />
    </SimpleForm>
  </Create>
);