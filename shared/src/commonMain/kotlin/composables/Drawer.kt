package composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import data.MainViewModel
import data.exampleAccountsState
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import platform.pointerCursor
import resourceBindings.drawable_jetchat_icon_mpp

@Composable
fun AppDrawer(
    onProfileClicked: (String) -> Unit,
    onChatClicked: (String) -> Unit,
    viewModel: MainViewModel,
) {
    val selectedChatTitle by viewModel.conversationUiState.collectAsState()
    val currentUser by viewModel.user.collectAsState()
    val chats by viewModel.chats.collectAsState()
    Box {
        Column {
            Spacer(Modifier.height(3.dp))
            DrawerHeader()
            DividerItem()
            DrawerItemHeader("Chats")
            chats.entries.forEach { (id, chat) ->
                ChatItem(chat.channelName, selectedChatTitle.channelName == chat.channelName) {
                    onChatClicked(id)
                }
            }
            RoomCreationButton(viewModel)
            DividerItem(modifier = Modifier.padding(horizontal = 28.dp))
            DrawerItemHeader("Recent Profiles")
            exampleAccountsState.entries.forEach { (profileId, profile) ->
                ProfileItem(profile.name, profile.photo) { onProfileClicked(profileId) }
            }
            ThemeSwitch(viewModel)
        }
        Row(
            Modifier
                .shadow(32.dp)
                .background(MaterialTheme.colorScheme.onBackground)
                .padding(24.dp)
                .align(Alignment.BottomCenter)
        ) {
            currentUser?.let {
                Text(
                    text = "Logged in as ${it.firstName} ${it.lastName}",
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(12.dp))
                LogoutButton(viewModel, Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun RoomCreationButton(viewModel: MainViewModel) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Column(
            Modifier
                .size(48.dp)
                .shadow(18.dp, RoundedCornerShape(50))
                .background(MaterialTheme.colorScheme.tertiary, RoundedCornerShape(50))
                .pointerHoverIcon(PointerIcon.Hand)
                .clickable {
                    viewModel.openRoomDialog()
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Add room",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun LogoutButton(viewModel: MainViewModel, modifier: Modifier = Modifier) {
    AuthButton(true, "Log out", modifier) {
        viewModel.logoutUser()
    }
}


@Composable
private fun DrawerHeader() {
    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
        JetchatIcon(
            contentDescription = "Open navigation drawer",
            modifier = Modifier
                .size(64.dp)
                .padding(16.dp)
        )
    }
}

@Composable
private fun DrawerItemHeader(text: String) {
    Box(
        modifier = Modifier
            .heightIn(min = 52.dp)
            .padding(horizontal = 28.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun ChatItem(text: String, selected: Boolean, onChatClicked: () -> Unit) {
    val background = if (selected) {
        Modifier.background(MaterialTheme.colorScheme.tertiary)
    } else {
        Modifier
    }
    Row(
        modifier = Modifier
            .height(56.dp)
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .pointerCursor()
            .clip(CircleShape)
            .then(background)
            .clickable(onClick = onChatClicked),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val iconTint = if (selected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        }
        Icon(
            painter = painterResource(drawable_jetchat_icon_mpp),
            tint = iconTint,
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 16.dp),
            contentDescription = null
        )
        Text(
            text,
            style = MaterialTheme.typography.bodyMedium,
            color = if (selected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurface
            },
            modifier = Modifier.padding(start = 12.dp)
        )
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun ProfileItem(text: String, profilePic: String?, onProfileClicked: () -> Unit) {
    Row(
        modifier = Modifier
            .height(56.dp)
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .clip(CircleShape)
            .pointerCursor()
            .clickable(onClick = onProfileClicked),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val paddingSizeModifier = Modifier
            .padding(start = 16.dp, top = 16.dp, bottom = 16.dp)
            .size(24.dp)
        if (profilePic != null) {
            Image(
                painter = painterResource(profilePic),
                modifier = paddingSizeModifier.then(Modifier.clip(CircleShape)),
                contentScale = ContentScale.Crop,
                contentDescription = null
            )
        } else {
            Spacer(modifier = paddingSizeModifier)
        }
        Text(
            text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(start = 12.dp)
        )
    }
}

@Composable
fun DividerItem(modifier: Modifier = Modifier) {
    Divider(
        modifier = modifier,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
    )
}