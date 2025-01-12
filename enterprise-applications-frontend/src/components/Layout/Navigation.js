import { Link } from 'react-router-dom';

import React, { useState } from 'react';
import { AppBar, Toolbar, Button, Typography, IconButton } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../Auth/Authcontext';
import LoginModal from '../Auth/LoginModal';
import RegisterModal from '../Auth/RegisterModal';
import ShoppingCartIcon from '@mui/icons-material/ShoppingCart';

const Navigation = () => {
    const { currentUser, logout } = useAuth();
    const navigate = useNavigate();
    const [showLogin, setShowLogin] = useState(false);
    const [showRegister, setShowRegister] = useState(false);

    const handleLogout = () => {
        logout();
        navigate('/');
    };

    return (
        <>
            <AppBar position="static">
                <Toolbar>
                    <Typography variant="h6" component={Link} to="/" sx={{ flexGrow: 1, textDecoration: 'none', color: 'inherit' }}>
                        Rental App
                    </Typography>
                    <IconButton
                        color="inherit"
                        component={Link}
                        to="/cart"
                        sx={{ mr: 2 }}
                    >
                        <ShoppingCartIcon />
                    </IconButton>
                    {currentUser ? (
                        <Button color="inherit" onClick={handleLogout}>
                            Logout
                        </Button>
                    ) : (
                        <>
                            <Button color="inherit" onClick={() => setShowLogin(true)}>
                                Login
                            </Button>
                            <Button color="inherit" onClick={() => setShowRegister(true)}>
                                Register
                            </Button>
                        </>
                    )}
                </Toolbar>
            </AppBar>

            <LoginModal
                open={showLogin}
                onClose={() => setShowLogin(false)}
                onSwitchToRegister={() => {
                    setShowLogin(false);
                    setShowRegister(true);
                }}
            />
            <RegisterModal
                open={showRegister}
                onClose={() => setShowRegister(false)}
                onSwitchToLogin={() => {
                    setShowRegister(false);
                    setShowLogin(true);
                }}
            />
        </>
    );
};

export default Navigation;