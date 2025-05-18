# PassMark [PENDING]

## Info

- icon - https://uxwing.com/shield-lock-line-icon/

## Why

- TODO - talk about special features

### Authentication (Login screen)

Google login is the only option. click on the login button and select your google account

### Authentication (Create/Enter master password screen)

depending on your account, if you are a new user, you would be asked to create a new Master Password

[Image]

If you already had an account, you would be asked to enter your master password for verification

[Image]

### Home screen

Once Authentication is completed, you would be taken to the home screen.

[empty home screen]

Now, you can create a new password by using the create new password `+` floating action button.

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

In case there was an error and you want to update or delete a vault, long press the appropriate vault's button. this
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

[show auto lock screen]

### Settings

The settings screen contains some preferences and some options. the `enable fingerprint by default` enables fingerprint
protection option for all passwords by default during creation. Same is true for `Enable offline storage by default`.
Change Password button can be used to change your password.

[change password gif]

Logout option logs you out of your account without destroying your local passwords in the process.

[logout user git]

While reset account
option can be used to destroy everything associated with the user including local passwords.

[reset user git]