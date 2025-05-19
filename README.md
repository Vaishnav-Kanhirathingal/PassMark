# PassMark [PENDING]

![PassMark Icon](https://raw.githubusercontent.com/Vaishnav-Kanhirathingal/PassMark/refs/heads/main/app/src/main/res/mipmap-xxxhdpi/ic_launcher.webp)

## Info

- icon - https://uxwing.com/shield-lock-line-icon/

## Why [PENDING - explain in detail]

- local password and biometric authentication
- auto lock

## Material-You theming

this app implements Material-You theming which means it has dynamic color support for UI elements. To make use of this,
change your phone's system color and the app would match it. Assuming dynamic colors is enables on device

<img src="https://github.com/user-attachments/assets/08490913-800e-49ff-b1da-c84b816199c1" alt="Empty home screenshot" width="273">
<img src="https://github.com/user-attachments/assets/f020d0de-3e8b-456a-bc26-7540d2bb46a5" alt="Empty home screenshot" width="273">
<img src="https://github.com/user-attachments/assets/49e74c71-9ab7-4362-bb2d-c0e782356f94" alt="Empty home screenshot" width="273">

<img src="https://github.com/user-attachments/assets/b102b9d3-725b-4224-9121-b1585491e03e" alt="Empty home screenshot" width="273">
<img src="https://github.com/user-attachments/assets/b2682e4f-7f1c-422c-baec-fe2b4f2a17e7" alt="Empty home screenshot" width="273">
<img src="https://github.com/user-attachments/assets/3ff6ce6e-e217-48a0-be28-120404d37732" alt="Empty home screenshot" width="273">

## Documentation

below is a step by step guide for everything

### Authentication (Login screen)

Google login is the only option. click on the login button and select your Google account

[google login screen]

### Authentication (Create master password screen)

depending on your account, if you are a new user, you would be asked to create a new Master Password. Make sure the
password is between 8 and 32 characters long. otherwise it shows an error

[screen with error]

[solve error and create password]

If you already had an account, you would be asked to enter your master password for verification during re-login (
mentioned below)

### Authentication (Enter master password screen)

during Re-Login, you would be asked to re-enter your master password for password verification and storage.

[enter master password screen]

### Home screen

Once Authentication is completed, you would be taken to the home screen.

[empty home screen]

Now, you can create a new password by using the create-new-password `+` floating action button.

### Create new password

Create a new password by adding necessary details. Title and password are compulsory details. The last 2 options are
security features. Passmark allows you to do 2 additional things. require fingerprint authentication to copy your
password and second is a keep on device only option. This way the current password is saved locally only. It would
survive logouts but not app resets and uninstalls as they aren't stored remotely. This is a security feature for users
who might want to store certain details locally only as that would certainly be helpful in avoiding cyber attacks.

[show top error image]

once all warnings are sorted, click the save button

[click save button image]

you can further organize your passwords by sorting them into different vaults, similar to folders. To create a new
vault, go to home screen and open the navigation drawer (side menu) there click on the `+` button to create a new vault

### Create new vault

In this screen, set a name and an icon for the vault and click create

[saving Vault GIF]

In case there was an error, and you want to update or delete a vault, long press the appropriate vault's button. this
would reopen the vault dialog with new options.

[update vault GIF]

[delete vault GIF]

### Selecting a vault

Select a vault from navigation drawer to only show it's list.

[GIF selecting a vault]

you can move a password to a vault or create a password predefining a vault

[GIF showing selection of vault and saving]

the app also shows a warning if no vaults are created

[gif showing no vault warning]

### Search and sort

search and sort can be easily performed on the password list provided. However, it does take into account which vault is
selected.

[gif showing search]

sort preferences are saved locally to avoid resetting

[gif showing sort]

### Auto lock

The app auto locks itself if pushed to recents. The locking has a delay set to ensure it wasn't user error (accidentally
exiting the app just to re-enter instantly)

[show auto lock screen unlocking via fingerprint]

[show auto lock screen unlocking via password]

### Settings

The settings screen contains some preferences and some options.

[settings screen]

the `enable fingerprint by default` enables fingerprint protection option for all passwords by default during creation.
Same is true for `Enable offline storage by default`. Change Password button can be used to change your password.

[change password gif]

this is a self retrying function. So, it will retry the step where an error occurs.

[change password gif retrying]

Logout option logs you out of your account without destroying your local passwords in the process.

[logout user git]

While reset account
option can be used to destroy everything associated with the user including local passwords.

[reset user git]

## Error screens

Error screens are necessary to handle error states. Each screen has an error UI to manage errors.

### Authentication error

### Home error

## Easter eggs [PENDING]
