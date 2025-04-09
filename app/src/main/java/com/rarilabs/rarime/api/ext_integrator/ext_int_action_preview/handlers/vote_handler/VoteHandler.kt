package com.rarilabs.rarime.api.ext_integrator.ext_int_action_preview.handlers.vote_handler

import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.rarilabs.rarime.R
import com.rarilabs.rarime.modules.main.LocalMainViewModel
import com.rarilabs.rarime.modules.main.ScreenInsets
import com.rarilabs.rarime.modules.votes.voteProcessScreen.VoteProcessScreenContent
import com.rarilabs.rarime.ui.components.SnackbarSeverity
import com.rarilabs.rarime.ui.components.getSnackbarDefaultShowOptions
import com.rarilabs.rarime.util.ErrorHandler
import com.rarilabs.rarime.util.Screen
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

@Composable
fun VoteHandler(
    queryParams: Map<String, String?>?,
    onCancel: () -> Unit = {},
    onSuccess: (destination: String?) -> Unit = {},
    onFail: () -> Unit = {},
    viewModel: VoteHandlerViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val mainViewModel = LocalMainViewModel.current
    val screenInsets by mainViewModel.screenInsets.collectAsState()

    fun onSuccessHandler() {
        scope.launch {
            val snackBar = async {
                mainViewModel.showSnackbar(
                    options = getSnackbarDefaultShowOptions(
                        severity = SnackbarSeverity.Success,
                        duration = SnackbarDuration.Long,
                        title = context.getString(R.string.light_verification_success_title),
                        message = context.getString(R.string.light_verification_success_subtitle),
                    )
                )
            }

            val redirect = async { onSuccess.invoke(Screen.Main.Home.route) }

            awaitAll(
                snackBar,
                redirect
            )
        }

    }

    fun onFailHandler(e: Exception) {
        scope.launch {
            ErrorHandler.logError("VoteHandler", "onFailHandler", e)

            mainViewModel.showSnackbar(
                options = getSnackbarDefaultShowOptions(
                    severity = SnackbarSeverity.Error,
                    duration = SnackbarDuration.Long,
                    title = context.getString(R.string.light_verification_error_title),
                    message = "", // TODO: implement me
                )
            )
        }
        onFail.invoke()
    }

    var isLoading by remember { mutableStateOf(true) }
    val voteData by viewModel.voteData.collectAsState()

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val qrCodeUrl = queryParams?.get("qr_code_url")
                    ?: throw Exception("Proposal ID not found")

                viewModel.saveVoting(qrCodeUrl)
            } catch (e: Exception) {
                ErrorHandler.logError("ExtIntActionPreview", "loadPreviewFields", e)
                onFailHandler(e)
            }

            isLoading = false
        }
    }

    VoteProcessScreenContent(
        screenInsets = mapOf(
            ScreenInsets.TOP to screenInsets.get(ScreenInsets.TOP),
            ScreenInsets.BOTTOM to screenInsets.get(ScreenInsets.BOTTOM)
        ),
        voteData = voteData,
        isLoading = isLoading,
        onBackClick = onCancel,
        onVote = {
            scope.launch {
                try {
                    viewModel.vote(context, it)
                    onSuccessHandler()
                } catch (error: Exception) {
                    onFailHandler(error)
                }
            }
        }
    )
}
