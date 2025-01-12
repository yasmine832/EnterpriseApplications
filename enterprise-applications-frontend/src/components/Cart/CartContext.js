import React, {createContext, useContext, useEffect, useState} from 'react';
import API from "../../services/api";
import {useAuth} from "../Auth/Authcontext";


const CartContext = createContext();


export function CartProvider({ children }) {
    const [cartItems, setCartItems] = useState(() => {
        // Retrieve cart data from localStorage on initial load
        const storedCart = localStorage.getItem('cartItems');
        return storedCart ? JSON.parse(storedCart) : []; });

    const [error, setError] = useState(null);

    const { currentUser } = useAuth();

    console.log('Cart items:', cartItems);
    // Persist cartItems to localStorage whenever it changes
    useEffect(() => {
        localStorage.setItem('cartItems', JSON.stringify(cartItems));
    }, [cartItems]);

    const addToCart = async (item) => {
        try {
            const reservationDTO = {
                userId: currentUser.id,
                products: [{
                    productId: item.productId,
                    quantity: item.quantity
                }],
                startDate: item.startDate,
                endDate: item.endDate
            };

            console.log('Sending reservation:', reservationDTO);

            const response = await API.reservations.create(reservationDTO);
            console.log('Response:', response);

            setCartItems((prev) => [
                ...prev,
                {
                    ...item,
                    product: response.data.products[0]?.product || {},
                    quantity: item.quantity,
                    reservationId: response.data.id,
                    status: 'PENDING',
                },]);
            setError(null);
        } catch (error) {
            const errorMessage = error.response?.data?.message || error.message;
            setError(errorMessage);
            throw new Error(errorMessage);
        }
    };

    const clearCart = () => {
        setCartItems([]);
        localStorage.removeItem('cartItems');
    };

    const removeItem = (productId) => {
        setCartItems(prev => prev.filter(item => item.productId !== productId));
    };

    // Fetch the current user's reservations only
    useEffect(() => {
        if (currentUser) {
            const fetchReservations = async () => {
                try {
                    const response = await API.reservations.getMyReservations(); // Fetch only current user's reservations
                    const reservations = response.data;

                    // Filter out only the reservations of the current user and store in the cart
                    setCartItems(reservations.map(reservation => ({
                        ...reservation,
                        product: reservation.products[0].product, // Assuming there's only one product per reservation
                        status: reservation.status || 'PENDING',
                    })));
                } catch (err) {
                    setError('Failed to load reservations.');
                    console.error(err);
                }
            };

            fetchReservations();
        }
    }, [currentUser]);

    // Store cart items in localStorage
    useEffect(() => {
        if (cartItems.length > 0) {
            localStorage.setItem('cartItems', JSON.stringify(cartItems));
        }
    }, [cartItems]);

    //todo source


    return (
        <CartContext.Provider value={{
            cartItems,
            addToCart,
            clearCart,
            removeItem,
            error
        }}>
            {children}
        </CartContext.Provider>
    );

}

export const useCart = () => useContext(CartContext);


//TODO SOURCE AND EXPL