@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.timelytrack.ui.history

//import android.graphics.Color

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.RectangleShape

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Preview(showBackground = true)
@Composable
fun HistoryScreenPreview() {
    HistoryScreen()
}




@Composable
fun HistoryScreen() {
    Text(text = "History Screen")

    DemoBottomSheet()

    DemoSwipeToDismissBox()

    Scaffold(
        content={ innerPadding ->
            LazyColumn{
              item { DemoSwipeToDismissBox() }
                item { DemoBottomSheet() }

            }
        }
    )

}

@Composable
fun DemoSwipeToDismissBox() {
    val dismissState = rememberSwipeToDismissBoxState()
    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            val color by
            animateColorAsState(
                when (dismissState.targetValue) {
                    SwipeToDismissBoxValue.Settled -> Color.LightGray
                    SwipeToDismissBoxValue.StartToEnd -> Color.Green
                    SwipeToDismissBoxValue.EndToStart -> Color.Red
                }
            )
            Box(Modifier.fillMaxSize().background(color))
        }
    ) {
        OutlinedCard(shape = RectangleShape) {
            ListItem(
                headlineContent = { Text("Cupcake") },
                supportingContent = { Text("Swipe me left or right!") }
            )
        }
    }
}


@Composable
fun DemoBottomSheet() {
    var openBottomSheet by rememberSaveable { mutableStateOf(false) }
    var skipPartiallyExpanded by rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val bottomSheetState =
        rememberModalBottomSheetState(skipPartiallyExpanded = skipPartiallyExpanded)


// App content
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            Modifier.toggleable(
                value = skipPartiallyExpanded,
                role = Role.Checkbox,
                onValueChange = { checked -> skipPartiallyExpanded = checked }
            )
        ) {
            Checkbox(checked = skipPartiallyExpanded, onCheckedChange = null)
            Spacer(Modifier.width(16.dp))
            Text("Skip partially expanded State")
        }
        Button(
            onClick = { openBottomSheet = !openBottomSheet },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Show Bottom Sheet")
        }
    }

// Sheet content
    if (openBottomSheet) {

        ModalBottomSheet(
            onDismissRequest = { openBottomSheet = false },
            sheetState = bottomSheetState,
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Button(
                    // Note: If you provide logic outside of onDismissRequest to remove the sheet,
                    // you must additionally handle intended state cleanup, if any.
                    onClick = {
                        scope
                            .launch { bottomSheetState.hide() }
                            .invokeOnCompletion {
                                if (!bottomSheetState.isVisible) {
                                    openBottomSheet = false
                                }
                            }
                    }
                ) {
                    Text("Hide Bottom Sheet")
                }
            }
            var text by remember { mutableStateOf("") }
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.padding(horizontal = 16.dp),
                label = { Text("Text field") }
            )
            LazyColumn {
                items(25) {
                    ListItem(
                        headlineContent = { Text("Item $it") },
                        leadingContent = {
                            Icon(
                                Icons.Default.Favorite,
                                contentDescription = "Localized description"
                            )
                        },
                        colors =
                        ListItemDefaults.colors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                        ),
                    )
                }
            }
        }
    }
}


@Preview
@Composable
fun ColorSchemePreview() {
    MaterialTheme {
        Column {
            ColorDisplay(color = MaterialTheme.colorScheme.tertiary, name = "primary")
            ColorDisplay(color = MaterialTheme.colorScheme.onPrimary, name = "onPrimary")
            ColorDisplay(color = MaterialTheme.colorScheme.primaryContainer, name = "primaryContainer")
            // ... display other colors similarly
        }
    }
}

@Composable
fun ColorDisplay(color: Color, name: String) {
    Box(
        modifier = Modifier
            .size(100.dp)
            .background(color),
        contentAlignment = Alignment.Center
    ) {
        Text(text = name, color = Color.White)
    }
}

