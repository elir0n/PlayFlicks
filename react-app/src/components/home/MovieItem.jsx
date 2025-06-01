import React from 'react';
import { useNavigate } from 'react-router-dom';
import styles from '../../styles/home.module.css';

const MovieItem = ({ movie }) => {
  const navigate = useNavigate();

  // Handle movie click
  const handleMovieClick = () => {
    navigate(`/details/${movie.id}`, { state: { movie } });
  };

  return (
    <div className={styles.movieItem} onClick={handleMovieClick}>
      <img 
        src={`http://localhost:3001${movie.image}`} 
        alt={movie.title} 
        className={styles.movieImage}
      />
      <h3>{movie.title}</h3>
    </div>
  );
};

export default MovieItem;