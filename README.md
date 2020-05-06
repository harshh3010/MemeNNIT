# MemeNNIT
MemeNNIT is an app for people who love sharing and making memes.
A memennit user can upload his own memes and view memes uploaded by other users,
the users are rewarded points on the basis of number of uploads and upvotes they recieve.
<br>
These points are used for computing the rank and generating a leaderboard listing all the users.

### Getting started
On launching the app, a splashscreen is displayed, here the app checks whether the user is logged-in through his/her google account
or not and accordingly redirects the user to the **Login Screen** or the **Home Screen**.
<br>
The users can register using their google account only.
<br>
<br>
<img src="https://github.com/harshh3010/MemeNNIT/blob/master/AppScreenshots/SplashScreen.jpg" width="200">
<img src="https://github.com/harshh3010/MemeNNIT/blob/master/AppScreenshots/LoginScreen.jpg" width="200">
<br>
<br>
 
 ### Home Screen
 After logging-in, the user is brought to the **Post Activity**. This activity contains a **Bottom Navigation Bar** using 
 which the user can navigate to different fragments in the app.
 <br>
 The fragment first displayed to the user is the **Home Fragment** which lists the posts uploaded by all other users in a RecyclerView.
 Here the user can interact with these posts. He/She can like and comment on the posts and also report the post if they find it inappropriate.
 <br>
 <br>
 <img src="https://github.com/harshh3010/MemeNNIT/blob/master/AppScreenshots/HomeFragment.jpg" width="200">
  <img src="https://github.com/harshh3010/MemeNNIT/blob/master/AppScreenshots/CommentsActivity.jpg" width="200">
  <br>
  <br>
  The **Post Activity** also contains a toolbar at the top displaying the logo of the app, and a settings icon.
  <br>
  On clicking the icon, an options menu is displayed. Through this menu, the user can sign-out of MemeNNIT or can edit his profile.
  The user can edit his profile in the **EditProfileActivity**
  <br>
  <br>
 <img src="https://github.com/harshh3010/MemeNNIT/blob/master/AppScreenshots/EditProfileActivity.jpg" width="200">  
 <br>
 <br>
 ### Uploading some memes
 Using bottom navigation bar, the user can navigate to the **PostFragment**. Here, he can can add a new post by choosing an image from his device storage. He can write a suitable caption and then upload this post by clicking on the upload button.
 <br>
 <br>
  <img src="https://github.com/harshh3010/MemeNNIT/blob/master/AppScreenshots/PostFragment.jpg" width="200">
  <br>
  <br>
  In case the user wishes to edit the caption he used, he can always do so in the **Edit Post Dialog**.
  <br>
  <br>
   <img src="https://github.com/harshh3010/MemeNNIT/blob/master/AppScreenshots/EditPostView.jpg" width="200">
<br>
<br>

### Profile Fragments and UploadsActivity
The user can view his profile by navigating to **ProfileFragment1** through navigation bar, and to view the profile of any other user, he has to navigate to **ProfileFragment2**.
<br>
In the profile fragment, the user-info is displayed and from here we can navigate to other activities which display the user's uploads and his achievements(which are not yet added :P).
<br>
<br>
 <img src="https://github.com/harshh3010/MemeNNIT/blob/master/AppScreenshots/ProfileFragment1.jpg" width="200">
    <img src="https://github.com/harshh3010/MemeNNIT/blob/master/AppScreenshots/ProfilePictureView.jpg" width="200">
  <img src="https://github.com/harshh3010/MemeNNIT/blob/master/AppScreenshots/ProfileFragment2.jpg" width="200">
  <br>
  <br>
  The uploads are displayed in the **UploadsActivity**
  <br>
  <br>
     <img src="https://github.com/harshh3010/MemeNNIT/blob/master/AppScreenshots/UploadsActivity.jpg" width="200">
     <br>
     <br>
### Leaderboard Fragment
All the users are alloted a rank on the basis of their score. The users are then displayed rank-wise in the **LeaderboardFragment**
<br>
This fragment also contains a searchview which is used to filter the items present in the **LeaderboardRecyclerView**
<br>
<br>
   <img src="https://github.com/harshh3010/MemeNNIT/blob/master/AppScreenshots/LeaderboardFragment.jpg" width="200">
   ### Notifications
   The users recieve the notification of various activities taking place. These notifications are sent to the users using firebase cloud messaging service. The notifications are first stored in a firestore collection, and then a cloud function is used to read this notification and then send it to user by obtaining the user's **FCM Token**.<br>
   You can find the cloud function here : 
   [MemeNNIT-Notifications](https://github.com/harshh3010/MemeNNIT-Notifications)
   <br>
   The notifications are also listed in **NotificationFragment**, on clicking the notification, the post attached to the notification is displayed and then, the notification gets deleted from the collection.
   <br>
   <br>
   <img src="https://github.com/harshh3010/MemeNNIT/blob/master/AppScreenshots/Notification.jpg" width="200">
   <img src="https://github.com/harshh3010/MemeNNIT/blob/master/AppScreenshots/NotificationFragment.jpg" width="200">
   <img src="https://github.com/harshh3010/MemeNNIT/blob/master/AppScreenshots/PostView.jpg" width="200">
