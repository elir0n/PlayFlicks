# Netflix-BarIlan

[Link To Page](https://github.com/ViviShp/Netflix-BarIlan)

**In our code, our goal is to build a web server that will serve as the core of our application and expose a RESTful API, including storing data on a MongoDB server.**

The code has multiple functions such as:

1. When the URL received is: "<http://foo.com/api/users>":
   * POST method will create a new user.
2. When the URL received is: "<http://foo.com/api/users/:id>":
   * GET method will return all of the details of the user that has that specific id.
3. When the URL received is: "<http://foo.com/api/tokens>":
   * POST method receives the username and password as a JSON object in the body and checks whether the user is registered in the system or not.
4. When the URL received is: "<http://foo.com/api/categories>":
   * GET method returns all categories, POST method creates a new category.
5. When the URL received is: "<http://foo.com/api/categories/:id>":
   * GET method returns the category with the identifier id, PATCH method updates the category with the identifier id, DELETE method deletes the category with the identifier id.
6. When the URL received is: "<http://foo.com/api/movies>":
    * GET method returns a list of movies by categories. Each category has a field that defines whether it is promoted or not (boolean). If a category is marked as promoted, it will be included in the response, and for each promoted category, 20 random movies will be returned, but only movies the current user has not watched yet. Additionally, a special category is included for movies the user has already watched, containing the last 20 movies the user watched in random order.
    * POST method will create a new movie.
7. When the URL received is: "<http://foo.com/api/movies/:id>":
    * GET method provides the details of the movie with the identifier id.
    * PUT method replaces the existing movie with the identifier id.
    * DELETE method deletes the existing movie with the identifier id.
8. When the URL received is: "<http://foo.com/api/movies/:id/recommend/>":
    * GET method returns the recommended movies from the recommendation system of Exercise 2 for the current user and the movie with the identifier id.
    * POST method adds to the recommendation system of Exercise 2 for the current user that they have watched the movie with the identifier id, additionally, the recommendation system is implemented using a ThreadPool.
9. When the URL received is: "<http://foo.com/api/movies/search/:query/>":
    * GET method returns the movies that are the result of the search for query, meaning those where one of the movie's fields contains the string query.

these are the requirements of the exercise, but we have implemented other functions in order to manage the logic of the server better, and to keep by the SOLID principles:

1. When the URL received is: "<http://foo.com/api/movies/:id//category/add>":
   * POST method will add a category to a movie.
2. When the URL received is: "<http://foo.com/api/movies/get/allMovies>":
   * GET method returns all existing movies.
3. When the URL received is: "<http://foo.com/api/users/:id/addMovie>":
   * POST method is used to add a movie to the user's list of watched movies by the user with the given id.

the app runs on the server, and you, as client, command the server, and the server gives you back a response in accordance to your command.

## User Admin

now to make your user admin you need to follow this steps:

* open your terminal

![image](https://github.com/user-attachments/assets/d8ad3f33-22bb-4dab-a994-b2002bda7aca)

* find your mongoDB container name: running "docker ps -a"

![image](https://github.com/user-attachments/assets/c69ec1c9-dbb5-46f0-907e-dfe1fd8e454a)

* enter your container shell using "docker exec -it "container name" sh"

![image](https://github.com/user-attachments/assets/9203ff40-82f3-4671-aea3-5fa2ff669f92)

* enter this command to start mongosh: "mongosh"

![image](https://github.com/user-attachments/assets/167fed24-1ffc-4be9-a562-27f68ff800e7)

* enter this command to get to the correct collection: "use data"

![image](https://github.com/user-attachments/assets/0d8675c9-d899-4147-b26b-a12c5ac4586f)

* enter this command to change role to admin: "db.users.updateOne({_id:"user id"}, {$set:{role:"admin"}})  

![image](https://github.com/user-attachments/assets/cb01022e-dbb0-4d17-b160-2695a5c85a50)

[Back To README Page](/README.md)
