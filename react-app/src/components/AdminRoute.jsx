import {Navigate} from "react-router-dom";
import {useEffect, useState} from "react";
import {isAuthenticated} from "../viewmodel/Authenticate";

// Helper function to decode JWT without an external library
function decodeJWT(token) {
  try {
    const parts = token.split('.');
    if (parts.length !== 3) return null;
    const payload = parts[1];
    const decodedPayload = atob(payload.replace(/-/g, '+').replace(/_/g, '/'));
    return JSON.parse(decodedPayload);
  } catch (error) {
    console.error('Error decoding token:', error);
    return null;
  }
}

// AdminRoute component to protect admin routes
function AdminRoute(props) {
    const [authenticated, setAuthenticated] = useState(true);
    const [isAdmin, setIsAdmin] = useState(false);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const checkAdminAuthentication = async () => {
            try {
                const token = localStorage.getItem("token");
                if (!token) {
                    setAuthenticated(false);
                    setLoading(false);
                    return;
                }
                
                // Check if token is valid
                const authenticated = await isAuthenticated(token);
                
                if (!authenticated) {
                    localStorage.removeItem("token");
                    setAuthenticated(false);
                    setLoading(false);
                    return;
                }
                
                // Check if user is admin
                const decodedToken = decodeJWT(token);
                const userIsAdmin = decodedToken && decodedToken.role === 'admin';
                setIsAdmin(userIsAdmin);
                setAuthenticated(true);
                setLoading(false);
            } catch (error) {
                console.error("Authentication check failed:", error);
                localStorage.removeItem("token");
                setAuthenticated(false);
                setLoading(false);
            }
        };

        checkAdminAuthentication();
    }, []);

    if (loading) {
        return <div>Loading...</div>;
    }

    if (!authenticated) {
        return <Navigate to="/login" />;
    } else if (!isAdmin) {
        return <Navigate to="/home" />;
    }

    return props.children;
}

export default AdminRoute;
