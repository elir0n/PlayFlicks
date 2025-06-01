import React from 'react';
import { useParams, useLocation } from 'react-router-dom';
import styles from '../../styles/watch.module.css';

const VideoPlayer = () => {
  const { id } = useParams();
  const location = useLocation();
  
  // Get movie data from location state or fetch it if not available
  const [movie, setMovie] = React.useState(location.state?.movie || null);
  const [loading, setLoading] = React.useState(!movie);
  const [error, setError] = React.useState(null);

  React.useEffect(() => {
    // If we don't have the movie data from navigation state, fetch it
    if (!movie) {
      const fetchMovieDetails = async () => {
        try {
          const token = localStorage.getItem('token');
          const userId = localStorage.getItem('userId');
          
          if (!token) {
            setError('Authentication required. Please log in.');
            setLoading(false);
            return;
          }

          const response = await fetch(`http://localhost:3001/api/movies/${id}`, {
            method: 'GET',
            headers: { 
              'Authorization': `Bearer ${token}`,
              'id': userId
            }
          });

          if (!response.ok) {
            throw new Error(`Server responded with status: ${response.status}`);
          }

          const data = await response.json();
          setMovie(data);
        } catch (err) {
          console.error("Error fetching movie details:", err);
          setError(err.message || 'Failed to fetch movie details');
        } finally {
          setLoading(false);
        }
      };

      fetchMovieDetails();
    }
  }, [id, movie]);

  if (loading) return <div className={styles.loading}>Loading movie...</div>;
  if (error) return <div className={styles.error}>Error: {error}</div>;
  if (!movie) return <div className={styles.empty}>Movie not found</div>;

  // Assume the movie object has a 'videoUrl' property
  const videoUrl = `http://localhost:3001${movie.video}`;

  return (
    <>
      <h1 className={styles.movieTitle}>{movie.title}</h1>
      <div className={styles.videoContainer}>
        <video 
          controls 
          autoPlay
          className={styles.videoPlayer}
          src={videoUrl}
        >
          Your browser does not support the video tag.
        </video>
      </div>
      
      <div className={styles.infoSection}>
        <div className={styles.movieDescription}>
          <h3>Description</h3>
          <p>{movie.description || "No description available."}</p>
        </div>
      </div>
    </>
  );
};

export default VideoPlayer;