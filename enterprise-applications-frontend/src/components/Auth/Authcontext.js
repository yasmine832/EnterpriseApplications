// todo source
import React, {createContext, useContext, useEffect, useState} from 'react';
import API from '../../services/api';
import axios from "axios";

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
    const [currentUser, setCurrentUser] = useState(null);
    const [authError, setAuthError] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    // Check if user is already logged in on mount
    useEffect(() => {
        checkAuthStatus();
    }, []);

    const checkAuthStatus = async () => {
        try {
            const token = localStorage.getItem('token');
            if (!token) {
                setCurrentUser(null);
                return;
            }

            const response = await API.auth.getCurrentUser();
            if (response.data) {
                setCurrentUser(response.data);
            } else {
                setCurrentUser(null);
            }
        } catch (err) {
            console.error('Auth check failed:', err);
            setCurrentUser(null);
        } finally {
            setLoading(false);
        }
    }; //todo source

    const login = async (credentials) => {
        try {
            await axios.post('http://localhost:8080/login',
                new URLSearchParams({
                    username: credentials.username,
                    password: credentials.password,
                    'remember-me': credentials.rememberMe
                }),
                { withCredentials: true }
            );
            await getCurrentUser();  // Update user after login
        } catch (error) {
            throw new Error('Invalid credentials');
        }
    };

    const getCurrentUser = async () => {
        try {
            const response = await API.auth.getCurrentUser( { withCredentials: true });
            setCurrentUser(response.data);
        } catch (error) {
            console.error('Error getting current user:', error);
            setCurrentUser(null);
        } finally {
            setLoading(false);
        }
    };

    const register = async (userData) => {
        setLoading(true);
        setAuthError(null);
        try {
            const response = await API.auth.register(userData);
            return response.data;
        } catch (error) {
            setAuthError(error.response?.data?.message || 'Registration failed');
            throw error;
        } finally {
            setLoading(false);
        }
    };

    const logout = async () => {
        try {
            localStorage.removeItem('token');
            sessionStorage.removeItem('token');
            document.cookie = 'token=; Max-Age=0; path=/;'
            setCurrentUser(null);
            console.log('User logged out successfully.');
        } catch (error) {
            console.error('Logout failed:', error);
        }
    };

    return (
        <AuthContext.Provider value={{
            currentUser,
            login,
            register,
            logout,
            authError,
            loading,
            error
        }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => useContext(AuthContext);