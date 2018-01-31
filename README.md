My Three Favorite Radio Stations App
=====================================
App will allows to listen to three radio stations. 
For this work device should be connected to the Internet. 
URLs of these radio stations will be stored in database. 
User well see information from database in separate “About app” screen of app and edit these radio stations on the 3rd screen. 
During installation, the application requests access to calls to monitor the status of the phone and turn off the radio during a phone conversation.

<a href="url"><img src="https://github.com/aTasja/MyRadio/raw/master/00pics/splash_screen.png" align="left" height="360" width="190"></a><a href="url"><img src="https://github.com/aTasja/MyRadio/raw/master/00pics/main_screen.png" align="left" height="360" width="190"></a><a href="url"><img src="https://github.com/aTasja/MyRadio/raw/master/00pics/about_screen.png" align="left" height="360" width="190"></a><a href="url"><img src="https://github.com/aTasja/MyRadio/raw/master/00pics/edit_screen.png" align="left" height="360" width="190"></a>  
<br/>  
<br/>  
<br/>  
<br/>  
<br/>  
<br/>  
<br/>  
<br/>  
<br/>  

App will have four screens:
---------------------------

I. After launching, user will have seen splash screen.

II. After splash screen user will have seen main screen with 5 buttons.
Radio 1 - Iskatel 
Radio 2 - Nashe Radio
Radio 3 - Piter FM
/Progress bar is seen only while radio is connecting. /
Exit button
About app and Edit button

III. The 3rd screen displays URLs of current radio stations from database. If user select one of the radio on the screen to edit it, the latest screen will be shown.

IV. The latest screen allows to user enter title and URL of new radio station and save it for the App.

Behaviour:
----------
1. Pushing on radio button starts progress bar, make font of chosen radio red and disable other radio buttons.
2. Some time after user will see toast message that radio is connected and hear radio.
	Progress bar is hidden. All radio buttons are active again. 
3. If the device loses connection with the network, the user will see the message "Internet connection lost!".
	When the connection is restored the appropriate message will be showen and the radio will be reconnected.
4. If user will have pushed on working radio station again - the radio will switch off.
5. If user will have pushed on other radio button par.1-2 will be repeated.
5. Pushing on Exit button stops radio and close app.
6. Pushing on About app and Edit button opens third screen of app with URI information about radio stations, saved in database. 
	Each radio row here is click-able. 
7. If user select radio on previous step, the latest screen will be shown. Here user can enter title ind URL of new radio. 
   After pushing on Save button App will save new radio station on place of selected radio.
   If URL will be invalid an appropliate toast will be shown.
8. While the radio is playing, the application responds to incoming calls. 
   At the moment of an incoming call, the radio stops. After the end of the call or conversation, the radio reconnects.

App firstly connect to three radio stream URLs:
-----------------------------------------------
Radio 1 - http://iskatel.hostingradio.ru:8015/iskatel-128.mp3<br/>
Radio 2 - http://nashe1.hostingradio.ru/nashespb128.mp3<br/>
Radio 3 - http://cdn.radiopiterfm.ru/piterfm<br/>

App have the following structure:
---------------------------------

### ---SplashActivity class extends Activity--- <br/>
- During installation, the application requests access to calls to monitor the status of the phone and turn off the radio during a phone conversation.
- Launches the app and send intent to RadioActivity class to start.<br/>
<br/>

### ---RadioActivity class extends Activity---  <br/>
- Launches user interface with 5 buttons.<br/>
- Handle all button mode changes, including progress bar.<br/>
- Send intent to PlayService class to connect radio.<br/>
- Have BroadcastReceiver (dynamically registered) to receive messages from Service about Radio connecting.<br/>
- Stops service.<br/>
<br/>

### ---PlayService class extends IntentService---  <br/>
- From starting intent gets radio URI and starts MediaPlayer.<br/>
- After connecting to radio station send local broadcast message to RadioActivity.<br/>
<br/>

### ---DataActivity class---  <br/>
- Launches "My Radio - About app" screen.<br/>
- Gets radio titles and URIs from database.<br/>
- Display radio titles and URIs.<br/>
- Pushing on some radio station starts EditActivity.<br/>
<br/>

### ---EditActivity class---  <br/>
- Launches "My Radio - About app - Edit" screen.<br/>
- Allows to enter new title and URL in plase of selected radio station.<br/>
- If URL is valid radio station will be saved in database and DataActivity will starts again.<br/>
- Otherwice user will see appropriate toast message and stay in the same screen.<br/>
<br/>

### ---RadioUtils class---  <br/>
- Have setting and getting methods for radio station.<br/>
<br/>

### ---RadioContract class - with inner class - RadioEntry class extends BaseColumns---  <br/>
- This contract defines the metadata for the App, including the provider's access URIs and its "database" constants.<br/>
<br/>

### ---DBOpenHelper class extends SQLiteOpenHelper---  <br/>
- The database helper used by the Radio Provider to create and manage its underlying SQLite database.<br/>
<br/>

### ---RadioProvider class extends ContentProvider---  <br/>
- Implements a Content Provider used to manage Radio Stations in database.<br/>

This project on GitHub: https://github.com/aTasja/MyRadio