//
//
///** The directions in which a [SwipeToDismiss] can be dismissed. */
//enum class DismissDirection {
//    /** Can be dismissed by swiping in the reading direction. */
//    StartToEnd,
//
//    /** Can be dismissed by swiping in the reverse of the reading direction. */
//    EndToStart
//}
//
///** Possible values of [DismissState]. */
//enum class DismissValue {
//    /** Indicates the component has not been dismissed yet. */
//    Default,
//
//    /** Indicates the component has been dismissed in the reading direction. */
//    DismissedToEnd,
//
//    /** Indicates the component has been dismissed in the reverse of the reading direction. */
//    DismissedToStart
//}
//
///**
// * State of the [SwipeToDismiss] composable.
// *
// * @param initialValue The initial value of the state.
// * @param confirmStateChange Optional callback invoked to confirm or veto a pending state change.
// */
//@ExperimentalMaterialApi
//class DismissState(
//    initialValue: DismissValue,
//    confirmStateChange: (DismissValue) -> Boolean = { true }
//) : SwipeableState<DismissValue>(initialValue, confirmStateChange = confirmStateChange) {
//    /**
//     * The direction (if any) in which the composable has been or is being dismissed.
//     *
//     * If the composable is settled at the default state, then this will be null. Use this to change
//     * the background of the [SwipeToDismiss] if you want different actions on each side.
//     */
//    val dismissDirection: DismissDirection?
//        get() = if (offset.value == 0f) null else if (offset.value > 0f) StartToEnd else EndToStart
//
//    /**
//     * Whether the component has been dismissed in the given [direction].
//     *
//     * @param direction The dismiss direction.
//     */
//    fun isDismissed(direction: DismissDirection): Boolean {
//        return currentValue == if (direction == StartToEnd) DismissedToEnd else DismissedToStart
//    }
//
//    /**
//     * Reset the component to the default position with animation and suspend until it if fully
//     * reset or animation has been cancelled. This method will throw [CancellationException] if the
//     * animation is interrupted
//     *
//     * @return the reason the reset animation ended
//     */
//    suspend fun reset() = animateTo(targetValue = Default)
//
//    /**
//     * Dismiss the component in the given [direction], with an animation and suspend. This method
//     * will throw [CancellationException] if the animation is interrupted
//     *
//     * @param direction The dismiss direction.
//     */
//    suspend fun dismiss(direction: DismissDirection) {
//        val targetValue = if (direction == StartToEnd) DismissedToEnd else DismissedToStart
//        animateTo(targetValue = targetValue)
//    }
//
//    companion object {
//        /** The default [Saver] implementation for [DismissState]. */
//        fun Saver(confirmStateChange: (DismissValue) -> Boolean) =
//            Saver<DismissState, DismissValue>(
//                save = { it.currentValue },
//                restore = { DismissState(it, confirmStateChange) }
//            )
//    }
//}
//
///**
// * Create and [remember] a [DismissState].
// *
// * @param initialValue The initial value of the state.
// * @param confirmStateChange Optional callback invoked to confirm or veto a pending state change.
// */
//@Composable
//@ExperimentalMaterialApi
//fun rememberDismissState(
//    initialValue: DismissValue = Default,
//    confirmStateChange: (DismissValue) -> Boolean = { true }
//): DismissState {
//    return rememberSaveable(saver = DismissState.Saver(confirmStateChange)) {
//        DismissState(initialValue, confirmStateChange)
//    }
//}
//
///**
// * A composable that can be dismissed by swiping left or right.
// *
// * @sample androidx.compose.material.samples.SwipeToDismissListItems
// * @param state The state of this component.
// * @param modifier Optional [Modifier] for this component.
// * @param directions The set of directions in which the component can be dismissed.
// * @param dismissThresholds The thresholds the item needs to be swiped in order to be dismissed.
// * @param background A composable that is stacked behind the content and is exposed when the content
// *   is swiped. You can/should use the [state] to have different backgrounds on each side.
// * @param dismissContent The content that can be dismissed.
// */
//@Composable
//@ExperimentalMaterialApi
//@Suppress("ReferencesDeprecated")
//fun SwipeToDismiss(
//    state: DismissState,
//    modifier: Modifier = Modifier,
//    directions: Set<DismissDirection> = setOf(EndToStart, StartToEnd),
//    dismissThresholds: (DismissDirection) -> ThresholdConfig = {
//        FixedThreshold(DISMISS_THRESHOLD)
//    },
//    background: @Composable RowScope.() -> Unit,
//    dismissContent: @Composable RowScope.() -> Unit
//) =
//    BoxWithConstraints(modifier) {
//        val width = constraints.maxWidth.toFloat()
//        val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
//
//        val anchors = mutableMapOf(0f to Default)
//        if (StartToEnd in directions) anchors += width to DismissedToEnd
//        if (EndToStart in directions) anchors += -width to DismissedToStart
//
//        val thresholds = { from: DismissValue, to: DismissValue ->
//            dismissThresholds(getDismissDirection(from, to)!!)
//        }
//        val minFactor =
//            if (EndToStart in directions) StandardResistanceFactor else StiffResistanceFactor
//        val maxFactor =
//            if (StartToEnd in directions) StandardResistanceFactor else StiffResistanceFactor
//        Box(
//            Modifier.swipeable(
//                state = state,
//                anchors = anchors,
//                thresholds = thresholds,
//                orientation = Orientation.Horizontal,
//                enabled = state.currentValue == Default,
//                reverseDirection = isRtl,
//                resistance =
//                ResistanceConfig(
//                    basis = width,
//                    factorAtMin = minFactor,
//                    factorAtMax = maxFactor
//                )
//            )
//        ) {
//            Row(content = background, modifier = Modifier.matchParentSize())
//            Row(
//                content = dismissContent,
//                modifier = Modifier.offset { IntOffset(state.offset.value.roundToInt(), 0) }
//            )
//        }
//    }
//
//private fun getDismissDirection(from: DismissValue, to: DismissValue): androidx.compose.material.DismissDirection? {
//    return when {
//        // settled at the default state
//        from == to && from == Default -> null
//        // has been dismissed to the end
//        from == to && from == DismissedToEnd -> StartToEnd
//        // has been dismissed to the start
//        from == to && from == DismissedToStart -> EndToStart
//        // is currently being dismissed to the end
//        from == Default && to == DismissedToEnd -> StartToEnd
//        // is currently being dismissed to the start
//        from == Default && to == DismissedToStart -> EndToStart
//        // has been dismissed to the end but is now animated back to default
//        from == DismissedToEnd && to == Default -> StartToEnd
//        // has been dismissed to the start but is now animated back to default
//        from == DismissedToStart && to == Default -> EndToStart
//        else -> null
//    }
//}
//
//private val DISMISS_THRESHOLD = 56.dp