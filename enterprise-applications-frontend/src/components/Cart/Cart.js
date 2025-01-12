//todo source plus expl

import React, {useState} from 'react';
import { useCart } from './CartContext';
import {
    Button,
    Container,
    Typography,
    Card,
    CardContent,
    Stack,
    IconButton,
    DialogActions,
    DialogContent, Dialog, DialogTitle, Alert, DialogContentText
} from '@mui/material';
import axios from 'axios';
import DeleteOutlineIcon from '@mui/icons-material/DeleteOutline';
import API from "../../services/api";
import dayjs from "dayjs";
import {ExpandOutlined} from "@mui/icons-material";
import {useAuth} from "../Auth/Authcontext";


function EditIcon() {
    return null;
}

const Cart = () => {
    const { cartItems, removeItem, clearCart } = useCart();
    const [confirmDialogOpen, setConfirmDialogOpen] = useState(false);
    const [checkoutError, setCheckoutError] = useState(null);
    const [selectedItem, setSelectedItem] = useState(null);
    const [showConfirmDialog, setShowConfirmDialog] = useState(false);

    const { currentUser } = useAuth();

    const handleCheckout = async () => {
            if (!currentUser) {
                setCheckoutError('Please login to checkout');
                return;
            }

            if (!cartItems.length) {
                setCheckoutError('Your cart is empty');
                return;
            }
        try {
            // Confirm payment for each reservation in the cart
            for (const item of cartItems) {
                const reservationId = item.reservationId; // Use the reservation ID from the cart
                if (!reservationId) {
                    throw new Error('Missing reservation ID for one or more items in the cart.');
                }
                console.log('Confirming payment for reservation ID:', reservationId);
                await API.reservations.confirmPayment(reservationId); // Confirm payment for the existing reservation
            }
            clearCart();
            console.log('All reservations confirmed successfully.');
        } catch (error) {
            setCheckoutError(error.message);
        }
    };


    const calculateItemTotal = (item) => {
        if (!item?.product?.dailyRentalPrice || !item?.startDate || !item?.endDate || !item?.products?.[0]?.quantity) {
            return 0;
        }
        const days = dayjs(item.endDate).diff(dayjs(item.startDate), 'day') + 1;
        return item.product.dailyRentalPrice * item.quantity * days;
    };



    //


    if (!cartItems || cartItems.length === 0) {
        return (
            <Container>
                <Typography variant="h4" mb={3}>Shopping Cart</Typography>
                <Typography>Your cart is empty</Typography>
            </Container>
        );
    }


    return (
        <Container>
            <Typography variant="h4" mb={3}>Shopping Cart</Typography>

            {checkoutError && (
                <Alert severity="error" sx={{ mb: 2 }} onClose={() => setCheckoutError(null)}>
                    {checkoutError}
                </Alert>
            )}

            {cartItems.map((item, index) => (
                <Card key={index} sx={{ mb: 2 }}>
                    <CardContent>
                        <Stack direction="row" justifyContent="space-between" alignItems="center">
                            <div>
                                <Typography variant="h6">{item.product?.name || 'Unknown Product'}</Typography>
                                <Typography>
                                    Quantity: {item.products?.[0]?.quantity || 0}
                                </Typography>
                                <Typography>
                                    From: {item.startDate || 'Not specified'}
                                </Typography>
                                <Typography>
                                    To: {item.endDate || 'Not specified'}
                                </Typography>
                                <Typography>
                                    Total: €{calculateItemTotal(item).toFixed(2)}
                                </Typography>
                            </div>
                            <IconButton
                                onClick={() => removeItem(item.products[0].productId)}
                                color="error"
                            >
                                <DeleteOutlineIcon />
                            </IconButton>
                        </Stack>
                    </CardContent>
                </Card>
            ))}

            <Button
                variant="contained"
                onClick={handleCheckout}
                disabled={!cartItems.length}
                sx={{ mt: 2 }}
            >
                Proceed to Checkout
            </Button>
        </Container>
    );
};

export default Cart;