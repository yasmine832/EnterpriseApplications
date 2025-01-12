import { useState } from 'react';
import {
    Modal,
    Box,
    TextField,
    Button,
    Typography,
    CircularProgress,
    Alert,
    FormControlLabel,
    Checkbox
} from '@mui/material';
import API from "../../services/api";
import axios from "axios";
import {useAuth} from "./Authcontext";


export default function LoginModal({ open, onClose, onSwitchToRegister }) {
    const [credentials, setCredentials] = useState({
        username: '',
        password: '',
        rememberMe: false
    });

    const { login, loading, error, currentUser } = useAuth();
    const [formError, setFormError] = useState(null);


    const handleSubmit = async (e) => {
        e.preventDefault();
        setFormError(null);

        try {
            // Call login from context (this will update currentUser and session)
            await login(credentials);

            // Check if the user is successfully logged in
            if (currentUser) {
                onClose();  // Close the modal if logged in successfully
            } else {
                setFormError('Login failed. Please try again.');
            }
        } catch (err) {
            setFormError(err.message || 'An error occurred.');
        }
    };//todo link chatgpt

    return (
        <Modal
            open={open}
            onClose={() => !loading && onClose()}
        >
            <Box sx={{
                position: 'absolute',
                top: '50%',
                left: '50%',
                transform: 'translate(-50%, -50%)',
                width: 400,
                bgcolor: 'background.paper',
                boxShadow: 24,
                p: 4,
                borderRadius: 1
            }}>
                <Typography variant="h6" gutterBottom>
                    Login
                </Typography>

                {error && (
                    <Alert severity="error" sx={{ mb: 2 }}>
                        {error}
                    </Alert>
                )}

                <form onSubmit={handleSubmit}>
                    <TextField
                        fullWidth
                        label="Username"
                        value={credentials.username}
                        onChange={(e) => setCredentials(prev => ({
                            ...prev,
                            username: e.target.value
                        }))}
                        margin="normal"
                        required
                        autoFocus
                    />
                    <TextField
                        fullWidth
                        type="password"
                        label="Password"
                        value={credentials.password}
                        onChange={(e) => setCredentials(prev => ({
                            ...prev,
                            password: e.target.value
                        }))}
                        margin="normal"
                        required
                    />
                    <FormControlLabel
                        control={
                            <Checkbox
                                checked={credentials.rememberMe}
                                onChange={(e) => setCredentials(prev => ({
                                    ...prev,
                                    rememberMe: e.target.checked
                                }))}
                                color="primary"
                            />
                        }
                        label="Remember me"
                    />
                    <Button
                        type="submit"
                        fullWidth
                        variant="contained"
                        disabled={loading}
                        sx={{ mt: 2 }}
                    >
                        {loading ? <CircularProgress size={24} /> : 'Login'}
                    </Button>
                </form>

                <Button
                    onClick={onSwitchToRegister}
                    disabled={loading}
                    sx={{ mt: 2 }}
                >
                    Need an account? Register
                </Button>
            </Box>
        </Modal>
    );
}
