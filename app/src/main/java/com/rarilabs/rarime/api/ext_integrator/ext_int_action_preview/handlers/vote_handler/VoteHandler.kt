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
import com.rarilabs.rarime.manager.VoteError
import com.rarilabs.rarime.modules.main.LocalMainViewModel
import com.rarilabs.rarime.modules.votes.voteProcessScreen.VotingAppSheet
import com.rarilabs.rarime.ui.components.SnackbarSeverity
import com.rarilabs.rarime.ui.components.getSnackbarDefaultShowOptions
import com.rarilabs.rarime.ui.components.rememberAppSheetState
import com.rarilabs.rarime.util.ErrorHandler
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

    val voteSheetState = rememberAppSheetState()

    val selectedVote by viewModel.selectedVote.collectAsState()

    VotingAppSheet(
        voteSheetState = voteSheetState,
        selectedPoll = selectedVote,
        navigate = onSuccess
    )


    fun onFailHandler(e: Exception) {
        scope.launch {
            ErrorHandler.logError("VoteHandler", "onFailHandler", e)

            if (e is VoteError.NotFound) {
                mainViewModel.showSnackbar(
                    options = getSnackbarDefaultShowOptions(
                        severity = SnackbarSeverity.Error,
                        duration = SnackbarDuration.Long,
                        title = context.getString(R.string.light_verification_error_title),
                        message = "Qr code is expired. Try another one",
                    )
                )
            } else {
                mainViewModel.showSnackbar(
                    options = getSnackbarDefaultShowOptions(
                        severity = SnackbarSeverity.Error,
                        duration = SnackbarDuration.Long,
                        title = context.getString(R.string.light_verification_error_title),
                        message = context.getString(R.string.failed_to_parse_qr_code),
                    )
                )
            }


        }
        onFail.invoke()
    }

    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val qrCodeUrl = queryParams?.get("qr_code_url")
                    ?: throw Exception("Proposal ID not found")

                voteSheetState.show()
                viewModel.setQrVoting(qrCodeUrl)

            } catch (e: Exception) {
                voteSheetState.hide()
                ErrorHandler.logError("ExtIntActionPreview", "loadPreviewFields", e)
                onFailHandler(e)
            } finally {
                isLoading = false
            }
        }
    }
}
