import { Navigate } from 'react-router-dom';
import { useAuth } from './Authcontext';

const ProtectedRoute = ({ children, roles = [] }) => {
    const { currentUser } = useAuth();

    if (!currentUser) {
        return <Navigate to="/login" replace />;
    }


    return children;
};
export default ProtectedRoute;