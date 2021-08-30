import axios from 'axios';
import authHeader from './auth-header';

const API_URL = 'http://localhost:3000/api/';


const getItems = () => {
    return axios.get(API_URL + 'items', { headers: authHeader() });
}

export default{
    getItems,
}