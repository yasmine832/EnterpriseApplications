import React, { useState, useEffect } from 'react';//useState and useEffect are React hooks for managing state and side effects
import {
    Container,
    Grid,
    Card,
    CardContent,
    Typography,
    Select,
    MenuItem,
    FormControl,
    InputLabel, CircularProgress,
    OutlinedInput, Checkbox, ListItemText, TextField, Box, Stack, IconButton, Modal, Button, Alert
} from '@mui/material';
import axios from 'axios';//for making requests to APIs
import {DatePicker, LocalizationProvider} from "@mui/x-date-pickers";
import {AdapterDayjs} from "@mui/x-date-pickers/AdapterDayjs";
import 'dayjs/plugin/weekOfYear';
import 'dayjs/plugin/customParseFormat';
import 'dayjs/plugin/localizedFormat';
import 'dayjs/plugin/isBetween';//todo
import AddShoppingCartIcon from '@mui/icons-material/AddShoppingCart';
import dayjs from "dayjs";
import {useCart} from "../Cart/CartContext";
import api from "../../services/api";
import API from "../../services/api";
import isSameOrBefore from "dayjs/plugin/isSameOrBefore";
import isBetween from "dayjs/plugin/isBetween";


dayjs.extend(isSameOrBefore);
dayjs.extend(isBetween); //todoo

//todo split in productcard and productmodal



