// SearchButton.jsx
import React, { useState, useRef } from 'react';
import styles from '../../styles/home.module.css';

const SearchButton = () => {
    const [searchOpen, setSearchOpen] = useState(false);
    const searchInputRef = useRef(null);

    // Toggle search bar
    const toggleSearch = () => {
        setSearchOpen(!searchOpen);
    };

    // Handle key up event
    const handleKeyUp = () => {
        if (searchInputRef.current && window.filterMovies) {
            window.filterMovies(searchInputRef.current.value);
        }
    };

    return (
        <div className={styles.searchContainer}>
            <button className={styles.searchButton} onClick={toggleSearch}>
                <img src="https://upload.wikimedia.org/wikipedia/commons/thumb/5/55/Magnifying_glass_icon.svg/640px-Magnifying_glass_icon.svg.png" alt="Search" />
            </button>
            {searchOpen && (
                <input 
                    type="text" 
                    className={styles.searchBar} 
                    placeholder="Search..." 
                    ref={searchInputRef}
                    onKeyUp={handleKeyUp}
                    onChange={handleKeyUp}
                />
            )}
        </div>
    );
};

export default SearchButton;