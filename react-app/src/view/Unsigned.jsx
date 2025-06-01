import React from "react";
import Overlay from "../components/unsigned/Overlay";
import styles from "../styles/unsigned.module.css";

function Unsigned() {
    return (<div className={styles.background}>
        <Overlay/>;
    </div>);
}

export default Unsigned;
