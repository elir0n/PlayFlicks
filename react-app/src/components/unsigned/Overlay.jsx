import React from "react";
import Logo from "./Logo";
import TextView from "./TextView";
import ActionButtons from "./ActionButtons";
import styles from '../../styles/unsigned.module.css';

function Overlay() {
  return (
    <div className={styles.overlay}>
      <Logo />
      <TextView />
      <ActionButtons />
    </div>
  );
}

export default Overlay;
