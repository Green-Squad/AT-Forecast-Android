package com.gsnamespace.atforecast.ui.components

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource

/**
 * Display a state image based on the state's image name.
 * Loads drawable resources directly using Compose's painterResource.
 */
@Composable
fun StateImage(
    imageName: String,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Get drawable resource ID from image name
    val resourceId = context.resources.getIdentifier(
        imageName,
        "drawable",
        context.packageName
    )

    if (resourceId != 0) {
        // Use standard Compose Image for drawable resources
        Image(
            painter = painterResource(id = resourceId),
            contentDescription = contentDescription,
            modifier = modifier
        )
    } else {
        // Fallback: No image available - could show a placeholder or icon
        // For now, we'll just not render anything
    }
}
