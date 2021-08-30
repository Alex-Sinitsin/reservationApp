import React from 'react';
import { List, Filter, SimpleList, Create, Datagrid, TextField, ReferenceField, EditButton, SelectInput, Edit, SimpleForm, TextInput, ReferenceInput } from 'react-admin';
import userName from './components/Home';

const PostFilter = (props) => (
  <Filter {...props}>
    <ReferenceInput label='User' source='userId' reference='users' allowEmpty>
      <SelectInput optionText='name' />
    </ReferenceInput>
  </Filter>
);

export const PostList = props => (
  <List filters={<PostFilter />} {...props}>
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
      <TextInput source='title' />
      <TextInput source='start' />
      <TextInput source='end' />
    </SimpleForm>
  </Create>
);