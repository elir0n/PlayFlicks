import React, { useState, useEffect } from 'react';
import styles from '../../styles/home.module.css';

const DarkModeButton = () => {
    const [darkMode, setDarkMode] = useState(() => {
        // Check local storage for initial dark mode state
        const savedDarkMode = localStorage.getItem('darkMode');
        return savedDarkMode === 'true';
    });

    useEffect(() => {
        // Apply dark mode class to body
        if (darkMode) {
            document.body.classList.add('darkMode');
            document.body.classList.remove('light');
        } else {
            document.body.classList.add('light');
            document.body.classList.remove('darkMode');
        }
        
        // Save dark mode preference to local storage
        localStorage.setItem('darkMode', darkMode.toString());
    }, [darkMode]);

    const toggleDarkMode = () => {
        setDarkMode(!darkMode);
    };

    return (
        <button className={styles.darkModeButton} onClick={toggleDarkMode}>
            <img src="https://pixsector.com/cache/afa23d3a/av1c12f667576e96088e6.png" alt="Dark Mode Toggle" />
        </button>
    );
};

export default DarkModeButton;