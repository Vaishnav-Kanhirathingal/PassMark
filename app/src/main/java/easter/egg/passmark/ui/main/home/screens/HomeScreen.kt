package easter.egg.passmark.ui.main.home.screens

import android.util.Log
import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.google.gson.GsonBuilder
import easter.egg.passmark.R
import easter.egg.passmark.data.models.content.Password
import easter.egg.passmark.data.models.content.PasswordSortingOptions
import easter.egg.passmark.ui.main.HomeListData
import easter.egg.passmark.ui.main.MainViewModel
import easter.egg.passmark.ui.main.home.HomeViewModel
import easter.egg.passmark.ui.shared_components.CustomLoader
import easter.egg.passmark.utils.ScreenState
import easter.egg.passmark.utils.annotation.MobileHorizontalPreview
import easter.egg.passmark.utils.annotation.MobilePreview
import easter.egg.passmark.utils.values.PassMarkDimensions
import easter.egg.passmark.utils.values.PassMarkFonts
import easter.egg.passmark.utils.values.setSizeLimitation
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object HomeScreen {
    private val TAG = this::class.simpleName

    // TODO: fingerprint screen
    @Composable
    fun Screen(
        modifier: Modifier,
        toPasswordEditScreen: (password: Password?) -> Unit,
        mainViewModel: MainViewModel,
        toViewPasswordScreen: (password: Password) -> Unit,
        homeViewModel: HomeViewModel,
        toSettingsScreen: () -> Unit
    ) {
        BackHandler(
            enabled = homeViewModel.searchText.collectAsState().value != null,
            onBack = { homeViewModel.updateSearchText(str = null) }
        )
        val context = LocalContext.current
        when (val screenState = mainViewModel.screenState.collectAsState().value) {
            is ScreenState.PreCall, is ScreenState.Loading -> Box(
                modifier = modifier,
                contentAlignment = Alignment.Center,
                content = { CustomLoader.FullScreenLoader(modifier = Modifier) }
            )

            is ScreenState.Loaded -> {
                Log.d(TAG, GsonBuilder().setPrettyPrinting().create().toJson(screenState.result))
                MainScreen(
                    modifier = modifier,
                    toPasswordEditScreen = toPasswordEditScreen,
                    mainViewModel = mainViewModel,
                    toViewPasswordScreen = toViewPasswordScreen,
                    homeViewModel = homeViewModel,
                    toSettingsScreen = toSettingsScreen
                )
            }

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
        errorState: ScreenState.ApiError<HomeListData>,
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

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun MainScreen(
        modifier: Modifier,
        toPasswordEditScreen: (password: Password?) -> Unit,
        mainViewModel: MainViewModel,
        toViewPasswordScreen: (password: Password) -> Unit,
        homeViewModel: HomeViewModel,
        toSettingsScreen: () -> Unit
    ) {
        val coroutineScope = rememberCoroutineScope()
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val mainResultState = mainViewModel.screenState.collectAsState()
        ModalNavigationDrawer(
            modifier = modifier,
            drawerState = drawerState,
            drawerContent = {
                HomeDrawer.DrawerContent(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(fraction = 0.7f),
                    viewModel = homeViewModel,
                    mainViewModel = mainViewModel,
                    selectVault = {
                        homeViewModel.updateVaultIdSelected(id = it)
                        coroutineScope.launch { drawerState.close() }
                    },
                    toSettingsScreen = toSettingsScreen
                )
            },
            content = {
                Scaffold(
                    modifier = modifier,
                    topBar = {
                        HomeTopBar(
                            modifier = Modifier
                                .fillMaxWidth()
                                .setSizeLimitation(),
                            searchingVaultName = homeViewModel.vaultIdSelected.collectAsState().value?.let {
                                (mainResultState.value as? ScreenState.Loaded)
                                    ?.result?.getVaultById(it)
                                    ?.name
                            },
                            searchText = homeViewModel.searchText.collectAsState().value,
                            onSearch = homeViewModel::updateSearchText,
                            openNavigationDrawer = { coroutineScope.launch { drawerState.open() } },
                            sortingOptionsSelected = homeViewModel.passwordSortingOption.collectAsState().value,
                            selectPasswordSortingOption = homeViewModel::updatePasswordSortingOption,
                            isIncreasing = homeViewModel.increasingOrder.collectAsState().value,
                            setIncreasing = homeViewModel::updateIncreasingOrder
                        )
                    },
                    content = {
                        val isRefreshing = remember { mutableStateOf(false) }
                        LaunchedEffect(
                            key1 = mainResultState.value,
                            block = {
                                if (mainResultState.value is ScreenState.Loaded) {
                                    isRefreshing.value = false
                                }
                            }
                        )
                        PullToRefreshBox(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues = it),
                            isRefreshing = isRefreshing.value,
                            onRefresh = {
                                isRefreshing.value = true
                                mainViewModel.refreshHomeList(silentReload = true)
                            },
                            content = {
                                HomeContent.HomeContent(
                                    modifier = Modifier
                                        .fillMaxSize(),
                                    mainViewModel = mainViewModel,
                                    homeViewModel = homeViewModel,
                                    toViewPasswordScreen = toViewPasswordScreen,
                                    toPasswordEditScreen = toPasswordEditScreen
                                )
                            }
                        )
                    },
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = { toPasswordEditScreen(null) },
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
        searchingVaultName: String?,
        onSearch: (String?) -> Unit,
        openNavigationDrawer: () -> Unit,
        sortingOptionsSelected: PasswordSortingOptions,
        selectPasswordSortingOption: (PasswordSortingOptions) -> Unit,
        isIncreasing: Boolean,
        setIncreasing: (Boolean) -> Unit
    ) {
        fun getSortingIcon(isInc: Boolean) =
            if (isInc) Icons.Default.KeyboardArrowDown
            else Icons.Default.KeyboardArrowUp


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
                            }
                            .padding(horizontal = 12.dp),
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
                    Box(
                        modifier = Modifier
                            .size(size = componentHeight)
                            .clip(shape = CircleShape)
                            .background(color = MaterialTheme.colorScheme.primaryContainer)
                            .clickable(onClick = { onSearch(null) }),
                        contentAlignment = Alignment.Center,
                        content = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
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
                                    .padding(horizontal = 16.dp),
                                contentAlignment = Alignment.CenterStart,
                                content = {
                                    if (searchText.isEmpty()) {

                                        Text(
                                            text = "Search in ${searchingVaultName ?: "all items"}...",
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    text()
                                }
                            )
                        }
                    )
                }

                val showSortMenu = remember { mutableStateOf(false) }

                ConstraintLayout(
                    modifier = Modifier
                        .size(size = componentHeight)
                        .clip(shape = CircleShape)
                        .clickable(onClick = { showSortMenu.value = true }),
                    content = {
                        val (mainIcon, statusIcon) = createRefs()

                        Icon(
                            modifier = Modifier.constrainAs(
                                ref = mainIcon,
                                constrainBlock = {
                                    this.top.linkTo(parent.top)
                                    this.bottom.linkTo(parent.bottom)
                                    this.start.linkTo(parent.start)
                                    this.end.linkTo(parent.end)
                                }
                            ),
                            imageVector = Icons.AutoMirrored.Filled.Sort,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                        Icon(
                            modifier = Modifier
                                .size(size = 12.dp)
                                .constrainAs(
                                    ref = statusIcon,
                                    constrainBlock = {
                                        this.end.linkTo(mainIcon.end)
                                        this.bottom.linkTo(mainIcon.bottom)
                                    }
                                ),
                            imageVector = getSortingIcon(isInc = isIncreasing),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                        DropdownMenu(
                            expanded = showSortMenu.value,
                            onDismissRequest = { showSortMenu.value = false },
                            shape = RoundedCornerShape(size = 12.dp),
                            content = {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(
                                            start = 8.dp,
                                            end = 8.dp,
                                            bottom = 8.dp
                                        )
                                        .clip(RoundedCornerShape(size = 8.dp))
                                        .background(color = MaterialTheme.colorScheme.surfaceContainerHigh),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically,
                                    content = {
                                        @Composable
                                        fun SortButton(forAscending: Boolean) {
                                            Box(
                                                modifier = Modifier
                                                    .setSizeLimitation()
                                                    .weight(1f)
                                                    .background(
                                                        color =
                                                            if (isIncreasing == forAscending) MaterialTheme.colorScheme.primaryContainer
                                                            else Color.Transparent
                                                    )
                                                    .clickable(
                                                        onClick = {
                                                            setIncreasing(forAscending)
                                                            showSortMenu.value = false
                                                        }
                                                    ),
                                                contentAlignment = Alignment.Center,
                                                content = {
                                                    Icon(
                                                        imageVector = getSortingIcon(isInc = forAscending),
                                                        contentDescription = null,
                                                        tint =
                                                            if (isIncreasing == forAscending) MaterialTheme.colorScheme.onPrimaryContainer
                                                            else MaterialTheme.colorScheme.onSurface
                                                    )
                                                }
                                            )
                                        }
                                        SortButton(forAscending = false)
                                        SortButton(forAscending = true)
                                    }
                                )
                                PasswordSortingOptions.entries.forEach { passwordSortingOptions: PasswordSortingOptions ->
                                    DropdownMenuItem(
                                        text = { Text(text = passwordSortingOptions.getMenuDisplayText()) },
                                        onClick = {
                                            selectPasswordSortingOption(passwordSortingOptions)
                                            showSortMenu.value = false
                                        },
                                        leadingIcon = {
                                            Icon(
                                                imageVector =
                                                    if (passwordSortingOptions == sortingOptionsSelected) Icons.Default.RadioButtonChecked
                                                    else Icons.Default.RadioButtonUnchecked,
                                                contentDescription = null
                                            )
                                        }
                                    )
                                }
                            }
                        )
                    }
                )
            }
        )
    }
}

@Composable
@MobilePreview
@MobileHorizontalPreview
private fun HomeScreenPreview() {
    HomeScreen.Screen(
        modifier = Modifier.fillMaxSize(),
        toPasswordEditScreen = {},
        mainViewModel = MainViewModel.getTestViewModel(),
        toViewPasswordScreen = {},
        homeViewModel = HomeViewModel.getTestViewModel(),
        toSettingsScreen = {}
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