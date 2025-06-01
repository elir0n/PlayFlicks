import React, { useState, useEffect, useRef } from "react";
import styles from "../../styles/admin.module.css";

const MovieManager = () => {
  const [movies, setMovies] = useState([]);
  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  // Raw string for categories input
  const [categoriesInput, setCategoriesInput] = useState("");
  const [categories, setCategories] = useState([]);
  const [allCategories, setAllCategories] = useState([]);
  const [editingId, setEditingId] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [filteredMovies, setFilteredMovies] = useState([]);
  const [feedback, setFeedback] = useState({ message: "", type: "" });

  // File references
  const [imageFile, setImageFile] = useState(null);
  const [videoFile, setVideoFile] = useState(null);
  const [imagePreview, setImagePreview] = useState("");
  const [videoName, setVideoName] = useState("");

  const imageInputRef = useRef(null);
  const videoInputRef = useRef(null);

  // Display temporary feedback messages to the user
  const showFeedback = (message, type) => {
    setFeedback({ message, type });
    setTimeout(() => setFeedback({ message: "", type: "" }), 3000);
  };

  // Load all available categories
  useEffect(() => {
    const fetchCategories = async () => {
      try {
        const response = await fetch("http://localhost:3001/api/categories", {
          method: "GET",
          headers: {
            "Authorization": `Bearer ${localStorage.getItem("token")}`,
            "id": localStorage.getItem("userId"),
          },
        });

        if (!response.ok) throw new Error("Failed to fetch categories");
        const data = await response.json();
        setAllCategories(data);
      } catch (err) {
        console.error("Error fetching categories:", err);
      }
    };

    fetchCategories();
  }, []);

  // Load movies from the API with category details
  useEffect(() => {
    const fetchMovies = async () => {
      try {
        const response = await fetch("http://localhost:3001/api/movies/get/allMovies", {
          method: "GET",
          headers: {
            "Authorization": `Bearer ${localStorage.getItem("token")}`,
            "id": localStorage.getItem("userId"),
          },
        });

        if (!response.ok) throw new Error("Failed to fetch movies");

        const data = await response.json();

        // For each movie, fetch category details to display titles
        const moviesWithCategoryDetails = await Promise.all(
          data.map(async (movie) => {
            // Ensure categories is an array
            const categoryIds = Array.isArray(movie.categories) ? movie.categories : [];

            // Fetch category details for each ID
            const categoryDetails = await Promise.all(
              categoryIds.map(async (catId) => {
                try {
                  const category = await fetchCategoryById(catId);
                  return category ? category.title : null;
                } catch (err) {
                  console.error(`Error fetching category ${catId}:`, err);
                  return null;
                }
              })
            );

            // Filter out null values and return movie with category titles
            return {
              ...movie,
              id: movie._id || movie.id, // Ensure id is consistent
              _id: movie._id || movie.id, // Ensure _id is consistent
              categoryIds: categoryIds, // Keep original IDs
              categories: categoryDetails.filter(Boolean), // Display titles
            };
          })
        );

        setMovies(moviesWithCategoryDetails);
        setFilteredMovies(moviesWithCategoryDetails);
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };

    fetchMovies();
  }, []);

  // Set up global movie filtering functionality
  useEffect(() => {
    // Create a global registry for filter functions if it doesn't exist
    window.filterFunctions = window.filterFunctions || {};

    // Register the movie filter function
    const movieFilterFunction = (searchTerm) => {
      if (!searchTerm.trim()) {
        setFilteredMovies(movies);
        return;
      }

      const lowercasedTerm = searchTerm.toLowerCase();

      const filtered = movies.filter(movie =>
        movie.title.toLowerCase().includes(lowercasedTerm) ||
        (movie.description && movie.description.toLowerCase().includes(lowercasedTerm)) ||
        (movie.categories && movie.categories.some(category =>
          category && category.toLowerCase().includes(lowercasedTerm)
        ))
      );

      setFilteredMovies(filtered);
    };

    // Add our filter function to the registry
    window.filterFunctions.movies = movieFilterFunction;

    // Create or update the master filter function
    window.filterMovies = (searchTerm) => {
      // Call all registered filter functions
      Object.values(window.filterFunctions).forEach(filterFn => {
        if (typeof filterFn === 'function') {
          filterFn(searchTerm);
        }
      });
    };

    // When component unmounts, remove our filter function from the registry
    return () => {
      if (window.filterFunctions) {
        window.filterFunctions.movies = null;
      }
    };
  }, [movies]);

  // Fetch a category by its ID from the API
  const fetchCategoryById = async (categoryId) => {
    try {
      const response = await fetch(`http://localhost:3001/api/categories/${categoryId}`, {
        method: "GET",
        headers: {
          "Authorization": `Bearer ${localStorage.getItem("token")}`,
          "id": localStorage.getItem("userId"),
          "Content-Type": "application/json",
        },
      });

      if (!response.ok) throw new Error("Category not found");
      return await response.json();
    } catch (err) {
      console.error(`Error fetching category ${categoryId}:`, err);
      return null;
    }
  };

  // Fetch a category by its title from the API
  const fetchCategoryByTitle = async (categoryTitle) => {
    try {
      const response = await fetch(`http://localhost:3001/api/categories/getTitle/${categoryTitle}`, {
        method: "GET",
        headers: {
          "Authorization": `Bearer ${localStorage.getItem("token")}`,
          "id": localStorage.getItem("userId"),
          "Content-Type": "application/json",
        },
      });

      if (!response.ok) throw new Error("Category not found");
      const category = await response.json();
      return category;
    } catch (err) {
      console.error("Error fetching category:", err);
      return null;
    }
  };

  // Parse comma-separated category input into an array
  const parseCategoriesInput = (input) => {
    return input.split(",").map(cat => cat.trim()).filter(Boolean);
  };

  // Convert an array of category titles to their corresponding IDs
  const getCategoryIdsFromTitles = async (categoryTitles) => {
    const categoryIds = [];

    for (const title of categoryTitles) {
      if (!title.trim()) continue;

      try {
        const category = await fetchCategoryByTitle(title.trim());
        if (category && category._id) {
          categoryIds.push(category._id);
        }
      } catch (err) {
        console.error(`Error finding category "${title}":`, err);
      }
    }

    return categoryIds;
  };

  // Add a movie to a specific category
  const addMovieToCategory = async (movieId, categoryTitle) => {
    try {
      // Ensure movieId is a number if possible
      const numericMovieId = Number(movieId);
      const movieIdToUse = isNaN(numericMovieId) ? movieId : numericMovieId;
      
      const response = await fetch(`http://localhost:3001/api/movies/${movieIdToUse}/category/add`, {
        method: "POST",
        headers: {
          "Authorization": `Bearer ${localStorage.getItem("token")}`,
          "id": localStorage.getItem("userId"),
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ title: categoryTitle })
      });
  
      if (!response.ok) {
        const errorText = await response.text();
        // Check if it's just a "category already exists" error, which isn't critical
        if (errorText.includes("category already exists")) {
          return true; // Return success since the movie is already in this category
        }
        console.warn(`Failed to add movie to category: ${errorText}`);
        return false;
      }
  
      return true;
    } catch (err) {
      console.error("Error adding movie to category:", err);
      return false;
    }
  };

  // Remove a category from a movie
  const removeCategoryFromMovie = async (movieId, categoryId) => {
    try {
      // First, fetch the current movie data to get existing categories
      const movieResponse = await fetch(`http://localhost:3001/api/movies/${movieId}`, {
        method: "GET",
        headers: {
          "Authorization": `Bearer ${localStorage.getItem("token")}`,
          "id": localStorage.getItem("userId"),
        },
      });

      if (!movieResponse.ok) {
        console.warn(`Could not fetch movie ${movieId}`);
        return false;
      }

      const movie = await movieResponse.json();

      // Filter out the category ID from the movie's categories array
      const updatedCategoryIds = Array.isArray(movie.categories)
        ? movie.categories.filter(catId =>
          catId.toString() !== categoryId.toString()
        )
        : [];

      // Create FormData for the movie update
      const formData = new FormData();

      // Add all existing movie properties
      formData.append("title", movie.title);
      formData.append("description", movie.description || "");
      formData.append("categories", JSON.stringify(updatedCategoryIds));

      // Update the movie with the modified categories array
      const updateResponse = await fetch(`http://localhost:3001/api/movies/${movieId}`, {
        method: "PUT",
        headers: {
          "Authorization": `Bearer ${localStorage.getItem("token")}`,
          "id": localStorage.getItem("userId")
        },
        body: formData
      });

      if (!updateResponse.ok) {
        const errorText = await updateResponse.text();
        console.warn(`Failed to update movie categories: ${errorText}`);
        return false;
      }

      // Update the local state to reflect these changes
      setMovies(prevMovies =>
        prevMovies.map(m => {
          if ((m._id === movieId || m.id === movieId)) {
            // Find the category that was removed
            const removedCategory = allCategories.find(cat =>
              cat._id === categoryId || cat.id === categoryId
            );

            // Remove the category title from the display array
            const updatedCategories = m.categories.filter(categoryTitle =>
              removedCategory ? categoryTitle !== removedCategory.title : true
            );

            return {
              ...m,
              categories: updatedCategories,
              categoryIds: updatedCategoryIds
            };
          }
          return m;
        })
      );

      setFilteredMovies(prevFiltered =>
        prevFiltered.map(m => {
          if ((m._id === movieId || m.id === movieId)) {
            // Find the category that was removed
            const removedCategory = allCategories.find(cat =>
              cat._id === categoryId || cat.id === categoryId
            );

            // Remove the category title from the display array
            const updatedCategories = m.categories.filter(categoryTitle =>
              removedCategory ? categoryTitle !== removedCategory.title : true
            );

            return {
              ...m,
              categories: updatedCategories,
              categoryIds: updatedCategoryIds
            };
          }
          return m;
        })
      );

      return true;

    } catch (err) {
      console.error("Error removing category from movie:", err);
      return false;
    }
  };

  // Handle image file selection
  const handleImageChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      setImageFile(file);

      // Create a preview
      const reader = new FileReader();
      reader.onloadend = () => {
        setImagePreview(reader.result);
      };
      reader.readAsDataURL(file);
    }
  };

  // Handle video file selection
  const handleVideoChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      setVideoFile(file);
      setVideoName(file.name);
    }
  };

  // Add a movie to the recommendation server
  const addMovieToRecommendServer = async (movieId) => {
    // Maximum number of retry attempts
    const MAX_RETRIES = 3;
    // Delay between retries (in milliseconds)
    const RETRY_DELAY = 1000;
    
    // Function to delay execution
    const delay = ms => new Promise(resolve => setTimeout(resolve, ms));
    
    // Process the movie ID to ensure it's a number
    let recommendMovieId = movieId;
    
    // Check if the ID is a string containing only numbers
    if (typeof movieId === 'string' && /^\d+$/.test(movieId)) {
      recommendMovieId = parseInt(movieId, 10);
    } 
    // If it's a string with non-numeric characters (like an ObjectId), extract numeric part
    else if (typeof movieId === 'string') {
      const numericPart = movieId.replace(/\D/g, '');
      if (numericPart) {
        recommendMovieId = parseInt(numericPart, 10);
      }
    }
    
    let attempts = 0;
    
    while (attempts < MAX_RETRIES) {
      attempts++;
      
      try {
        const url = `http://localhost:3001/api/movies/${recommendMovieId}/recommend`;
        
        // Debug the request headers
        const headers = {
          "Authorization": `Bearer ${localStorage.getItem("token")}`,
          "id": localStorage.getItem("userId"),
          "Content-Type": "application/json"
        };
        
        const recommendResponse = await fetch(url, {
          method: "POST", 
          headers,
          signal: AbortSignal.timeout(10000) // Increase timeout to 10 seconds for debugging
        });
        
        // If successful or we get a specific error that indicates it won't work with retries
        if (recommendResponse.ok) {
          return true;
        } else if (recommendResponse.status === 400) {
          // 400 Bad Request likely indicates an issue with the data format that won't be fixed with retries
          const responseText = await recommendResponse.text();
          console.warn("Invalid request format:", responseText);
          return false;
        } else {
          // For 404 or other errors, we'll retry
          console.warn(`Attempt ${attempts} failed with status ${recommendResponse.status}`);
            
          // If we have more retries left, wait before trying again
          if (attempts < MAX_RETRIES) {
            await delay(RETRY_DELAY);
          }
        }
      } catch (recommendError) {
        console.warn(`Attempt ${attempts} error:`, recommendError);
        
        // If we have more retries left, wait before trying again
        if (attempts < MAX_RETRIES) {
          await delay(RETRY_DELAY);
        }
      }
    }
    
    // If we exhausted all retries
    console.warn(`Failed after ${MAX_RETRIES} attempts`);
    return false;
  };

  // Create a new movie
  const addMovie = async () => {
    if (!title.trim() || !description.trim() || categoriesInput.trim() === "") {
      showFeedback("Title, description, and categories are required", "error");
      return;
    }
  
    try {
      // Parse the categories input to get an array of category titles
      const categoryTitles = parseCategoriesInput(categoriesInput);
  
      // Convert category titles to IDs
      const categoryIds = await getCategoryIdsFromTitles(categoryTitles);
  
      if (categoryIds.length === 0) {
        showFeedback("No valid categories selected", "error");
        return;
      }
  
      // Create FormData for file uploads
      const formData = new FormData();
  
      // Add text fields
      formData.append("title", title);
      formData.append("description", description);
      formData.append("categories", JSON.stringify(categoryIds));
  
      // Add files with the correct field name
      if (imageFile) {
        formData.append("image-video", imageFile);
      }
  
      if (videoFile) {
        formData.append("image-video", videoFile);
      }
  
      const response = await fetch("http://localhost:3001/api/movies/", {
        method: "POST",
        headers: {
          "Authorization": `Bearer ${localStorage.getItem("token")}`,
          "id": localStorage.getItem("userId")
        },
        body: formData
      });
  
      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(`Failed to add movie: ${errorText}`);
      }
  
      const savedMovie = await response.json();
  
      // Extract movie ID - handle different response formats
      let movieId;
      if (typeof savedMovie === 'object' && (savedMovie._id || savedMovie.id)) {
        movieId = savedMovie._id || savedMovie.id;
      } else if (savedMovie === 'Movie created successfully') {
        // If we just got a success message, we need to fetch the latest movie
        const allMoviesResponse = await fetch("http://localhost:3001/api/movies/get/allMovies", {
          method: "GET",
          headers: {
            "Authorization": `Bearer ${localStorage.getItem("token")}`,
            "id": localStorage.getItem("userId"),
          },
        });
  
        if (allMoviesResponse.ok) {
          const allMovies = await allMoviesResponse.json();
          // Find our movie by title (assuming titles are unique)
          const foundMovie = allMovies.find(m => m.title === title);
          if (foundMovie) {
            movieId = foundMovie.id || foundMovie._id;
          }
        }
      }
  
      if (!movieId) {
        console.error("Could not determine movie ID from response");
        return;
      }
  
      // Construct the image and video paths based on the naming convention in your server code
      let imagePath = "";
      let videoPath = "";
  
      if (imageFile) {
        const extension = imageFile.name.substring(imageFile.name.lastIndexOf('.'));
        imagePath = `/moviePosters/${movieId}${extension}`;
      }
  
      if (videoFile) {
        const extension = videoFile.name.substring(videoFile.name.lastIndexOf('.'));
        videoPath = `/movieVideos/${movieId}${extension}`;
      }
  
      // Explicitly add movie to each category (this ensures the relationship is bidirectional)
      const categoryAddPromises = categoryTitles.map(async (categoryTitle) => {
        try {
          return await addMovieToCategory(movieId, categoryTitle);
        } catch (categoryError) {
          console.warn(`Error adding movie to category '${categoryTitle}':`, categoryError);
          return false;
        }
      });
  
      await Promise.all(categoryAddPromises);
  
      // Create a new movie object with the data we have
      const newMovie = {
        _id: movieId,
        id: movieId, // Ensure both id and _id are set
        title: title,
        description: description,
        categoryIds: categoryIds,
        categories: categoryTitles, // Display titles
        image: imagePath,
        video: videoPath
      };
  
      // Update movies state with new movie
      const updatedMovies = [...movies, newMovie];
      setMovies(updatedMovies);
      setFilteredMovies(updatedMovies);
  
      // Add to recommendation server directly using our helper function
      const recommendResult = await addMovieToRecommendServer(movieId);
      if (!recommendResult) {
        console.log("Failed to add movie to recommendation server, but movie was created successfully");
      }
  
      // Reset input fields 
      setTitle("");
      setDescription("");
      setCategoriesInput("");
      setCategories([]);
      setImageFile(null);
      setVideoFile(null);
      setImagePreview("");
      setVideoName("");
  
      // Reset file inputs
      if (imageInputRef.current) imageInputRef.current.value = "";
      if (videoInputRef.current) videoInputRef.current.value = "";
  
      showFeedback("Movie added successfully", "success");
  
    } catch (err) {
      showFeedback(err.message, "error");
      console.error("Error adding movie:", err);
    }
  };

  // Update an existing movie
  const updateMovie = async () => {
    if (!editingId || !title.trim() || !description.trim() || categoriesInput.trim() === "") {
      showFeedback("All fields are required for update", "error");
      return;
    }

    try {
      // Get the original movie to compare categories
      const originalMovie = movies.find(movie => (movie._id === editingId || movie.id === editingId));
      if (!originalMovie) {
        throw new Error(`Could not find original movie with ID: ${editingId}`);
      }

      // Parse the categories input to get an array of category titles
      const categoryTitles = parseCategoriesInput(categoriesInput);

      // Convert category titles to IDs
      const categoryIds = await getCategoryIdsFromTitles(categoryTitles);

      if (categoryIds.length === 0) {
        showFeedback("No valid categories selected", "error");
        return;
      }

      // Find categories that were removed from the movie
      const originalCategoryIds = originalMovie.categoryIds || [];
      const removedCategoryIds = originalCategoryIds.filter(origId =>
        !categoryIds.some(newId => newId.toString() === origId.toString())
      );

      // Update each category that had this movie removed from it
      if (removedCategoryIds.length > 0) {
        const categoryUpdatePromises = removedCategoryIds.map(async (categoryId) => {
          try {
            // First get the category to update its movies array
            const categoryResponse = await fetch(`http://localhost:3001/api/categories/${categoryId}`, {
              method: "GET",
              headers: {
                "Authorization": `Bearer ${localStorage.getItem("token")}`,
                "id": localStorage.getItem("userId")
              }
            });

            if (!categoryResponse.ok) {
              console.warn(`Could not fetch category ${categoryId}`);
              return false;
            }

            const category = await categoryResponse.json();

            // Filter out this movie from the category's movies array
            const updatedMovies = Array.isArray(category.movies)
              ? category.movies.filter(id => id.toString() !== editingId.toString())
              : [];

            // Update the category with the modified movies array
            const updateResponse = await fetch(`http://localhost:3001/api/categories/${categoryId}`, {
              method: "PATCH",
              headers: {
                "Authorization": `Bearer ${localStorage.getItem("token")}`,
                "id": localStorage.getItem("userId"),
                "Content-Type": "application/json"
              },
              body: JSON.stringify({
                ...category,
                movies: updatedMovies
              })
            });

            if (!updateResponse.ok) {
              console.warn(`Failed to update category ${categoryId} after removing movie`);
              return false;
            }

            return true;
          } catch (err) {
            console.error(`Error updating category ${categoryId}:`, err);
            return false;
          }
        });

        await Promise.all(categoryUpdatePromises);
      }

      // Create FormData for file uploads
      const formData = new FormData();
      formData.append("title", title);
      formData.append("description", description);
      formData.append("categories", JSON.stringify(categoryIds));

      // Add files if they exist
      if (imageFile) {
        formData.append("image-video", imageFile);
      }

      if (videoFile) {
        formData.append("image-video", videoFile);
      }

      const response = await fetch(`http://localhost:3001/api/movies/${editingId}`, {
        method: "PUT",
        headers: {
          "Authorization": `Bearer ${localStorage.getItem("token")}`,
          "id": localStorage.getItem("userId")
        },
        body: formData
      });

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(`Failed to update movie: ${errorText}`);
      }

      // Find new categories that need to be added
      const originalCategoryTitles = originalMovie.categories || [];
      const newCategories = categoryTitles.filter(cat => !originalCategoryTitles.includes(cat));

      // Explicitly add movie to each new category
      const categoryAddPromises = newCategories.map(categoryTitle =>
        addMovieToCategory(editingId, categoryTitle)
      );

      await Promise.all(categoryAddPromises);

      // Construct updated image and video paths if files were changed
      let updatedImage = null;
      let updatedVideo = null;

      if (imageFile) {
        const extension = imageFile.name.substring(imageFile.name.lastIndexOf('.'));
        updatedImage = `/moviePosters/${editingId}${extension}`;
      }

      if (videoFile) {
        const extension = videoFile.name.substring(videoFile.name.lastIndexOf('.'));
        updatedVideo = `/movieVideos/${editingId}${extension}`;
      }

      // Create updated movie object
      const updatedMovie = {
        ...originalMovie,
        id: editingId,  // Ensure id property exists
        _id: editingId, // Ensure _id property exists 
        title: title,
        description: description,
        categoryIds: categoryIds,
        categories: categoryTitles, // Use the current category titles
        // Only update image/video if new files were provided
        image: imageFile ? updatedImage : originalMovie.image,
        video: videoFile ? updatedVideo : originalMovie.video
      };

      // Update local state - check for either id or _id
      setMovies(movies.map(movie =>
        (movie._id === editingId || movie.id === editingId) ? updatedMovie : movie
      ));

      setFilteredMovies(prev => prev.map(movie =>
        (movie._id === editingId || movie.id === editingId) ? updatedMovie : movie
      ));

      // Reset form state
      setEditingId(null);
      setTitle("");
      setDescription("");
      setCategoriesInput("");
      setCategories([]);
      setImageFile(null);
      setVideoFile(null);
      setImagePreview("");
      setVideoName("");

      // Reset file inputs
      if (imageInputRef.current) imageInputRef.current.value = "";
      if (videoInputRef.current) videoInputRef.current.value = "";

      showFeedback("Movie updated successfully", "success");
    } catch (err) {
      showFeedback(err.message, "error");
      console.error("Error updating movie:", err);
    }
  };

  // Delete a movie
  const deleteMovie = async (id) => {
    // Add validation to ensure we have a valid ID
    if (!id) {
      showFeedback("Cannot delete movie: Movie ID is undefined", "error");
      return;
    }

    try {
      const response = await fetch(`http://localhost:3001/api/movies/${id}`, {
        method: "DELETE",
        headers: {
          "Authorization": `Bearer ${localStorage.getItem("token")}`,
          "id": localStorage.getItem("userId"),
          "Content-Type": "application/json",
        },
      });

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(`Failed to delete movie: ${errorText}`);
      }

      // Update local state to remove the deleted movie - check either property
      const updatedMovies = movies.filter(movie => (movie._id !== id && movie.id !== id));
      setMovies(updatedMovies);
      setFilteredMovies(updatedMovies);

      // If we were editing this movie, reset the form
      if (editingId === id) {
        setEditingId(null);
        setTitle("");
        setDescription("");
        setCategoriesInput("");
        setCategories([]);
        setImageFile(null);
        setVideoFile(null);
        setImagePreview("");
        setVideoName("");

        // Reset file inputs
        if (imageInputRef.current) imageInputRef.current.value = "";
        if (videoInputRef.current) videoInputRef.current.value = "";
      }

      showFeedback("Movie deleted successfully", "success");
    } catch (err) {
      showFeedback(err.message, "error");
      console.error("Error deleting movie:", err);
    }
  };

  // Prepare a movie for editing
  const startEditing = (movie) => {
    // Use either _id or id, whichever exists
    const movieId = movie._id || movie.id;

    if (!movieId) {
      showFeedback("Movie ID is missing", "error");
      return;
    }

    // Set the editingId state with the correct ID
    setEditingId(movieId);

    // Set form fields with movie data
    setTitle(movie.title);
    setDescription(movie.description);

    // Update both categories array and the input string
    if (Array.isArray(movie.categories) && movie.categories.length > 0) {
      setCategories(movie.categories);
      setCategoriesInput(movie.categories.join(", "));
    } else {
      setCategories([]);
      setCategoriesInput("");
    }

    if (movie.image) {
      const imageSrc = `http://localhost:3001${movie.image}`;
      setImagePreview(imageSrc);
    } else {
      setImagePreview("");
    }

    if (movie.video) {
      const videoFilename = movie.video.split('/').pop() || "Current video";
      setVideoName(videoFilename);
    } else {
      setVideoName("");
    }

    setImageFile(null);
    setVideoFile(null);
    if (imageInputRef.current) imageInputRef.current.value = "";
    if (videoInputRef.current) videoInputRef.current.value = "";
  };

  if (loading) return <div>Loading movies...</div>;
  if (error) return <div>Error: {error}</div>;

  return (
    <div className={styles.container}>
      <h2 className={styles.title}>Movie Manager</h2>

      {feedback.message && (
        <div className={`${styles.feedback} ${styles[feedback.type]}`}>
          {feedback.message}
        </div>
      )}

      <div className={styles.movieForm}>
        <input
          type="text"
          className={styles.inputField}
          placeholder="Movie Title"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
        />
        <textarea
          className={styles.inputField}
          placeholder="Description"
          value={description}
          onChange={(e) => setDescription(e.target.value)}
        />
        <input
          type="text"
          className={styles.inputField}
          placeholder="Categories (comma-separated)"
          value={categoriesInput}
          onChange={(e) => {
            setCategoriesInput(e.target.value);
            setCategories(parseCategoriesInput(e.target.value));
          }}
        />

        {/* Image upload */}
        <div className={styles.fileInputContainer}>
          <label className={styles.fileInputLabel}>
            Image Upload:
            <input
              type="file"
              accept="image/*"
              ref={imageInputRef}
              className={styles.fileInput}
              onChange={handleImageChange}
            />
          </label>
          {imagePreview && (
            <div className={styles.imagePreview}>
              <img
                src={imagePreview}
                alt="Preview"
                style={{ width: '100px', height: 'auto' }}
                crossOrigin="anonymous"
                onError={(e) => {
                  console.log("Image failed to load:", e);
                  console.log("Failed image URL:", e.target.src);
                  // You can set a fallback image or just hide the image
                  e.target.style.display = 'none';
                  // Alternatively, show a placeholder text
                  e.target.parentNode.textContent = "Image preview unavailable";
                }}
              />
            </div>
          )}
        </div>

        {/* Video upload */}
        <div className={styles.fileInputContainer}>
          <label className={styles.fileInputLabel}>
            Video Upload:
            <input
              type="file"
              accept="video/*"
              ref={videoInputRef}
              className={styles.fileInput}
              onChange={handleVideoChange}
            />
          </label>
          {videoName && (
            <div className={styles.videoNamePreview}>
              Selected video: {videoName}
            </div>
          )}
        </div>

        {editingId === null ? (
          <button className={styles.btnAdd} onClick={addMovie}>Add Movie</button>
        ) : (
          <div>
            <button className={styles.btnUpdate} onClick={updateMovie}>Update Movie</button>
            <button
              className={styles.btnCancel}
              onClick={() => {
                setEditingId(null);
                setTitle("");
                setDescription("");
                setCategoriesInput("");
                setCategories([]);
                setImageFile(null);
                setVideoFile(null);
                setImagePreview("");
                setVideoName("");
                if (imageInputRef.current) imageInputRef.current.value = "";
                if (videoInputRef.current) videoInputRef.current.value = "";
              }}
            >
              Cancel
            </button>
          </div>
        )}
      </div>

      <ul className={styles.movieList}>
        {filteredMovies.map((movie) => {
          // Ensure we have an ID for the movie
          const movieId = movie._id || movie.id;

          return (
            <li key={movieId} className={styles.movieItem}>
              <div className={styles.movieDetails}>
                <h3>{movie.title}</h3>
                <p>{movie.description}</p>
                <p>Categories: {movie.categories?.length > 0 ? movie.categories.join(", ") : "No categories"}</p>
                {movie.image && (
                  <div>
                    <p>Image: {movie.image.split('/').pop()}</p>
                    <img
                      src={`http://localhost:3001${movie.image}`}
                      alt={`Thumbnail for ${movie.title}`}
                      style={{ width: '80px', height: 'auto', marginTop: '5px' }}
                      crossOrigin="anonymous"
                      onError={(e) => {
                        console.log("List image failed to load:", e);
                        console.log("Failed image URL:", e.target.src);
                        e.target.style.display = 'none';
                      }}
                    />
                  </div>
                )}
                {movie.video && (
                  <p>Video: {movie.video.split('/').pop()}</p>
                )}
              </div>
              <div className={styles.movieActions}>
                <button className={styles.btnEdit} onClick={() => startEditing(movie)}>Edit</button>
                <button className={styles.btnDelete} onClick={() => deleteMovie(movieId)}>Delete</button>
              </div>
            </li>
          );
        })}
      </ul>
    </div>
  )
};

export default MovieManager;