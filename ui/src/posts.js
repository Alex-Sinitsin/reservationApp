import * as React from 'react';
import { List, useListContext, Filter, Create, TextField, ReferenceField, EditButton, SelectInput, Edit, SimpleForm, TextInput, ReferenceInput } from 'react-admin';
import { Card, CardActions, CardContent, CardHeader, Avatar } from '@material-ui/core';
import PersonIcon from '@material-ui/icons/Person';

const PostFilter = (props) => (
  <Filter {...props}>
          <ReferenceInput source='userId' reference='users' allowEmpty>
                 <SelectInput source='name' />
          </ReferenceInput>
  </Filter>
);

const cardStyle = {
    width: 300,
    minHeight: 300,
    margin: '0.5em',
    display: 'inline-block',
    verticalAlign: 'top'
};
const CommentGrid = () => {
    const { ids, data } = useListContext();
    return (
        <div style={{ margin: '1em' }}>
        {ids.map(id =>
            <Card key={id} style={cardStyle}>
                <CardHeader
                    title={<ReferenceField record={data[id]} source='userId' reference='users'>
                                 <TextField source='name' />
                                 </ReferenceField>}
                    avatar={<Avatar icon={<PersonIcon />} />}
                />
                <CardContent>
                    Start event:&nbsp;
                   <TextField record={data[id]} source='start' />
                </CardContent>
                <CardContent>
                   End event:&nbsp;
                   <TextField record={data[id]} source='end' />
                </CardContent>
                <CardContent>
                    <TextField record={data[id]} source='title' />
                </CardContent>
                <CardContent>
                    Members:&nbsp;
                    <TextField record={data[id]} source='members' />
                </CardContent>
                <CardActions style={{ textAlign: 'right' }}>
                    <EditButton resource="posts" record={data[id]} />
                </CardActions>
            </Card>
        )}
        </div>
    );
};

export const PostList = (props) => (
    <List title="All posts" filters={<PostFilter />} {...props}>
        <CommentGrid />
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
      <TextInput source='members' />
    </SimpleForm>
  </Edit>
);

export const PostCreate = (props) => (
  <Create {...props}>

    <SimpleForm >
      <ReferenceInput source='userId' reference='users'>
      <SelectInput optionText='name' />
      </ReferenceInput>
      <TextInput source='title' />
      <TextInput source='start' />
      <TextInput source='end' />
      <TextInput source='members' />
    </SimpleForm>
  </Create>
);

