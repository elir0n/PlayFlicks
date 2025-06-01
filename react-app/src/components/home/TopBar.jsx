import React from 'react';
import Logo from '../unsigned/Logo';
import ActionButtons from './ActionButtons';
import SearchButton from './SearchButton';
import DarkModeButton from './DarkModeButton';  
import styles from '../../styles/home.module.css';

const TopBar = () => {
    return (
        <div className={styles.topBar}>
            <Logo />
            <ActionButtons />
            <SearchButton />
            <DarkModeButton />
        </div>
    );
};

export default TopBar;
