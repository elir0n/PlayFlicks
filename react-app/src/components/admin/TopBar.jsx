import React from 'react';
import Logo from '../unsigned/Logo';
import ActionButtons from './ActionButtons';
import SearchButton from '../home/SearchButton';
import DarkModeButton from '../home/DarkModeButton';
import styles from '../../styles/admin.module.css';

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