const ProductList = () => {
    const [products, setProducts] = useState([]); //stores the lsit of products
    const [selectedCategories, setSelectedCategories] = useState([]);
    const [startDate, setStartDate] = useState(null);
    const [endDate, setEndDate] = useState(null);
    const [searchQuery, setSearchQuery] = useState('');
    const [error, setError] = useState(null);//stores any error messages




    const [debouncedSearchQuery, setDebouncedSearchQuery] = useState(''); //stores the debounced search query to prevent too many requests


    const categories = ['CABLES', 'LIGHTING', 'CONTROL_PANELS']; //Todo


    //useEffect(() => { //runs whenever category change
    //    fetchProducts();//triggers fetch again to get the new list of products
    //}, [selectedCategories, searchQuery, startDate, endDate])


    const [selectedProduct, setSelectedProduct] = useState(null);
    const [modalOpen, setModalOpen] = useState(false);
    const [quantity, setQuantity] = useState(1);
    const [rentalStart, setRentalStart] = useState(null);
    const [rentalEnd, setRentalEnd] = useState(null);
    const [modalError, setModalError] = useState(null);


    useEffect(() => {
        fetchProducts()
    }, [selectedCategories, debouncedSearchQuery, startDate, endDate]);


    const handleSearchKeyPress = (e) => {//user input
        if (e.key === 'Enter') {
            setDebouncedSearchQuery(searchQuery); //only search on enter
        }
    }


    const fetchProducts = async () => {
        try {
            let response;
            if (debouncedSearchQuery) {
                response = await API.products.search(debouncedSearchQuery);
            } else {
                response = await API.products.getAll({
                    selectedCategories,
                    startDate,
                    endDate
                });
            }
            setProducts(response.data);
            setError(null);
        } catch (error) {
            setError(`Failed to fetch products: ${error.message}`);
        }
    };


    const { addToCart } = useCart();


    const handleAddToCart = async () => {
        if (!rentalStart || !rentalEnd || quantity < 1) {
            setModalError('Please fill in all fields');
            return;
        }

        const cartItem = {

            productId: selectedProduct.id,
            quantity: quantity,
            startDate: rentalStart.format('YYYY-MM-DD'),
            endDate: rentalEnd.format('YYYY-MM-DD'),
            product: selectedProduct
        };

        try {
            await addToCart(cartItem);
            setModalOpen(false);
            setSelectedProduct(null);
            setQuantity(1);
            setRentalStart(null);
            setRentalEnd(null);
            setModalError(null);
        } catch (error) {
            setModalError(error.message);
        }
    };

    const [availabilityData, setAvailabilityData] = useState({});

    const fetchAvailabilityForDateRange = async (start, end) => {
        if (!selectedProduct) return;

        try {
            const response = await API.products.getAvailability(
                selectedProduct.id,
                start.format('YYYY-MM-DD'),
                end.format('YYYY-MM-DD')
            );
            setAvailabilityData(response.data);
        } catch (error) {
            console.error('Error fetching availability:', error);
        }
    };

    const [disabledDates, setDisabledDates] = useState([]);
    const [isLoadingDates, setIsLoadingDates] = useState(false);

    const checkAvailability = async (productId, newQuantity) => {
        setIsLoadingDates(true);
        try {
            const startCheck = dayjs();
            const endCheck = dayjs().add(3, 'months');

            const response = await API.products.getAll({
                startDate: startCheck.format('YYYY-MM-DD'),
                endDate: endCheck.format('YYYY-MM-DD')
            });

            // Get all reservations for this product
            const reservations = response.data
                .filter(p => p.id === productId)
                .flatMap(p => p.reservations || []);

            // Create array of dates that should be disabled
            const unavailableDates = [];
            let currentDate = startCheck;

            while (currentDate.isBefore(endCheck) || currentDate.isSame(endCheck)) {
                const reservedQuantity = reservations
                    .filter(res =>
                        currentDate.isBetween(
                            dayjs(res.startDate),
                            dayjs(res.endDate),
                            'day',
                            '[]'
                        )
                    )
                    .reduce((total, res) => total + res.quantity, 0);

                if ((selectedProduct.stock - reservedQuantity) < newQuantity) {
                    unavailableDates.push(currentDate.format('YYYY-MM-DD'));
                }
                currentDate = currentDate.add(1, 'day');
            }

            setDisabledDates(unavailableDates);
        } catch (error) {
            console.error('Error checking availability:', error);
            setModalError('Error checking availability');
        } finally {
            setIsLoadingDates(false);
        }
    };

    // Effect to check availability when quantity changes
    useEffect(() => {
        if (selectedProduct && quantity > 0) {
            checkAvailability(selectedProduct.id, quantity);
        }
    }, [selectedProduct, quantity]);



//todo source
    const modalStyle = {
        position: 'absolute',
        top: '50%',
        left: '50%',
        transform: 'translate(-50%, -50%)',
        width: 400,
        bgcolor: 'background.paper',
        boxShadow: 24,
        p: 4,
        borderRadius: 2
    };




    //todo source convo
    return (
        <Container>
            {error && <Alert severity="error" sx={{mb: 2}}>{error}</Alert>}

            <Box sx={{display: 'flex', gap: 2, mb: 3}}>
                <FormControl fullWidth>
                    <InputLabel>Categories</InputLabel>
                    <Select
                        multiple
                        value={selectedCategories}
                        onChange={(e) => setSelectedCategories(e.target.value)} // Update selected categories
                        input={<OutlinedInput label="Categories" />}
                        renderValue={(selected) => selected.join(', ')} // Show selected categories as a comma-separated string
                    >
                        {categories.map((category) => (
                            <MenuItem key={category} value={category}>
                                <Checkbox checked={selectedCategories.includes(category)} />
                                <ListItemText primary={category} />
                            </MenuItem>
                        ))}
                    </Select>
                </FormControl>

                <TextField
                    fullWidth
                    label="Search products"
                    value={searchQuery}
                    onChange={(e) => setSearchQuery(e.target.value)}
                    onKeyPress={handleSearchKeyPress}
                />
            </Box>

            <LocalizationProvider dateAdapter={AdapterDayjs}>
                <Box sx={{display: 'flex', gap: 2, mb: 3}}>
                    <DatePicker
                        label="Start Date"
                        value={startDate}
                        onChange={setStartDate}
                        slotProps={{textField: {fullWidth: true}}}
                    />
                    <DatePicker
                        label="End Date"
                        value={endDate}
                        onChange={setEndDate}
                        slotProps={{textField: {fullWidth: true}}}
                    />
                </Box>
            </LocalizationProvider>

            <Grid container spacing={3}>
                {products.length > 0 ? (
                    products.map((product) => (
                        <Grid item xs={12} sm={6} md={4} key={product.id}>
                            <Card>
                                <CardContent>
                                    <Stack direction="row" justifyContent="space-between" alignItems="center">
                                        <Typography variant="h6">{product.name}</Typography>
                                        <IconButton
                                            color="primary"
                                            onClick={() => {
                                                setSelectedProduct(product);
                                                setModalOpen(true);
                                            }}
                                        >
                                            <AddShoppingCartIcon />
                                        </IconButton>
                                    </Stack>
                                    <Typography variant="body1">
                                        €{product.dailyRentalPrice}/day
                                    </Typography>
                                </CardContent>
                            </Card>
                        </Grid>
                    ))
                ) : (
                    <Grid item xs={12}>
                        <Typography variant="body1" align="center">
                            No products found
                        </Typography>
                    </Grid>
                )}
            </Grid>

            <LocalizationProvider dateAdapter={AdapterDayjs}>
                <Modal open={modalOpen} onClose={() => setModalOpen(false)}>
                    <Box sx={modalStyle}>
                        {selectedProduct && (
                            <>
                                <Typography variant="h6" component="h2" mb={2}>
                                    {selectedProduct.name}
                                </Typography>
                                <Typography variant="body1" mb={2}>
                                    {selectedProduct.description}
                                </Typography>
                                <Typography variant="body2" mb={2}>
                                    Maximum Available: {selectedProduct.stock}
                                </Typography>
                                {modalError && (
                                    <Alert severity="error" sx={{ mb: 2 }}>
                                        {modalError}
                                    </Alert>
                                )}
                                <Stack spacing={2}>
                                    <TextField
                                        type="number"
                                        label="Quantity"
                                        value={quantity}
                                        onChange={(e) => {
                                            const newQuantity = Math.max(1, Math.min(parseInt(e.target.value) || 1, selectedProduct.stock));
                                            setQuantity(newQuantity);
                                            setRentalStart(null);
                                            setRentalEnd(null);
                                            setModalError(null);
                                        }}
                                        inputProps={{
                                            min: 1,
                                            max: selectedProduct.stock
                                        }}
                                    />

                                    {isLoadingDates ? (
                                        <CircularProgress />
                                    ) : (
                                        <>
                                            <DatePicker
                                                label="Rental Start"
                                                value={rentalStart}
                                                onChange={(date) => {
                                                    setRentalStart(date);
                                                    setRentalEnd(null);
                                                    setModalError(null);
                                                }}
                                                minDate={dayjs()}
                                                shouldDisableDate={(date) =>
                                                    disabledDates.includes(date.format('YYYY-MM-DD'))
                                                }
                                            />

                                            <DatePicker
                                                label="Rental End"
                                                value={rentalEnd}
                                                onChange={(date) => {
                                                    setRentalEnd(date);
                                                    setModalError(null);
                                                }}
                                                minDate={rentalStart || dayjs()}
                                                disabled={!rentalStart}
                                                shouldDisableDate={(date) => {
                                                    if (!rentalStart) return true;

                                                    // Check each day in the range
                                                    let currentDate = dayjs(rentalStart);
                                                    while (currentDate.isSameOrBefore(date, 'day')) {
                                                        if (disabledDates.includes(currentDate.format('YYYY-MM-DD'))) {
                                                            return true;
                                                        }
                                                        currentDate = currentDate.add(1, 'day');
                                                    }
                                                    return false;
                                                }}
                                            />
                                        </>
                                    )}

                                    {quantity > 0 && rentalStart && rentalEnd && (
                                        <Typography variant="body2" color="text.secondary">
                                            Total Price: €{(
                                            selectedProduct.dailyRentalPrice *
                                            quantity *
                                            (dayjs(rentalEnd).diff(dayjs(rentalStart), 'day') + 1)
                                        ).toFixed(2)}
                                        </Typography>
                                    )}

                                    <Button
                                        variant="contained"
                                        onClick={handleAddToCart}
                                        disabled={!quantity || !rentalStart || !rentalEnd || isLoadingDates}
                                    >
                                        Add to Cart
                                    </Button>
                                </Stack>
                            </>
                        )}
                    </Box>
                </Modal>            </LocalizationProvider>
        </Container>
    );
}
export default ProductList;