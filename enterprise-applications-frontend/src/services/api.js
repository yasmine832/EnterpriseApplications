import axios from 'axios';


const api = axios.create({
    baseURL: 'http://localhost:8080/api',
    headers: {
        'Content-Type': 'application/json',
    },
    withCredentials: true  // Important for sending cookies
});

const API = {
    products: {
        getAll: (params) => api.get('/products', { params }),
        search: (query) => api.get('/products/search', { params: { query } }),
        getById: (id) => api.get(`/products/${id}`),
        checkAvailability: (productId, startDate, endDate, quantity) =>
            api.get(`/products`, {
                params: {
                    startDate: startDate,
                    endDate: endDate
                }            })


    },
    reservations: {
        create: (data) => api.post('/reservations', data),
        getById: (id) => api.get(`/reservations/${id}`),
        getAll: () => api.get('/reservations'),
        getMyReservations: (status) => api.get('/reservations/my-reservations', { params: { status } }),
        confirmPayment: (id) => api.put(`/reservations/${id}/confirm-payment`),
        cancel: (id, confirm = true) => api.delete(`/reservations/${id}?confirm=${confirm}`)
    },

    auth: {
        login: (credentials) => api.post('http://localhost:8080/login', credentials),
        register: (userData) => api.post('/auth/register', userData),
        getCurrentUser: () => api.get('/auth/current')
        //logout:

    }
};

export default API;

