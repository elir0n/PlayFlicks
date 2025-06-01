import React, { useState, useEffect } from 'react';
import styles from '../../styles/home.module.css';

function RandomMovie() {
    const [movie, setMovie] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchRandomMovie = async () => {
            try {
                // Get authentication token
                const token = localStorage.getItem('token');
                const userId = localStorage.getItem('userId');
                
                if (!token) {
                    setError('Authentication required. Please log in.');
                    setLoading(false);
                    return;
                }

                // Fetch random movie
                const response = await fetch('http://localhost:3001/api/movies/random-movie', {
                    method: 'GET',
                    headers: {
                        'Authorization': `Bearer ${token}`,
                        'Content-Type': 'application/json',
                        'id': userId
                    }
                });

                // Handle authentication errors
                if (response.status === 403) {
                    setError('Access denied. Your session may have expired. Please log in again.');
                    setLoading(false);
                    return;
                }

                if (!response.ok) {
                    throw new Error(`Server responded with status: ${response.status}`);
                }

                const data = await response.json();
                console.log("Fetched random movie:", data);
                setMovie(data);
            } catch (err) {
                console.error('Error fetching random movie:', err);
                setError(err.message || 'Failed to fetch random movie');
            } finally {
                setLoading(false);
            }
        };

        fetchRandomMovie();
    }, []);

    if (loading) {
        return <div className={styles.loading}>Loading...</div>;
    }

    if (error) {
        return <div className={styles.error}>Error: {error}</div>;
    }

    if (!movie) {
        return <div className={styles.empty}>No movie found.</div>;
    }

    return (
        <div className={styles.videoContainer}>
            <div className={styles.videoPlayer}>
                <video controls autoPlay>
                    <source 
                        src={movie.video ? `http://localhost:3001${movie.video}` : undefined} 
                        type="video/mp4" 
                    />
                    Your browser does not support the video tag.
                </video>
            </div>
        </div>
    );
}

export default RandomMovie;