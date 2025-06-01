import React from 'react';
import { Link } from 'react-router-dom';
import styles from '../../styles/home.module.css';

function ActionButtons() {
    // Handle logout function
    const handleLogout = () => {
        localStorage.removeItem('token'); // Remove the token from localStorage
    };

    return (
        <>
            <div className={styles.logoutButton}>
                <Link to="/" onClick={handleLogout}>Log Out</Link>
            </div>
            <div className={styles.adminButton}>
                <Link to="/admin">Admin Mode</Link>
            </div>
        </>
    );
}

export default ActionButtons;
