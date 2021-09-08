import React from 'react';
import {Datagrid, List, TextField, EditButton, CreateButton, TopToolbar } from 'react-admin';

const ItemEditButton = ({ record }) => (
  <EditButton basePath="/items" label="Изменить" record={record} />
);

const ItemsListActions = ({ basePath }) => (
  <TopToolbar>
    <CreateButton basePath={basePath} />
  </TopToolbar>
);

const ItemList = props => (
  <List actions={<ItemsListActions />} {...props} >
    <Datagrid>
      <TextField source='id' label="ID" sortable={false}/>
      <TextField source='name' label="Имя" sortable={false}/>
      <ItemEditButton />
    </Datagrid>
  </List>
);

export default ItemList