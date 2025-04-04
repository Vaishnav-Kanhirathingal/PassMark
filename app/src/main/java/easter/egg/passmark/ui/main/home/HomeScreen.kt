package easter.egg.passmark.ui.main.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import easter.egg.passmark.R
import easter.egg.passmark.data.supabase.account.SupabaseAccountHelper
import easter.egg.passmark.data.supabase.api.PasswordApi
import easter.egg.passmark.data.supabase.api.UserApi
import easter.egg.passmark.data.supabase.api.VaultApi
import easter.egg.passmark.di.supabase.SupabaseModule
import easter.egg.passmark.ui.main.HomeListingData
import easter.egg.passmark.ui.main.MainViewModel
import easter.egg.passmark.ui.main.home.screens.HomeDrawer
import easter.egg.passmark.utils.ScreenState
import easter.egg.passmark.utils.annotation.MobileHorizontalPreview
import easter.egg.passmark.utils.annotation.MobilePreview
import easter.egg.passmark.utils.values.PassMarkDimensions
import easter.egg.passmark.utils.values.PassMarkFonts
import easter.egg.passmark.utils.values.setSizeLimitation
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object HomeScreen {
    @Composable
    fun Screen(
        modifier: Modifier,
        toAddNewPasswordScreen: () -> Unit,
        mainViewModel: MainViewModel
    ) {
        val context = LocalContext.current
        when (val screenState = mainViewModel.screenState.collectAsState().value) {
            is ScreenState.PreCall, is ScreenState.Loading -> Box(
                modifier = modifier,
                contentAlignment = Alignment.Center,
                content = {
                    CircularProgressIndicator(modifier = Modifier.setSizeLimitation())
                }
            )

            is ScreenState.Loaded -> MainScreen(
                modifier = modifier,
                toAddNewPasswordScreen = toAddNewPasswordScreen
            )

            is ScreenState.ApiError -> {
                screenState.manageToastActions(context = context)
                ErrorScreen(
                    modifier = modifier,
                    errorState = screenState,
                    onRetry = { mainViewModel.refreshHomeList() }
                )
            }
        }
    }

    @Composable
    fun ErrorScreen(
        modifier: Modifier,
        errorState: ScreenState.ApiError<HomeListingData>,
        onRetry: () -> Unit
    ) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            content = {
                Icon(
                    modifier = Modifier.size(100.dp),
                    imageVector = Icons.Default.Warning,
                    contentDescription = null
                )
                Text(
                    fontFamily = PassMarkFonts.font,
                    fontSize = PassMarkFonts.Headline.medium,
                    fontWeight = FontWeight.SemiBold,
                    text = when (errorState) {
                        is ScreenState.ApiError.NetworkError -> "No Internet"
                        is ScreenState.ApiError.SomethingWentWrong -> "Something Went Wrong"
                    }
                )
                Text(
                    fontFamily = PassMarkFonts.font,
                    fontSize = PassMarkFonts.Body.medium,
                    fontWeight = FontWeight.Medium,
                    text = when (errorState) {
                        is ScreenState.ApiError.NetworkError -> "Can't connect to network"
                        is ScreenState.ApiError.SomethingWentWrong -> "Give it another try"
                    }
                )
                Spacer(modifier = Modifier.height(height = 4.dp))
                Button(
                    modifier = Modifier.setSizeLimitation(),
                    onClick = onRetry,
                    content = { Text(text = "Retry") }
                )
            }
        )
    }

    @Composable
    private fun MainScreen(
        modifier: Modifier,
        toAddNewPasswordScreen: () -> Unit,
//        mainViewModel: MainViewModel
    ) {
        val coroutineScope = rememberCoroutineScope()
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        ModalNavigationDrawer(
            modifier = modifier,
            drawerState = drawerState,
            drawerContent = {
                // TODO: fix the width limitation problem
                HomeDrawer.DrawerContent(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(fraction = 0.7f)
                )
            },
            content = {
                val searchText: MutableState<String?> = remember { mutableStateOf(null) }
                Scaffold(
                    modifier = modifier,
                    topBar = {
                        HomeTopBar(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = PassMarkDimensions.minTouchSize),
                            searchText = searchText.value,
                            onSearch = { searchText.value = it },
                            openNavigationDrawer = { coroutineScope.launch { drawerState.open() } }
                        )
                    },
                    content = {
                        HomeContent(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues = it)
                        )
                    },
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = toAddNewPasswordScreen,
                            content = {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null
                                )
                            }
                        )
                    }
                )
            }
        )
    }

    @Composable
    private fun HomeTopBar(
        modifier: Modifier,
        searchText: String?,
        onSearch: (String?) -> Unit,
        openNavigationDrawer: () -> Unit
    ) {
        Row(
            modifier = modifier.padding(
                top = 8.dp,
                bottom = 8.dp,
                start = 16.dp,
                end = 4.dp
            ),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            content = {
                val componentHeight = PassMarkDimensions.minTouchSize
                val focusRequester = remember { FocusRequester() }
                val keyboardController = LocalSoftwareKeyboardController.current
                val coroutineScope = rememberCoroutineScope()
                if (searchText == null) {
                    Box(
                        modifier = Modifier
                            .size(size = componentHeight)
                            .clip(shape = CircleShape)
                            .background(color = MaterialTheme.colorScheme.primaryContainer)
                            .clickable(onClick = openNavigationDrawer),
                        contentAlignment = Alignment.Center,
                        content = {
                            Image(
                                painter = painterResource(id = R.drawable.ic_monochrome),
                                contentDescription = null,
                                colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onPrimaryContainer)
                            )
                        }
                    )
                    Row(
                        modifier = Modifier
                            .height(height = componentHeight)
                            .weight(1f)
                            .padding(horizontal = 8.dp)
                            .clip(shape = RoundedCornerShape(size = 16.dp))
                            .background(color = MaterialTheme.colorScheme.surfaceContainerHigh)
                            .padding(horizontal = 12.dp)
                            .clickable {
                                onSearch("")
                                coroutineScope.launch {
                                    delay(100)
                                    try {
                                        focusRequester.requestFocus()
                                        keyboardController?.show()
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }
                            },
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        content = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null
                            )
                            Text(
                                modifier = Modifier.weight(1f),
                                text = "Search passwords"
                            )
                        }
                    )
                } else {
                    IconButton(
                        modifier = Modifier.size(
                            width = componentHeight,
                            height = componentHeight
                        ),
                        onClick = { onSearch(null) },
                        content = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null
                            )
                        }
                    )
                    BasicTextField(
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = PassMarkDimensions.minTouchSize)
                            .focusRequester(focusRequester = focusRequester),
                        value = searchText,
                        onValueChange = onSearch,
                        singleLine = true,
                        maxLines = 1,
                        textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface),
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurfaceVariant),
                        decorationBox = { text ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = PassMarkDimensions.minTouchSize)
                                    .padding(horizontal = 8.dp),
                                contentAlignment = Alignment.CenterStart,
                                content = {
                                    if (searchText.isEmpty()) {
                                        Text(
                                            text = "Search in all items...",
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    text()
                                }
                            )
                        }
                    )
                }
                IconButton(
                    modifier = Modifier.size(
                        width = PassMarkDimensions.minTouchSize,
                        height = componentHeight
                    ),
                    onClick = { TODO() },
                    content = {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = null
                        )
                    }
                )
            }
        )
    }

    @Composable
    private fun HomeContent(modifier: Modifier) {
        Column(
            modifier = modifier,
            content = {
                // TODO: pending
            }
        )
    }
}

@Composable
@MobilePreview
@MobileHorizontalPreview
private fun HomeScreenPreview() {
    val supabaseClient = SupabaseModule.mockClient
    HomeScreen.Screen(
        modifier = Modifier.fillMaxSize(),
        toAddNewPasswordScreen = {},
        mainViewModel = MainViewModel(
            context = LocalContext.current,
            supabaseAccountHelper = SupabaseAccountHelper(supabaseClient),
            userApi = UserApi(supabaseClient),
            vaultApi = VaultApi(supabaseClient),
            passwordApi = PasswordApi(supabaseClient)
        )
    )
}

@Composable
@MobilePreview
@MobileHorizontalPreview
private fun HomeErrorScreenPreview() {
    HomeScreen.ErrorScreen(
        modifier = Modifier.fillMaxSize(),
        errorState = ScreenState.ApiError.NetworkError(),
        onRetry = {}
    )
}