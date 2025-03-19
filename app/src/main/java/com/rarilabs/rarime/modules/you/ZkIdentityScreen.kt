package com.rarilabs.rarime.modules.you

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun ZkIdentityScreen(modifier: Modifier = Modifier, navigate: (String) -> Unit) {

    /*
    * TODO: Same logic like in card
    */

    //TODO: Get scan passport Status
    val isScanned = remember { false }

    if (isScanned) {
        ZkIdentityPassport(navigate = navigate)
    } else {
        ZkIdentityNoPassport(navigate = navigate)
    }


}


@Preview(showBackground = true)
@Composable
private fun ZkIdentityScreenPreview() {
    ZkIdentityScreen {}
}