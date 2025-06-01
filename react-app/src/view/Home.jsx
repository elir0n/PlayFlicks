import React from 'react';
import TopBar from '../components/home/TopBar'; 
import VideoPlayer from '../components/home/VideoPlayer'; 
import MoviesByCategory from '../components/home/MoviesByCategories';
import styles from '../styles/home.module.css';

const Home = () => {
    return (
        <>
            <TopBar />
            <div className={styles.contentContainer}>
                <VideoPlayer />
            </div>
            <MoviesByCategory />
        </>
    );
};

export default Home;
