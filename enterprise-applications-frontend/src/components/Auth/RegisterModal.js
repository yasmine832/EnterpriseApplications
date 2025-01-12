import { useState } from 'react';
import {
    Modal,
    Box,
    TextField,
    Button,
    Typography,
    CircularProgress,
    Alert
} from '@mui/material';
import API from "../../services/api";

export default function RegisterModal({ open, onClose, onSwitchToLogin }) {
    const [formData, setFormData] = useState({
        username: '',
        email: '',
        password: '',
        confirmPassword: ''
    });
    const [loading, setLoading] = useState(false);
    const [errors, setErrors] = useState({});

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setErrors({});

        try {
            if (formData.password !== formData.confirmPassword) {
                setErrors({ confirmPassword: 'Passwords do not match' });
                return;
            }

            await API.auth.register({
                username: formData.username,
                email: formData.email,
                password: formData.password
            });

            onSwitchToLogin();
        } catch (err) {
            // Handle Spring Boot validation errors
            if (err.response?.data?.errors) {
                const backendErrors = {};
                err.response.data.errors.forEach(error => {
                    // Convert Spring Boot validation field names to your form fields
                    const field = error.field.toLowerCase();
                    backendErrors[field] = error.defaultMessage;
                });
                setErrors(backendErrors);
            } else if (err.response?.data?.message) {
                // Handle custom backend error messages
                if (err.response.data.message.includes('username')) {
                    setErrors({ username: err.response.data.message });
                } else if (err.response.data.message.includes('email')) {
                    setErrors({ email: err.response.data.message });
                } else {
                    setErrors({ submit: err.response.data.message });
                }
            } else {
                setErrors({ submit: 'Registration failed. Please try again.' });
            }
        } finally {
            setLoading(false);
        }
    };

    const handleChange = (field) => (e) => {
        setFormData(prev => ({
            ...prev,
            [field]: e.target.value
        }));
        // Only clear backend validation errors when user types
        if (errors[field]) {
            setErrors(prev => ({ ...prev, [field]: undefined }));
        }
    };

    return (
        <Modal open={open} onClose={() => !loading && onClose()}>
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
                    Create Account
                </Typography>

                {errors.submit && (
                    <Alert severity="error" sx={{ mb: 2 }}>
                        {errors.submit}
                    </Alert>
                )}

                <form onSubmit={handleSubmit}>
                    <TextField
                        fullWidth
                        label="Username"
                        value={formData.username}
                        onChange={handleChange('username')}
                        error={!!errors.username}
                        helperText={errors.username}
                        margin="normal"
                        required
                    />
                    <TextField
                        fullWidth
                        type="email"
                        label="Email"
                        value={formData.email}
                        onChange={handleChange('email')}
                        error={!!errors.email}
                        helperText={errors.email}
                        margin="normal"
                        required
                    />
                    <TextField
                        fullWidth
                        type="password"
                        label="Password"
                        value={formData.password}
                        onChange={handleChange('password')}
                        error={!!errors.password}
                        helperText={errors.password}
                        margin="normal"
                        required
                    />
                    <TextField
                        fullWidth
                        type="password"
                        label="Confirm Password"
                        value={formData.confirmPassword}
                        onChange={handleChange('confirmPassword')}
                        error={!!errors.confirmPassword}
                        helperText={errors.confirmPassword}
                        margin="normal"
                        required
                    />
                    <Button
                        type="submit"
                        fullWidth
                        variant="contained"
                        disabled={loading}
                        sx={{ mt: 2 }}
                    >
                        {loading ? <CircularProgress size={24} /> : 'Register'}
                    </Button>
                </form>

                <Button
                    onClick={onSwitchToLogin}
                    disabled={loading}
                    sx={{ mt: 2 }}
                >
                    Already have an account? Login
                </Button>
            </Box>
        </Modal>
    );
}