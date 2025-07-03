# PassMark [PENDING]

TODO - description

icon - https://uxwing.com/shield-lock-line-icon/

## App icon

This is the normal icon which will be displayed on devices with themed icon disabled (to enable, you might be able to
find the setting under personalization or home screen settings)

![PassMark Icon](https://raw.githubusercontent.com/Vaishnav-Kanhirathingal/PassMark/refs/heads/main/app/src/main/res/mipmap-xxxhdpi/ic_launcher.webp)

## Dynamic (Themed) app icons

Themed app icons change colors based on the theme selected in the system. This is a system-wide functionality but adding
support for this is optional. While some apps skip out on this, Passmark has this baked in. These are the themed icons
which will appear on devices with themed icon enabled.

<img src="https://github.com/user-attachments/assets/f5c8f8e5-64c0-4dbd-8631-a5526de00717" alt="themed icon sample" width="88">
<img src="https://github.com/user-attachments/assets/d3f97fa6-88d3-4e7f-a89f-86ee9ac57172" alt="themed icon sample" width="88">
<img src="https://github.com/user-attachments/assets/4850a3a8-61ee-411c-8be7-26c14ae5816f" alt="themed icon sample" width="88">
<img src="https://github.com/user-attachments/assets/837d9fa3-1c46-4da0-b052-18fa93ee86e6" alt="themed icon sample" width="88">
<img src="https://github.com/user-attachments/assets/032c13a7-620f-416e-b7d2-82515136b62f" alt="themed icon sample" width="88">
<img src="https://github.com/user-attachments/assets/dff2801b-5c88-478d-adbf-60fec26649d5" alt="themed icon sample" width="88">
<img src="https://github.com/user-attachments/assets/953b74b8-e9db-47c2-83bb-6b36fc71f548" alt="themed icon sample" width="88">
<img src="https://github.com/user-attachments/assets/44c7e43e-6813-4ace-8156-e3949f174981" alt="themed icon sample" width="88">
<img src="https://github.com/user-attachments/assets/56e4dda4-cd02-4e8d-81df-03d1a833a57b" alt="themed icon sample" width="88">

## Features

Majority of the features are listed below. There may be additional features not listed below.

| Feature                       | Description                                                                                                                                                                                                                                          |
|-------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Master password               | A master password is a password for this service. This password will be used to create an encryption key which will be used for encryption of everything related to the user.                                                                        |
| Local password storage option | The user has the option to store the password either locally or on a remote server. While both are secure, this option gives the user a peace of mind in the sense that an attack on a remote storage won't compromise their security.               |
| Biometric authentication      | Passwords can be locked using biometrics. Meaning, an additional layer of security for viewing or editing the password.                                                                                                                              |
| Password history              | Passwords store their history. Meaning, they store the past passwords you saved in them. This list of previous passwords can be easily viewed when needed from the view password screen.                                                             |
| Grouping/Collection formation | Passwords can be grouped to form collections known as `vaults`. Users can further use these vaults as a filter mechanism or, also to delete all passwords associated with that vault.                                                                |
| Auto lock                     | Auto lock is a feature where the application locks itself after being pushed to the background. This is paired with the `FLAG_SECURE` parameter for the activity. Meaning, the app locks itself while also hiding it's content from the recents tab. |
| Re-encryption                 | If the user decides to change their password, the entire list of passwords is re-encrypted using the key generate from the new password.                                                                                                             |
| Secure activity               | App content is not viewable from the recent page (cannot peak app's content from recent)                                                                                                                                                             |
| Secure Screen                 | When typing passwords, screen recording and screenshots are not allowed for security purposes                                                                                                                                                        |

## Material-You theming

this app implements Material-You theming which means it has dynamic color support for UI elements. To make use of this,
change your phone's system color and the app would match it. Assuming dynamic colors is enabled on device

<img src="https://github.com/user-attachments/assets/08490913-800e-49ff-b1da-c84b816199c1" alt="Empty home screenshot" width="273">
<img src="https://github.com/user-attachments/assets/f020d0de-3e8b-456a-bc26-7540d2bb46a5" alt="Empty home screenshot" width="273">
<img src="https://github.com/user-attachments/assets/49e74c71-9ab7-4362-bb2d-c0e782356f94" alt="Empty home screenshot" width="273">

<img src="https://github.com/user-attachments/assets/b102b9d3-725b-4224-9121-b1585491e03e" alt="Empty home screenshot" width="273">
<img src="https://github.com/user-attachments/assets/b2682e4f-7f1c-422c-baec-fe2b4f2a17e7" alt="Empty home screenshot" width="273">
<img src="https://github.com/user-attachments/assets/3ff6ce6e-e217-48a0-be28-120404d37732" alt="Empty home screenshot" width="273">

## Full Flow -

the below video contains the majority of the actions documented. Refer the documentation section for deeper details

[multiple full flow video with unique themes themes]

> Themes to use (TODO - remove)
> 1. dark_monochrome
> 2. dark_green_blue
> 3. light_yellow_pink
> 4. light_green_blue

> Timeline -
> 1. 00:00 - login...TODO

## Documentation

below is a step-by-step guide for everything

### Authentication (Login screen -> Create master password screen -> Home)

Google login is the only option. click on the login button and select your Google account .depending on your account, if
you are a new user, you would be asked to create a new Master Password. Make sure the password is between 8 and 32
characters long. otherwise it shows an error. If you already had an account, you would be asked to enter your master
password for verification during re-login (mentioned below)

<img src="https://github.com/user-attachments/assets/2f81ecc0-b695-4eb1-bc66-e49ff7563f0b" alt="Create a new user" width="273">
<img src="https://github.com/user-attachments/assets/b5401c16-cb49-4fb4-baf3-91e6676f07ba" alt="Error screen in create a new user" width="273">
<img src="https://github.com/user-attachments/assets/58375ab7-6189-4398-b47c-fd5e22f53abc" alt="Login for existing user" width="273">

> 1. New user creation
> 2. Error screen
> 3. Login for existing user

### Create/Update/Delete a vault

A vault is where you store a collection of passwords. Creation steps are given below. To update a vault, just long press
it to open its update dialog. The update dialog also contains the delete button. You can have a total of 4 additional
vaults.

<img src="https://github.com/user-attachments/assets/be807579-b2e9-4a6e-957b-5067dc0884a2" alt="Create a vault" width="273">
<img src="https://github.com/user-attachments/assets/9d5c178f-625a-4ec4-b5a4-714ff493598a" alt="Update a vault" width="273">
<img src="https://github.com/user-attachments/assets/ac6a3703-1ac5-4011-8d17-ad0942bcc994" alt="Delete a vault" width="273">

> 1. Create a new vault
> 2. Update an existing vault
> 3. Delete a vault.

### Create a password (Home screen -> Crete new password screen -> save)

Once Authentication is completed, you would be taken to the home screen. Now, you can create a new password by using the
create-new-password `+` floating action button. Create a new password by adding necessary details. Title and password
are compulsory details. The last 2 options are security features. Warnings might occur if details are improper/
incomplete. In that case once all warnings are sorted, click the save
button.

> Passmark allows you to do 2 additional things, `require fingerprint authentication` to copy your password and second
> is a `keep on device` only option. This way the current password is saved locally only. It would survive logouts but
> not app resets and uninstalls as they aren't stored remotely. This is a security feature for users who might want to
> store certain details locally only as that would certainly be helpful in avoiding cyber-attacks.

<img src="https://github.com/user-attachments/assets/6d1461b5-9d59-41d9-a380-eff526e43b30" alt="Creating a password" width="273">
<img src="https://github.com/user-attachments/assets/a555236e-acde-4761-9e15-d5dd10ed956e" alt="Error screen in creation of a password" width="273">
<img src="https://github.com/user-attachments/assets/41f9aacb-1b16-4aa4-8345-4d07396bbf22" alt="Updating a password" width="273">

> 1. Creation of a Password,
> 2. Error screen when title is empty, password is empty or email is of incorrect format.
> 3. Updating a password (fingerprint will be required for editing if `use fingerprint` is enabled for the selected
     password)

### Difference between a remote and locally stored password -

<img src="https://github.com/user-attachments/assets/daba80cd-10d7-449d-9541-516ffd5c5812" alt="remote" width="820">
<img src="https://github.com/user-attachments/assets/a9b95013-95b4-4e67-b256-73203996f932" alt="local" width="820">

> 1. Password stored remotely has no border.
> 2. Password stored locally has a border.

### View a password -

This screen contains the password details and a history of previous passwords that can be helpful in recovery. Viewing
this screen triggers the update of the `last used` property of the password

<img src="https://github.com/user-attachments/assets/c2957783-ec75-4cdf-b330-2f80df60ae5e" alt="fingerprint first" width="273">
<img src="https://github.com/user-attachments/assets/c3349ed6-71eb-4ecd-a45f-6228c122eb34" alt="history first" width="273">
<img src="https://github.com/user-attachments/assets/7da79759-ac41-4781-91fb-17285e0ddfb9" alt="no biometrics view & delete" width="273">

> 1. Flow of the screen where the password is viewed first.
> 2. Flow of the screen where the password history is viewed first. In both cases, if fingerprint is verified once,
     reverification isn't required for subsequent action which requires fingerprint access.
> 3. Flow of screen where fingerprint access isn't required.

### Sorting, Searching & Filtering -

You can see contents of a selected vault if you select the vault's card. Sorting options are provided on home screen as
displayed below. Searching has also been implemented as usual.

<img src="https://github.com/user-attachments/assets/491453ea-de68-4500-9806-b0759e0e619d" alt="Sorting" width="273">
<img src="https://github.com/user-attachments/assets/14304e6c-164e-48ff-ba2d-ff6f7279f492" alt="Searching" width="273">
<img src="https://github.com/user-attachments/assets/e1caf762-1430-4999-bbcc-472057f730ba" alt="Selecting a vault" width="273">

> 1. Sorting using name (Sort preferences are saved locally to avoid resetting.)
> 2. Searching
> 3. Filtering using a vault.

### Auto lock

The app auto locks itself if pushed to recents. The locking has a delay set to ensure it wasn't user error (accidentally
exiting the app just to re-enter instantly)

<img src="https://github.com/user-attachments/assets/392ca41a-a4d4-4a72-9349-739108cc9e24" alt="show auto lock screen unlocking via fingerprint" width="410">
<img src="https://github.com/user-attachments/assets/38d400ff-08aa-4f27-ab35-11df6891251a" alt="show auto lock screen unlocking via password" width="410">

> 1. Show auto lock screen unlocking via fingerprint.
> 2. show auto lock screen unlocking via password.

### Settings

The settings screen contains some preferences and some options. The `enable fingerprint by default` enables fingerprint
protection option for all passwords by default during creation. Same is true for `Enable offline storage by default`.
Rest is self-explanatory. Logout option safely logs you out of your account without destroying your local passwords in
the process. Reset account option can be used to destroy everything associated with the user including local passwords.
This is a self retrying function. So, it will retry the step where an error occurs. Example showcased in the error
section below.

<img src="https://github.com/user-attachments/assets/b2ce209c-548f-44cd-80aa-b50c9778f07f" alt="settings screen nav and switches and logout" width="273">
<img src="https://github.com/user-attachments/assets/57937c31-6fcc-4cf0-9e24-6a8ac8121dc6" alt="change password gif" width="273">
<img src="https://github.com/user-attachments/assets/cda0ab53-7e59-4fbe-bf5c-0a3f9ed59527" alt="reset user git" width="273">

> 1. Switches and logout
> 2. Change Password
> 3. Reset User

## Error screens

Error screens are necessary to handle error states. Each screen has an error UI of some sort to manage errors.

### Error Toasters

Actions such as creating a `Vault` or `Password` are simple and do not require a separate UI composable. Hence, such
actions just display a toast when an error is encountered.

<img src="https://github.com/user-attachments/assets/59158580-a951-422a-b8f1-17c4d98f3f4c" alt="wrong password toaster" width="273">
<img src="https://github.com/user-attachments/assets/5b9a5901-0439-4ec3-a8a1-8112fe320565" alt="create vault toaster" width="273">
<img src="https://github.com/user-attachments/assets/e16cdcb1-b3e0-4f40-96a6-8d094f64e0d7" alt="create password toaster" width="273">

### Full Screen Error

Actions like authenticating user in loading screen, loading the passwords and vaults from the api are full screen
actions and do require a separate UI. here, They are given that separate UI.

<img src="https://github.com/user-attachments/assets/3ec1ae88-ad29-44d1-9eb7-1719f7cf7312" alt="Authentication error" width="410">
<img src="https://github.com/user-attachments/assets/97b60a73-9315-4af8-8c7b-3d13ba9ae0b3" alt="Password loading error" width="410">

> 1. Authentication error
> 2. Password loading error

### Sequential looping error

Actions like `changing user password` and `resetting a user` are sequential in nature and cannot proceed to their next
stage if an error occurs. In such a case, it would go into a loop of retrying until the entire task list is completed.

<img src="https://github.com/user-attachments/assets/d831cd38-b9ee-40e7-a896-cf26e21f0c89" alt="change password looping error" width="410">
<img src="https://github.com/user-attachments/assets/55af2d3e-35e3-455f-8d32-53380e0ae636" alt="reset user looping error" width="410">

> 1. Error occurred while trying to change password. Will keep retrying in a loop with a delay.
> 2. Error occurred while trying to reset user account. Will keep retrying in a loop with a delay.

## Easter eggs [PENDING]

> TODO
