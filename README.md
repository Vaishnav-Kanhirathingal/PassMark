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

## Full Flow -

the below video contains majority of the actions documented. Refer the documentation section for deeper details

Timeline -

> TODO

## Documentation

below is a step-by-step guide for everything



### Authentication (Login screen -> Create master password screen -> Home)

Google login is the only option. click on the login button and select your Google account .depending on your account, if
you are a new user, you would be asked to create a new Master Password. Make sure the password is between 8 and 32
characters long. otherwise it shows an error. If you already had an account, you would be asked to enter your master
password for verification during re-login (mentioned below)

[google login Process GIF for new user]
 
[google login Process GIF for new user with error]

[google login Process GIF for already existing user]

![LoginForExisting](https://github.com/user-attachments/assets/58375ab7-6189-4398-b47c-fd5e22f53abc)




> Description - TODO



### Create/Update/Delete a vault

A vault is where you store a collection of passwords. Creation steps are given below. To update a vault, just long press
it to open its update dialog. The update dialog also contains the delete button. You can have a total of 4 additional
vaults.


<img src="https://github.com/user-attachments/assets/be807579-b2e9-4a6e-957b-5067dc0884a2" alt="Empty home screenshot" width="273">
<img src="https://github.com/user-attachments/assets/9d5c178f-625a-4ec4-b5a4-714ff493598a" alt="Empty home screenshot" width="273">
<img src="https://github.com/user-attachments/assets/ac6a3703-1ac5-4011-8d17-ad0942bcc994" alt="Empty home screenshot" width="273">

> Description - TODO


### Create a password (Home screen -> Crete new password screen -> save)

Once Authentication is completed, you would be taken to the home screen. Now, you can create a new password by using the
create-new-password `+` floating action button. Create a new password by adding necessary details. Title and password
are compulsory details. The last 2 options are security features. Warnings might occur if details are improper/ incomplete. In that case once all warnings are sorted, click the save
button.

![creatingPassword](https://github.com/user-attachments/assets/6d1461b5-9d59-41d9-a380-eff526e43b30)

![creatingPasswordError](https://github.com/user-attachments/assets/a555236e-acde-4761-9e15-d5dd10ed956e)

![updatePassword](https://github.com/user-attachments/assets/41f9aacb-1b16-4aa4-8345-4d07396bbf22)



[empty home screen -> create new password screen -> select vault drop down -> back and fill details -> save -> home (GIF)]

[password screen wih error -> solve error -> home]

[update flow]

> Passmark allows you to do 2 additional things, `require
fingerprint authentication` to copy your password and second is a `keep on device` only option. This way the current
password is saved locally only. It would survive logouts but not app resets and uninstalls as they aren't stored
remotely. This is a security feature for users who might want to store certain details locally only as that would
certainly be helpful in avoiding cyber-attacks.


### View a password -

this screen contains the password details and a history of previous passwords that can be helpful in recovery.

[password view screen using fingerprint - view password history, delete]

[password view screen not using fingerprint - view password history, delete]




### Filtering, Sorting & Searching -

You can see contents of a selected vault if you select the vault's card. Sorting options are provided on home screen as
displayed below. Sort preferences are saved locally to avoid resetting. Searching has also been implemented as usual.

[home -> nav drawer -> select a vault -> Home(auto)]

[home -> sort]

[home -> search]

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
