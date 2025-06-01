import React, { useState, useEffect } from 'react';
import { useParams, useLocation, useNavigate } from 'react-router-dom';
import TopBar from "../home/TopBar";
import MovieItem from "../home/MovieItem";
import styles from '../../styles/movieDetails.module.css';

const MovieDetails = () => {
  const { id } = useParams();
  const location = useLocation();
  const navigate = useNavigate();
  
  // Get movie data from location state or fetch it
  const [movie, setMovie] = useState(location.state?.movie || null);
  const [recommendedMovies, setRecommendedMovies] = useState([]);
  const [loading, setLoading] = useState(!movie);
  const [loadingRecommended, setLoadingRecommended] = useState(true);
  const [error, setError] = useState(null);

  // Fetch movie details if not provided in location state
  useEffect(() => {
    const fetchMovieDetails = async () => {
      if (!movie) {
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
          setLoading(false);
        } catch (err) {
          console.error("Error fetching movie details:", err);
          setError(err.message || 'Failed to fetch movie details');
          setLoading(false);
        }
      } else {
        setLoading(false);
      }
    };

    fetchMovieDetails();
  }, [id, movie]);

  // Fetch recommended movies based on current movie
  useEffect(() => {
    const fetchRecommendedMovies = async () => {
      if (!movie) {
        setLoadingRecommended(false);
        return;
      }
      
      try {
        const token = localStorage.getItem('token');
        const userId = localStorage.getItem('userId');
        
        if (!token) {
          setLoadingRecommended(false);
          return;
        }

        const recommendUrl = `http://localhost:3001/api/movies/${id}/recommend`;
        
        const response = await fetch(recommendUrl, {
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
        
        // Handle different response formats
        try {
          let moviesData = [];
          
          // If it's already an array, use it
          if (Array.isArray(data)) {
            moviesData = data;
          }
          // If it's a string, try to parse it as JSON
          else if (typeof data === 'string') {
            if (data.trim()) {
              // Only try to parse if there's actual content
              try {
                const parsed = JSON.parse(data);
                moviesData = Array.isArray(parsed) ? parsed : [];
              } catch (e) {
                // String is not valid JSON, treat as empty array
              }
            }
          }
          
          if (moviesData.length > 0) {
            setRecommendedMovies(moviesData);
          } else {
            setRecommendedMovies([]);
          }
        } catch (parseError) {
          console.error("Error parsing recommendation data:", parseError);
          setRecommendedMovies([]);
        }
      } catch (err) {
        console.error("Error fetching recommended movies:", err);
        // Initialize as empty array on error
        setRecommendedMovies([]);
      } finally {
        setLoadingRecommended(false);
      }
    };

    fetchRecommendedMovies();
  }, [id, movie]);

  // Handle Watch Now button click
  const handleWatchClick = async () => {
    try {
      const token = localStorage.getItem('token');
      
      if (!token) {
        setError('Authentication required. Please log in.');
        return;
      }

      // Make sure we have the actual movie ID
      // Your backend expects this to be a number that can be converted with Number()
      const movieId = parseInt(movie?.id || id, 10);
      
      if (isNaN(movieId)) {
        console.error('Invalid movie ID:', movie?.id || id);
        // Still navigate to the watch page
        navigate(`/watch/${movie?.id || id}`, { state: { movie } });
        return;
      }

      try {
        // Add movie to watched list
        const response = await fetch(`http://localhost:3001/api/users/${movieId}/addMovie`, {
          method: 'POST',
          headers: { 
            'Authorization': `Bearer ${token}`
          }
          // No body needed as movieId is in the URL
        });

        if (!response.ok) {
          console.error(`Failed to add movie to watched list: ${response.status}`);
          try {
            const errorData = await response.text();
            console.error('Error details:', errorData);
          } catch (e) {
            console.error('Could not parse error response');
          }
        }
      } catch (err) {
        console.error("API error:", err);
      }

      // Navigate to watch page regardless of the outcome
      navigate(`/watch/${movieId}`, { state: { movie } });
    } catch (err) {
      console.error("Error in watch click handler:", err);
      // Still navigate to watch page
      navigate(`/watch/${movie?.id || id}`, { state: { movie } });
    }
  };

  if (loading) return <div className={styles.loading}>Loading movie details...</div>;
  if (error) return <div className={styles.error}>Error: {error}</div>;
  if (!movie) return <div className={styles.empty}>Movie not found</div>;

  return (
    <div className={styles.detailsPage}>
      <TopBar />
      <div className={styles.contentContainer}>
        <div className={styles.heroSection}>
          <div className={styles.backdropImage} style={{ backgroundImage: `url(http://localhost:3001${movie.image || ''})` }}>
            <div className={styles.backdropOverlay}></div>
          </div>
          <div className={styles.heroContent}>
            <div className={styles.moviePoster}>
              <img src={`http://localhost:3001${movie.image || ''}`} alt={movie.title || 'Movie poster'} />
            </div>
            <div className={styles.movieInfo}>
              <h1 className={styles.movieTitle}>{movie.title || 'Untitled Movie'}</h1>
              {movie.year && <p className={styles.movieYear}>{movie.year}</p>}
              {movie.genre && <p className={styles.movieGenre}>{movie.genre}</p>}
              <button className={styles.watchButton} onClick={handleWatchClick}>
                Watch Now
              </button>
            </div>
          </div>
        </div>

        <div className={styles.detailsSection}>
          <div className={styles.descriptionSection}>
            <h2>About this Movie</h2>
            <p>{movie.description || "No description available."}</p>
          </div>

          <div className={styles.recommendedSection}>
            <h2>Recommended Movies</h2>
            {loadingRecommended ? (
              <p>Loading recommendations...</p>
            ) : recommendedMovies && recommendedMovies.length > 0 ? (
              <div className={styles.recommendedGrid}>
                {recommendedMovies.map((recMovie, index) => (
                  <MovieItem key={recMovie.id || index} movie={recMovie} />
                ))}
              </div>
            ) : (
              <p>No recommendations available.</p>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default MovieDetails;