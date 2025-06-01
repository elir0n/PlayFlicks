import { BrowserRouter, Routes, Route } from "react-router-dom";
import Login from "./view/Login";
import Home from "./view/Home";
import PrivateRoute from "./components/PrivateRoute";
import AdminRoute from "./components/AdminRoute";
import Register from "./view/Register";
import Unsigned from "./view/Unsigned";
import Admin from "./view/Admin";
import Watch from "./view/Watch";
import MovieDetails from "./view/MovieDetails";
import { useEffect } from "react";
import './styles/darkMode.css'; 

function App() {
    // Set dark mode preference on app
    useEffect(() => {
        // Check initial dark mode preference on app load
        const savedDarkMode = localStorage.getItem('darkMode');
        if (savedDarkMode === 'true') {
            document.body.classList.add('darkMode');
        } else {
            document.body.classList.add('light');
        }
    }, []);

    return (
        <BrowserRouter>
            <Routes>
                <Route path="/" element={<Unsigned />} />
                <Route path="login" element={<Login />} />
                <Route path="register" element={<Register />} />
                <Route path="home" element={<Home />} />
                <Route path="/details/:id" element={<MovieDetails />} /> 
                <Route path="/watch/:id" element={<Watch />} />
                {/* Protected route only for admin users */}
                <Route path="admin" element={
                    <AdminRoute>
                        <Admin />
                    </AdminRoute>
                } />
                
            </Routes>
        </BrowserRouter>
    );
}

export default App;