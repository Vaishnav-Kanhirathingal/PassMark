package easter.egg.passmark.data

data class TestPasswordData(
    val vault: String?,
    val title: String,
    val email: String?,
    val userName: String?,
    val password: String,
    val website: String?,
    val note: String?,
    val useFingerprint: Boolean,
    val useLocalStorage: Boolean = false
) {
    companion object {
        private const val EMAIL = "john.doe@gmail.com"
        private const val WORK_EMAIL = "john.doe@SomeCompany.co"
        private const val NOTE = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. " +
                "Nullam feugiat lorem magna, in auctor urna molestie ut. Donec venenatis tortor " +
                "in elit scelerisque congue venenatis quis ligula. "

        val testList = listOf(
            TestPasswordData(
                vault = null,
                title = "Google",
                email = EMAIL,
                userName = "John D",
                password = "Google123",
                website = "www.google.com",
                note = NOTE,
                useFingerprint = true,
            ),
            TestPasswordData(
                vault = TestVault.SOCIAL_MEDIA_VAULT,
                title = "FaceBook",
                email = EMAIL,
                userName = "john_d",
                password = "FaceBook123",
                website = "www.facebook.com",
                note = NOTE,
                useFingerprint = false,
            ),
            TestPasswordData(
                vault = TestVault.SOCIAL_MEDIA_VAULT,
                title = "Instagram",
                email = EMAIL,
                userName = "j_doe",
                password = "insta123",
                website = "www.instagram.com",
                note = NOTE,
                useFingerprint = false,
            ),
            TestPasswordData(
                vault = TestVault.WORK_VAULT,
                title = "LinkedIn",
                email = EMAIL,
                userName = "John Marksman Doe",
                password = "insta123",
                website = "www.linkedin.com",
                note = NOTE,
                useFingerprint = true,
            ),
            TestPasswordData(
                vault = TestVault.WORK_VAULT,
                title = "Git-Hub",
                email = EMAIL,
                userName = "j_d_112",
                password = "git123",
                website = "www.github.com",
                note = NOTE,
                useFingerprint = true,
            ),
            TestPasswordData(
                vault = TestVault.WORK_VAULT,
                title = "Email",
                email = WORK_EMAIL,
                userName = "Dr. John Doe",
                password = "Work123",
                website = "www.outlook.com",
                note = NOTE,
                useFingerprint = true,
            ),
            TestPasswordData(
                vault = null,
                title = "Spotify",
                email = EMAIL,
                userName = "jd",
                password = "spoty_123",
                website = "www.spotify.com",
                note = NOTE,
                useFingerprint = false,
            ),
            TestPasswordData(
                vault = TestVault.FINANCE_VAULT,
                title = "RBI",
                email = EMAIL,
                userName = "Mr. John Doe",
                password = "fin123",
                website = "www.rbi.org",
                note = NOTE,
                useFingerprint = true,
            ),
        )
    }
}