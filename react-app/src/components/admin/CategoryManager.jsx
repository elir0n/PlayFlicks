import React, { useState, useEffect, useCallback } from "react";
import styles from "../../styles/admin.module.css";

function CategoryManager() {
    const [categories, setCategories] = useState([]);
    const [filteredCategories, setFilteredCategories] = useState([]);
    const [title, setTitle] = useState("");
    const [moviesInput, setMoviesInput] = useState("");
    const [movies, setMovies] = useState([]);
    const [allMovies, setAllMovies] = useState([]);
    const [editingId, setEditingId] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [promoted, setPromoted] = useState(false);
    const [feedback, setFeedback] = useState({ message: "", type: "" });

    // Display temporary feedback messages to the user
    const showFeedback = (message, type) => {
        setFeedback({ message, type });
        setTimeout(() => setFeedback({ message: "", type: "" }), 3000);
    };

    // Fetch all movies from the API
    const fetchMovies = useCallback(async () => {
        try {
            const response = await fetch("http://localhost:3001/api/movies", {
                method: "GET",
                headers: {
                    "Authorization": `Bearer ${localStorage.getItem("token")}`,
                    "id": localStorage.getItem("userId"),
                },
            });

            if (!response.ok) throw new Error("Failed to fetch movies");
            const data = await response.json();
            
            // Handle case where API returns an object with arrays
            if (typeof data === 'object' && !Array.isArray(data)) {
                // Flatten all arrays in the object into a single array
                const flattenedMovies = [];
                Object.values(data).forEach(movieArray => {
                    if (Array.isArray(movieArray)) {
                        flattenedMovies.push(...movieArray);
                    }
                });
                setAllMovies(flattenedMovies);
            } else if (Array.isArray(data)) {
                setAllMovies(data);
            } else {
                console.error("Unexpected movies data format:", data);
                setAllMovies([]);
            }
        } catch (err) {
            console.error("Error fetching movies:", err);
            setAllMovies([]); 
        }
    }, []);

    // Fetch all categories from the API
    const fetchCategories = useCallback(async () => {
        try {
            setLoading(true);
            const response = await fetch("http://localhost:3001/api/categories", {
                method: "GET",
                headers: {
                    "Authorization": `Bearer ${localStorage.getItem("token")}`,
                    "id": localStorage.getItem("userId"),
                },
            });

            if (!response.ok) throw new Error("Failed to fetch categories");
            const data = await response.json();

            if (Array.isArray(data)) {
                // Process categories to ensure consistent ID properties
                const processedCategories = data.map(category => ({
                    ...category,
                    id: category._id || category.id,
                    _id: category._id || category.id
                }));
                setCategories(processedCategories);
                setFilteredCategories(processedCategories);
            } else {
                console.error("Categories data is not an array:", data);
                setCategories([]);
                setFilteredCategories([]);
            }
        } catch (err) {
            setError(err.message);
            console.error("Error fetching categories:", err);
        } finally {
            setLoading(false);
        }
    }, []);

    // Load all available movies
    useEffect(() => {
        fetchMovies();
    }, [fetchMovies]);

    // Load categories from the API
    useEffect(() => {
        fetchCategories();
    }, [fetchCategories]);

    // Set up global category filtering functionality
    useEffect(() => {
        // Create a global registry for filter functions if it doesn't exist
        window.filterFunctions = window.filterFunctions || {};
        
        // Register the category filter function
        const categoryFilterFunction = (searchTerm) => {
            if (!searchTerm.trim()) {
                setFilteredCategories(categories);
                return;
            }
    
            const lowercasedTerm = searchTerm.toLowerCase();
    
            const filtered = categories.filter(category => {
                // First check if category title matches
                if (category.title.toLowerCase().includes(lowercasedTerm)) {
                    return true;
                }
                
                // Then check if any of its movies match
                if (Array.isArray(category.movies) && category.movies.length > 0) {
                    // Try to look up movie titles by ID
                    const movieMatches = category.movies.some(movieId => {
                        // Find movie by ID in allMovies
                        const movie = allMovies.find ? allMovies.find(m => 
                            (m._id && m._id.toString() === movieId.toString()) ||
                            (m.id && m.id.toString() === movieId.toString())
                        ) : null;
                        
                        // Check if movie title includes search term
                        return movie && movie.title.toLowerCase().includes(lowercasedTerm);
                    });
                    
                    if (movieMatches) return true;
                }
                
                // If category has stored movieTitles property, check those
                if (category.movieTitles && Array.isArray(category.movieTitles)) {
                    const movieTitleMatches = category.movieTitles.some(title => 
                        title.toLowerCase().includes(lowercasedTerm)
                    );
                    
                    if (movieTitleMatches) return true;
                }
                
                return false;
            });
    
            setFilteredCategories(filtered);
        };
        
        // Add our filter function to the registry
        window.filterFunctions.categories = categoryFilterFunction;
        
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
                window.filterFunctions.categories = null;
            }
        };
    }, [categories, allMovies]);

    // Fetch a movie by its title from the API
    const fetchMovieByTitle = async (movieTitle) => {
        try {
            const response = await fetch(`http://localhost:3001/api/movies/getTitle/${encodeURIComponent(movieTitle.trim())}`, {
                method: "GET",
                headers: {
                    "Authorization": `Bearer ${localStorage.getItem("token")}`,
                    "id": localStorage.getItem("userId"),
                    "Content-Type": "application/json",
                },
            });

            if (!response.ok) throw new Error("Movie not found");
            const movie = await response.json();
            return movie;
        } catch (err) {
            console.error("Error fetching movie:", err);
            return null;
        }
    };

    // Convert an array of movie titles to their corresponding IDs
    const getMovieIdsFromTitles = async (movieTitles) => {
        const movieIds = [];

        for (const title of movieTitles) {
            if (!title.trim()) continue;

            try {
                const movie = await fetchMovieByTitle(title.trim());
                if (movie && (movie._id || movie.id)) {
                    movieIds.push(movie._id || movie.id);
                }
            } catch (err) {
                console.error(`Error finding movie "${title}":`, err);
            }
        }

        return movieIds;
    };

    // Parse comma-separated movie titles into an array
    const parseMoviesInput = (input) => {
        return input.split(",").map(movie => movie.trim()).filter(Boolean);
    };

    // Add a movie to a specific category
    const addMovieToCategory = async (movieId, categoryId) => {
        try {
            // First check if we have the category in our local state
            const category = categories.find(c => 
                (c._id && c._id.toString() === categoryId.toString()) || 
                (c.id && c.id.toString() === categoryId.toString())
            );
            
            let categoryTitle;
            
            // If we found the category locally, use its title
            if (category) {
                categoryTitle = category.title;
            } 
            // Otherwise, try to fetch the category from the server
            else {
                try {
                    const response = await fetch(`http://localhost:3001/api/categories/${categoryId}`, {
                        method: "GET",
                        headers: {
                            "Authorization": `Bearer ${localStorage.getItem("token")}`,
                            "id": localStorage.getItem("userId"),
                        }
                    });
                    
                    if (response.ok) {
                        const fetchedCategory = await response.json();
                        categoryTitle = fetchedCategory.title;
                    } else {
                        // If we can't get the category from the server, use the ID as fallback
                        categoryTitle = categoryId;
                    }
                } catch (fetchErr) {
                    console.log(`Error fetching category ${categoryId}:`, fetchErr);
                    categoryTitle = categoryId;
                }
            }
            
            if (!categoryTitle) {
                console.log(`Cannot determine title for category ${categoryId}, aborting`);
                return false;
            }

            const response = await fetch(`http://localhost:3001/api/movies/${movieId}/category/add`, {
                method: "POST",
                headers: {
                    "Authorization": `Bearer ${localStorage.getItem("token")}`,
                    "id": localStorage.getItem("userId"),
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({ title: categoryTitle })
            });

            if (!response.ok) {
                // Special case for 400 Bad Request - this often happens when the relationship already exists
                if (response.status === 400) {
                    // Check if the error is because the relationship already exists
                    const errorText = await response.text();
                    if (errorText.includes("already exists")) {
                        return true; // Return true because the relationship exists, which is what we wanted
                    } else {
                        console.log(`Could not add movie ${movieId} to category ${categoryTitle}: ${errorText}`);
                        return false;
                    }
                } else {
                    const errorText = await response.text();
                    console.log(`Could not add movie ${movieId} to category ${categoryTitle}: ${errorText}`);
                    return false;
                }
            }

            return true;
        } catch (err) {
            console.log("Error in addMovieToCategory:", err);
            return false;
        }
    };

    // Create a new category
    const addCategory = async () => {
        if (!title.trim()) {
            showFeedback("Title is required", "error");
            return;
        }

        try {
            // Parse the movies input to get an array of movie titles
            const movieTitles = parseMoviesInput(moviesInput);
            
            // Convert movie titles to IDs
            const movieIds = await getMovieIdsFromTitles(movieTitles);
            
            const categoryData = {
                title: title,
                promoted: promoted,
                movies: movieIds
            };

            const response = await fetch("http://localhost:3001/api/categories", {
                method: "POST",
                headers: {
                    "Authorization": `Bearer ${localStorage.getItem("token")}`,
                    "id": localStorage.getItem("userId"),
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(categoryData)
            });

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(`Failed to add category: ${errorText}`);
            }

            const savedCategory = await response.json();
            
            let categoryId;
            if (typeof savedCategory === 'object' && (savedCategory._id || savedCategory.id)) {
                categoryId = savedCategory._id || savedCategory.id;
            } else if (typeof savedCategory === 'string') {
                // Wait a moment to make sure the server has processed the request
                await new Promise(resolve => setTimeout(resolve, 300));
                
                // Get all categories and find the new one by title
                try {
                    const categoriesResponse = await fetch("http://localhost:3001/api/categories", {
                        method: "GET",
                        headers: {
                            "Authorization": `Bearer ${localStorage.getItem("token")}`,
                            "id": localStorage.getItem("userId"),
                        }
                    });
                    
                    if (categoriesResponse.ok) {
                        const allCategories = await categoriesResponse.json();
                        if (Array.isArray(allCategories)) {
                            const foundCategory = allCategories.find(c => c.title === title);
                            if (foundCategory) {
                                categoryId = foundCategory._id || foundCategory.id;
                            }
                        }
                    }
                } catch (fetchErr) {
                    console.error("Error fetching categories after creation:", fetchErr);
                }
            }
            
            if (!categoryId) {
                // Even though we couldn't get the ID, we'll add a temporary category to the UI
                // The category will be updated with the correct ID after the next fetch
                const tempId = `temp-${Date.now()}`;
                const tempCategory = {
                    _id: tempId,
                    id: tempId,
                    title: title,
                    promoted: promoted,
                    movies: movieIds,
                    movieTitles: parseMoviesInput(moviesInput),
                    isTemporary: true // Flag to identify this is a temporary entry
                };
                
                // Add the temporary category to the UI
                setCategories(prevCategories => {
                    const newCategories = [...prevCategories, tempCategory];
                    setFilteredCategories(newCategories);
                    return newCategories;
                });
                
                // Refresh categories from server in background
                setTimeout(() => {
                    fetchCategories().then(() => {
                        console.log("Categories refreshed after adding new category");
                    });
                }, 500);
                
                // Reset form
                setTitle("");
                setMoviesInput("");
                setMovies([]);
                setPromoted(false);
                showFeedback("Category added successfully", "success");
                return;
            }

            // Explicitly update each movie to include this category
            if (movieIds.length > 0) {
                const addPromises = movieIds.map(movieId => 
                    addMovieToCategory(movieId, categoryId)
                );
                
                try {
                    await Promise.all(addPromises);
                } catch (err) {
                    console.error("Error updating movies with new category:", err);
                }
            }

            // Create a new category object with the data we have
            const newCategory = {
                _id: categoryId,
                id: categoryId,
                title: title,
                promoted: promoted,
                movies: movieIds,
                movieTitles: parseMoviesInput(moviesInput) // Store the movie titles for display
            };

            // Update categories state (with function form to ensure we're using the latest state)
            setCategories(prevCategories => {
                const newCategories = [...prevCategories, newCategory];
                setFilteredCategories(newCategories);
                return newCategories;
            });
            
            // Reset form
            setTitle("");
            setMoviesInput("");
            setMovies([]);
            setPromoted(false);
            showFeedback("Category added successfully", "success");
        } catch (err) {
            showFeedback(err.message, "error");
            console.error("Error adding category:", err);
        }
    };

    // Update an existing category
    const updateCategory = async () => {
        if (!editingId || !title.trim()) {
            showFeedback("All fields are required for update", "error");
            return;
        }

        try {
            // Get the original category to compare movies
            const originalCategory = categories.find(cat => cat._id === editingId || cat.id === editingId);
            if (!originalCategory) {
                throw new Error(`Could not find original category with ID: ${editingId}`);
            }
            
            // Parse the movies input to get an array of movie titles
            const movieTitles = parseMoviesInput(moviesInput);
            
            // Convert movie titles to IDs
            const movieIds = await getMovieIdsFromTitles(movieTitles);
            
            // Find movies that were removed from the category
            const originalMovieIds = originalCategory.movies || [];
            const removedMovieIds = originalMovieIds.filter(origId => {
                // Use explicit string conversion for comparing IDs of different types
                return !movieIds.some(newId => String(origId).trim() === String(newId).trim());
            });
            
            // Remove this category from each removed movie
            if (removedMovieIds.length > 0) {
                const removePromises = removedMovieIds.map(async (movieId) => {
                    try {
                        // First, fetch the movie to get its current data
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
                        
                        // Filter out the category ID from the movie's categories array with explicit type handling
                        const updatedCategories = Array.isArray(movie.categories) 
                            ? movie.categories.filter(catId => {
                                // Convert both IDs to strings and trim any whitespace
                                const catIdStr = String(catId).trim();
                                const editingIdStr = String(editingId).trim();
                                
                                // Return true to keep categories that don't match the current editing category
                                return catIdStr !== editingIdStr;
                              })
                            : [];
                        
                        // Create FormData for the movie update
                        const formData = new FormData();
                        
                        // Add all existing movie properties
                        formData.append("title", movie.title);
                        formData.append("description", movie.description || "");
                        formData.append("categories", JSON.stringify(updatedCategories));
                        
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
                        
                        return true;
                        
                    } catch (err) {
                        console.error("Error removing category from movie:", err);
                        return false;
                    }
                });
                
                try {
                    await Promise.all(removePromises);
                } catch (err) {
                    console.error("Error updating removed movies:", err);
                    // Continue with the update even if some removals fail
                }
            }
            
            const categoryData = {
                title: title,
                promoted: promoted,
                movies: movieIds
            };

            const response = await fetch(`http://localhost:3001/api/categories/${editingId}`, {
                method: "PATCH",
                headers: {
                    "Authorization": `Bearer ${localStorage.getItem("token")}`,
                    "id": localStorage.getItem("userId"),
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(categoryData)
            });

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(`Failed to update category: ${errorText}`);
            }
            
            // Find new movies that need to be updated with this category
            const newMovies = movieIds.filter(id => {
                // Use explicit string conversion for comparing IDs of different types
                return !originalMovieIds.some(origId => String(origId).trim() === String(id).trim());
            });
            
            // Update each new movie to include this category
            if (newMovies.length > 0) {
                const addPromises = newMovies.map(movieId => 
                    addMovieToCategory(movieId, editingId)
                );
                
                try {
                    await Promise.all(addPromises);
                } catch (err) {
                    console.error("Error updating movies with category:", err);
                }
            }
            
            // Create updated category object
            const updatedCategory = {
                ...originalCategory,
                id: editingId,
                _id: editingId,
                title: title,
                promoted: promoted,
                movies: movieIds,
                movieTitles: parseMoviesInput(moviesInput) // Store the movie titles for display
            };

            // Update local state (with function form to ensure we're using the latest state)
            setCategories(prevCategories => {
                const updatedCategories = prevCategories.map(cat =>
                    (cat._id === editingId || cat.id === editingId) ? updatedCategory : cat
                );
                
                // Also update filtered categories
                setFilteredCategories(updatedCategories);
                
                return updatedCategories;
            });

            // Reset form
            setEditingId(null);
            setTitle("");
            setMoviesInput("");
            setMovies([]);
            setPromoted(false);
            showFeedback("Category updated successfully", "success");
        } catch (err) {
            showFeedback(err.message, "error");
            console.error("Error updating category:", err);
        }
    };

    // Delete a category by ID
    const deleteCategory = async (id) => {
        if (!id) {
            showFeedback("Cannot delete category: Category ID is undefined", "error");
            return;
        }
        
        try {
            const response = await fetch(`http://localhost:3001/api/categories/${id}`, {
                method: "DELETE",
                headers: {
                    "Authorization": `Bearer ${localStorage.getItem("token")}`,
                    "id": localStorage.getItem("userId"),
                    "Content-Type": "application/json",
                }
            });

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(`Failed to delete category: ${errorText}`);
            }
            
            // Update local state (with function form to ensure we're using the latest state)
            setCategories(prevCategories => {
                const updatedCategories = prevCategories.filter(cat => (cat._id !== id && cat.id !== id));
                
                // Also update filtered categories
                setFilteredCategories(updatedCategories);
                
                return updatedCategories;
            });
            
            // If we were editing this category, reset the form
            if (editingId === id) {
                setEditingId(null);
                setTitle("");
                setMoviesInput("");
                setMovies([]);
                setPromoted(false);
            }
            
            showFeedback("Category deleted successfully", "success");
        } catch (err) {
            showFeedback(err.message, "error");
            console.error("Error deleting category:", err);
        }
    };

    // Start editing a category
    const startEditing = (category) => {
        const categoryId = category._id || category.id;
        
        if (!categoryId) {
            showFeedback("Cannot edit: Category ID is missing", "error");
            return;
        }
        
        setEditingId(categoryId);
        setTitle(category.title || "");
        setPromoted(category.promoted || false);
        
        // Get movie titles for the movies in this category
        if (Array.isArray(category.movies) && category.movies.length > 0) {
            // Find the movie titles from the IDs/references
            const movieTitles = category.movies.map(movieId => {
                // Try to find movie by ID
                const movie = allMovies.find ? allMovies.find(m => 
                    (m._id && m._id.toString() === movieId.toString()) ||
                    (m.id && m.id.toString() === movieId.toString())
                ) : null;
                
                return movie ? movie.title : movieId;
            });
            
            // Set the movies array and the input string
            setMovies(movieTitles);
            setMoviesInput(movieTitles.join(", "));
        } else {
            setMovies([]);
            setMoviesInput("");
        }
    };

    if (loading && categories.length === 0) {
        return <div className={styles.loading}>Loading categories...</div>;
    }

    return (
        <div className={styles.container}>
            <h2 className={styles.title}>Category Manager</h2>

            {feedback.message && (
                <div className={`${styles.feedback} ${styles[feedback.type]}`}>
                    {feedback.message}
                </div>
            )}

            <div className={styles.movieForm}>
                <input
                    type="text"
                    className={styles.inputField}
                    placeholder="Category Title"
                    value={title}
                    onChange={(e) => setTitle(e.target.value)}
                />
                <input
                    type="text"
                    className={styles.inputField}
                    placeholder="Movies (comma-separated titles)"
                    value={moviesInput}
                    onChange={(e) => {
                        setMoviesInput(e.target.value);
                        setMovies(parseMoviesInput(e.target.value));
                    }}
                />
                <div className={styles.inputField}>
                    <label>
                        <input
                            type="checkbox"
                            checked={promoted}
                            onChange={(e) => setPromoted(e.target.checked)}
                        /> Promoted
                    </label>
                </div>

                {editingId === null ? (
                    <button className={styles.btnAdd} onClick={addCategory}>Add Category</button>
                ) : (
                    <div>
                        <button className={styles.btnUpdate} onClick={updateCategory}>Update Category</button>
                        <button 
                            className={styles.btnCancel}
                            onClick={() => {
                                setEditingId(null);
                                setTitle("");
                                setMoviesInput("");
                                setMovies([]);
                                setPromoted(false);
                            }}
                        >
                            Cancel
                        </button>
                    </div>
                )}
            </div>

            <ul className={styles.movieList}>
                {filteredCategories.map((category) => {
                    // Ensure we have an ID for the category
                    const categoryId = category._id || category.id;
                    
                    // Get movie titles for display
                    let movieTitles;
                    
                    // If we have stored movie titles from adding/editing, use those
                    if (category.movieTitles && Array.isArray(category.movieTitles) && category.movieTitles.length > 0) {
                        movieTitles = category.movieTitles.join(", ");
                    }
                    // Otherwise try to look up titles by ID
                    else if (Array.isArray(category.movies) && category.movies.length > 0) {
                        movieTitles = category.movies.map(movieId => {
                            // Find movie by ID or title in allMovies
                            const movie = allMovies.find ? allMovies.find(m => 
                                (m._id && m._id.toString() === movieId.toString()) ||
                                (m.id && m.id.toString() === movieId.toString()) ||
                                // If it's already a title, just use it
                                (typeof movieId === 'string' && m.title === movieId)
                            ) : null;
                            return movie ? movie.title : movieId;
                        }).join(", ");
                    } else {
                        movieTitles = "No movies";
                    }
                    
                    return (
                        <li key={categoryId} className={styles.movieItem}>
                            <div className={styles.movieDetails}>
                                <h3>{category.title}</h3>
                                <p>Movies: {movieTitles}</p>
                                <p>Status: {category.promoted ? "Promoted" : "Not Promoted"}</p>
                            </div>
                            <div className={styles.movieActions}>
                                <button className={styles.btnEdit} onClick={() => startEditing(category)}>Edit</button>
                                <button className={styles.btnDelete} onClick={() => deleteCategory(categoryId)}>Delete</button>
                            </div>
                        </li>
                    );
                })}
                {filteredCategories.length === 0 && categories.length > 0 && (
                    <li className={styles.emptyList}>No categories match your search.</li>
                )}
                {categories.length === 0 && (
                    <li className={styles.emptyList}>No categories found. Create your first category above.</li>
                )}
            </ul>
        </div>
    );
}

export default CategoryManager;