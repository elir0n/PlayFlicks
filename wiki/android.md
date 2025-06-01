# Android app run

In here we will show and explain the android app running

## Unsigned User Page

This page  is the first page you see when you open the app

![Screenshot 2025-02-28 164142](https://github.com/user-attachments/assets/a36025e5-7a80-4ba8-9aa5-32df160d8d2e)

in this page you have an option to sign in, or to register, by clicking on the button Get Started

## Register Page

In this page the user can register himself, or go to the login page

![Screenshot 2025-02-28 165121](https://github.com/user-attachments/assets/6ded7308-5bd7-42e3-a325-4639742014d5)

To register you must enter all the fields:

1. username
2. password at least 8 chars
3. confirm password
4. user display name
5. user profile image browse button to choose the image

## Login Page

In this page the user can login himself, or go to the register page

![Screenshot 2025-03-03 232157](https://github.com/user-attachments/assets/c4a7330f-cc8e-4387-a5e3-787309b3bdac)

For login you must enter the correct username and password to login

## Main Page

In this page you see some movies organized by categories that got from the api server

![image](https://github.com/user-attachments/assets/feb400d0-c4c4-4a5c-8b4c-903cf82422d1)

![image](https://github.com/user-attachments/assets/d2852487-dc86-48aa-9b17-135942a313bc)

![Screenshot 2025-03-03 234042](https://github.com/user-attachments/assets/e9c001a1-582d-4584-b0a7-de993733d384)

In this page you have the toolbar where you have buttons that can:
logout, change dark\light mode, search movie, go to admin panel if user is admin.

In the page itself you see all th movies cards in there categories and a random movie playing view,
you can go to see the movie details by selecting on it.

and you can also go to movies By categories page by licking the corresponding button

## View Movies By Categories Page

In this page you see all movies organized by categories that got from the api server

![image](https://github.com/user-attachments/assets/62885f00-8ddc-42ff-8494-fae754aa86bd)

In this page you have the toolbar where you have buttons that can:
logout, change dark\light mode, search movie, go to admin panel if user is admin.

In the page itself you see all th movies cards in there categories,
you can also go to see the movie details by selecting on it.

and you can also go to Main page by clicking the corresponding button.

## Search Page

In this page we can search for movies, to find them.

![image](https://github.com/user-attachments/assets/bb4bdbda-caad-47a1-9550-25c2da6d103b)

In this page we have the search bar you can search by all of the movie fields in hope of finding it
also after you see the movies you can short click on each one of them  to see its details.

## Admin Page

To get to this page you must be an admin (go to api docs to see more how to be an admin), in this page you have the option to create/edit/delete movies and categories

![image](https://github.com/user-attachments/assets/7e90eedc-e914-436e-8ee0-6949894957a2)

![image](https://github.com/user-attachments/assets/d0af3d83-d6de-41e8-87dd-b4c73d0089c5)

First you have a spinner item that you can view all your categories and add a new category "Add Category",
moreover if you select a category you can add or delete it by clicking the corresponding button.

To add movies you need to click the + button.

## Add or Edit Category Page

In this page the admin adds or edits a  category

![image](https://github.com/user-attachments/assets/c4e935cd-006e-4ad6-af93-eef9a90d342b)

Here you have two fields:

1. category name (required)
2. category promotion switch (on=True, off=False)

and then you can or save the changes by clicking the save button, or cancel all the operation you did by clicking the cancel button

## Add or Edit Movie Page

In this page the admin adds or edits a movie

![image](https://github.com/user-attachments/assets/8ef6c16d-af0d-4c21-9486-d558e09ed7d2)

Here you have multiple fields:

1. movie poster from media chooser (required)
2. movie title (required)
3. movie description
4. movie video from media chooser (required)
5. categories check box (needs to select al least 1)

to change or select image and video you need to select on the empty or current object and then media picker will be provided.

and finally then you can or save the changes by clicking the save button, or cancel all the operation you did by clicking the cancel button

## Movie Description Page

In this page we have all the movies data.

![Screenshot 2025-03-03 234028](https://github.com/user-attachments/assets/c1d7e614-1494-4ae2-961f-371efcbfc452)
![image](https://github.com/user-attachments/assets/4ac785cc-9770-45c1-be2e-22108da90168)

1. movie poster  
2. movie title
3. movie description
4. categories
5. recommended movies that got from api server

and moreover you have a button to play your movie to watch.

## movie watch page

This is the simplest page where all you see is your movie video.

![Screenshot 2025-03-03 235659](https://github.com/user-attachments/assets/730925fc-adeb-4d38-835f-32a957e33b44)

Here you can seek to a position in video go 5 sec back or forward, and start or stop video,
to exit the video in the middle you have to click the android built in back button.

[Back To README Page](/README.md)
