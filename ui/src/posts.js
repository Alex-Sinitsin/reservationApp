import React from 'react';
import { List, Create, Datagrid, TextField, ReferenceField, EditButton, SelectInput, Edit, SimpleForm, TextInput, ReferenceInput } from 'react-admin';

export const PostList = props => (
  <List {...props}>
    <Datagrid>
      <TextField source='id' />
      <ReferenceField source='userId' reference='users'>
        <TextField source='name' />
      </ReferenceField>
      <TextField source='title' />
      <TextField source='start' />
      <TextField source='end' />
      <EditButton />
    </Datagrid>
  </List>
);

export const PostEdit = props => (
  <Edit {...props}>
    <SimpleForm>
      <TextInput disabled source='id' />
      <ReferenceInput source='userId' reference='users'>
        <SelectInput optionText='name' />
      </ReferenceInput>
      <TextInput source='title' />
      <TextInput source='start' />
      <TextInput source='end' />
    </SimpleForm>
  </Edit>
);

export const PostCreate = props => (
  <Create {...props}>
    <SimpleForm>
      <ReferenceInput source='userId' reference='users'>
        <SelectInput optionText='name' />
      </ReferenceInput>
      <SelectInput optionText='title' />
      <SelectInput optionText='start' />
      <SelectInput optionText='end' />
    </SimpleForm>
  </Create>
);