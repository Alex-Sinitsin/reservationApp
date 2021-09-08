import React from 'react';
import {Edit, SimpleForm, TextInput} from 'react-admin';

const ItemEdit = props =>(
  <Edit {...props} title="Редактирование объекта">
    <SimpleForm>
      <TextInput disabled source='id' />
      <TextInput source='name' />
    </SimpleForm>
  </Edit>
);

export default ItemEdit