import axios from 'axios';
import authHeader from './auth-header';

const API_URL = 'http://localhost:3000/api/';

const getUsers = () => {
    return axios.get(API_URL + 'users',
        {
        headers: {
            'X-Auth-Token': authHeader(),
        },
    })
}

export default{
        getUsers,
}