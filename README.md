# EverythingEverywhereHere_Capstone
My Capstone Project
Overview

This apps fetches product data from multiple ecommerce stores so users can easily compare products from everywhere all in one place.

App Evaluation

**Category:** eCommerce

**Mobile:** This app would be primarily developed for mobile but would perhaps be just as viable on a computer, such as tinder or other similar apps. 

Functionality wouldn't be limited to mobile devices, however mobile version could potentially have more features.

**Story:** Gets and shows products from multiple stores for users to interact and compare them. If interested, the user is transferred to the original's store website to purchase the product.

**Market:** Any individual could choose to use this app, and to keep it a safe environment, especially those that enjoy shopping online.

**Habit:** This app could be used as often or unoften as the user wanted depending on how frequently they purchase items online.

**Scope:** First we would read product data from two huge online stores(Amazon and Walmart) and display to the user. The most important details are displayed to the user. Details like product name, descrpition, price, ratings, image are displayed. This results are then saved unto the device for quicker lookup in times of terrible to no internet connection.

**Product Spec**
1. User Stories (Required and Optional)

Required Must-have Stories:

* User logs in to access the search product feature.
* Multiple products will be displayed to the user for comparison.
* Profile pages for each user.
* Access to data stored on the device using SQLite.

Optional Stories:
* Splash screen
* Animation
* Filters
* Changing email/ changing password.

2. Screen Archetypes
Login
Register - User signs up to open a new account.
User can login to previously opened account.

**Home Screen** - Contains a list of keywords that have been previously searched and stored in the database.

**User Screen**

* Allows users to logout.
* Allows users to change login details(email and password)

**Search Screen.**

* Allows user to be able to search for any product and interact with the returned results. The users are able to view the details and even buy now!

**Product details Screen**

* Contains all the details of a product not shown on the search screen.

3. **Botton Navigation**

**Home:** Navigates to the Home fragment(screen)

**Search:** Navigates to the Search fragment(screen)

**User:** Navigates to the User fragment(screen)

**App Navigation:**

Forced Log-in -> Account creation if no log in is available

Home screen is the default screen opened.

* Clicked item on Home screen -> opens result screen, which reads its data from the database.

Search -> Search fragment with a serach view for users to search for any product.

User -> Shows users details.

Here's a view of implemented user stories:

<img src='/Screen Shot 2022-07-11 at 4.png' title='Video Walkthrough' width='' alt='Video Walkthrough' />


**Login Page**

For a login action:

HTTP Verb             Example

GET                   Fetching the user's login details

For a signup action:
HTTP Verb             Example
POST                  Creating and storing a new user

**Home Page**
HTTP Verb             Example
GET                   Fetching all the products from the stores API. Posted on the search fragment.
