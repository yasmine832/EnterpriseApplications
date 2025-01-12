import React from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { CssBaseline } from '@mui/material';
import { LocalizationProvider } from '@mui/x-date-pickers';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import ProductList from './components/Products/ProductList';
import {CartProvider} from "./components/Cart/CartContext";
import { createTheme, ThemeProvider } from '@mui/material/styles';
import Cart from './components/Cart/Cart';

import {AuthProvider} from "./components/Auth/Authcontext"
import ProtectedRoute from './components/Auth/ProtectedRoute';
import Navigation from "./components/Navigation";
//themeprovider




function App() {
    return (

        <AuthProvider>
        <LocalizationProvider dateAdapter={AdapterDayjs}>
            <CartProvider>
                <CssBaseline />
                <BrowserRouter>
                    <Navigation/>
                    <Routes>
                        <Route path="/" element={<ProductList />} />
                        <Route path="/cart" element={
                            <ProtectedRoute>
                                <Cart />
                            </ProtectedRoute>
                        } />
                    </Routes>
                </BrowserRouter>
            </CartProvider>
        </LocalizationProvider>
            </AuthProvider>


    );
}


export default App;
