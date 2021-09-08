import React from 'react';
import {Create, SimpleForm, TextInput, required} from 'react-admin';

const validateName = required();

const ItemCreate = props => (
  <Create {...props} title="Добавление нового объекта">
    <SimpleForm>
      <TextInput source='name' label="Имя" validate={validateName} />
    </SimpleForm>
  </Create>
);

export default ItemCreate