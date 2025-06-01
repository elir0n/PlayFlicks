import React from "react";
import {Link} from "react-router-dom";
import styles from '../../styles/unsigned.module.css';

function ActionButtons() {
    return (
        <>
            <div className={styles.signup}>
                <Link to="/register">Sign Up</Link>
            </div>
            <div className={styles.login}>
                <Link to="/login">Login</Link>
            </div>
        </>
    );
}

export default ActionButtons;
