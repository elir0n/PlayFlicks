import React from 'react';
import TopBar from "../components/home/TopBar";
import WatchMovie from "../components/watch/VideoPlayer";
import styles from "../styles/watch.module.css";

const Watch = () => {
  return (
    <div className={styles.watchPage}>
      <TopBar />
      <div className={styles.contentContainer}>
        <div className={styles.videoSection}>
          <WatchMovie />
        </div>
      </div>
    </div>
  );
};

export default Watch;