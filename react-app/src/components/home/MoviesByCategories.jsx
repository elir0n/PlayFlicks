import React, { useState, useEffect } from 'react';
import styles from '../../styles/home.module.css';
import MovieItem from './MovieItem';

const MoviesByCategory = () => {
  const [categoryMovies, setCategoryMovies] = useState({});
  const [allMovies, setAllMovies] = useState({});
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Fetch movies from API and organize by category
  useEffect(() => {
    const fetchMovies = async () => {
      try {
        // Get authentication token
        const token = localStorage.getItem('token');
        const userId = localStorage.getItem('userId');
        
        if (!token) {
          setError('Authentication required. Please log in.');
          setLoading(false);
          return;
        }

        // Using the correct endpoint from your routes configuration
        const response = await fetch('http://localhost:3001/api/movies', {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`,
            'id': userId
          }
        });
        
        // Handle different error scenarios
        if (response.status === 403) {
          setError('Access denied. Your session may have expired. Please log in again.');
          setLoading(false);
          return;
        }

        if (!response.ok) {
          throw new Error(`Server responded with status: ${response.status}`);
        }

        // Parse the response data
        const text = await response.text();
        
        let data;
        try {
          // Try to parse the response as JSON
          data = JSON.parse(text);
        } catch (parseError) {
          console.error("Error parsing JSON:", parseError);
          throw new Error("Invalid response format from server");
        }

        // Set the movie data
        setCategoryMovies(data);
        setAllMovies(data);
      } catch (err) {
        console.error("Error fetching movies:", err);
        setError(err.message || 'Failed to fetch movies');
      } finally {
        setLoading(false);
      }
    };

    fetchMovies();
  }, []);

  // Set up global movie filtering functionality
  useEffect(() => {
    // Create filter function that will be accessible globally
    window.filterMovies = (searchTerm) => {
      if (!searchTerm.trim()) {
        // Reset to original movies if search term is empty
        setCategoryMovies(allMovies);
        return;
      }
      
      const lowercasedTerm = searchTerm.toLowerCase();
      
      // Filter the categories and their movies
      const filtered = {};
      
      // Loop through all categories in the original data
      Object.entries(allMovies).forEach(([category, movies]) => {
        // Filter movies in this category
        const filteredMovies = movies.filter(movie => 
          movie.title.toLowerCase().includes(lowercasedTerm)
        );
        
        // Only add the category if it has matching movies
        if (filteredMovies.length > 0) {
          filtered[category] = filteredMovies;
        }
      });
      
      setCategoryMovies(filtered);
    };

    // Clean up function to remove the global function when component unmounts
    return () => {
      window.filterMovies = undefined;
    };
  }, [allMovies]);

  if (loading) return <div className={styles.loading}>Loading movies...</div>;
  if (error) return <div className={styles.error}>Error: {error}</div>;
  if (Object.keys(categoryMovies).length === 0) return <div className={styles.empty}>No movies found</div>;

  return (
    <div>
      <h1>Movies by Category</h1>
      <div className={styles.categories}>
        {Object.entries(categoryMovies).map(([categoryTitle, movies]) => (
          <div key={categoryTitle} className={styles.category}>
            <h2>{categoryTitle}</h2>
            <div className={styles.movieList}>
              {movies.map((movie) => (
                <MovieItem key={movie.id} movie={movie} />
              ))}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default MoviesByCategory;