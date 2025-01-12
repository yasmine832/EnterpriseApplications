import React, { useState } from 'react';
import { AppBar, Toolbar, Button, Typography, IconButton } from '@mui/material';
import { Link } from 'react-router-dom';
import ShoppingCartIcon from '@mui/icons-material/ShoppingCart';
import {useAuth} from "./Auth/Authcontext";
import RegisterModal from "./Auth/RegisterModal";
import LoginModal from "./Auth/LoginModal";


const Navigation = () => {
    const { currentUser, logout } = useAuth();
    const [loginOpen, setLoginOpen] = useState(false);
    const [registerOpen, setRegisterOpen] = useState(false);

    return (
        <>
            <AppBar position="static">
                <Toolbar>
                    <Typography variant="h6" component={Link} to="/" sx={{ flexGrow: 1, textDecoration: 'none', color: 'inherit' }}>
                        Rental Shop
                    </Typography>

                    {currentUser ? (
                        <>
                            <IconButton component={Link} to="/cart" color="inherit">
                                <ShoppingCartIcon />
                            </IconButton>
                            <Button color="inherit" onClick={logout}>
                                Logout
                            </Button>
                        </>
                    ) : (
                        <>
                            <Button color="inherit" onClick={() => setLoginOpen(true)}>
                                Login
                            </Button>
                            <Button color="inherit" onClick={() => setRegisterOpen(true)}>
                                Register
                            </Button>
                        </>
                    )}
                </Toolbar>
            </AppBar>

            <LoginModal
                open={loginOpen}
                onClose={() => setLoginOpen(false)}
                onSwitchToRegister={() => {
                    setLoginOpen(false);
                    setRegisterOpen(true);
                }}
            />

            <RegisterModal
                open={registerOpen}
                onClose={() => setRegisterOpen(false)}
                onSwitchToLogin={() => {
                    setRegisterOpen(false);
                    setLoginOpen(true);
                }}
            />
        </>
    );
};

export default Navigation;