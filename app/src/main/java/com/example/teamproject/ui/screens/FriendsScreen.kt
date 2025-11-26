package com.example.teamproject.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.teamproject.viewmodel.FriendsUiState
import com.example.teamproject.viewmodel.RankingsUiState
import com.example.teamproject.viewmodel.UserUiState
import com.example.teamproject.viewmodel.UserViewModel

data class RankingUser(
    val userId: Long,
    val username: String,
    val name: String,
    val totalAmount: Int,
    val rank: Int,
    val isCurrentUser: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendsScreen(
    userViewModel: UserViewModel = viewModel()
) {
    var rankings by remember { mutableStateOf(listOf<RankingUser>()) }
    var myRanking by remember { mutableStateOf<RankingUser?>(null) }
    var myGoal by remember { mutableStateOf(2000) }

    // Observe states
    val rankingsState by userViewModel.rankingsState.collectAsState()
    val userState by userViewModel.userState.collectAsState()

    // Get lifecycle owner to observe lifecycle events
    val lifecycleOwner = LocalLifecycleOwner.current

    // Load user info and rankings on first composition
    LaunchedEffect(Unit) {
        userViewModel.loadCurrentUser()
    }

    // Load rankings when screen resumes (becomes visible)
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                userViewModel.loadTodayRankings()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Get my user ID and recommended amount from user state
    LaunchedEffect(userState) {
        if (userState is UserUiState.Success) {
            val user = (userState as UserUiState.Success).user
            myGoal = user.profile?.recommendAmount ?: 2000
        }
    }

    // Update rankings when API data is loaded
    LaunchedEffect(rankingsState, userState) {
        when (val state = rankingsState) {
            is RankingsUiState.Success -> {
                val currentUserId = if (userState is UserUiState.Success) {
                    (userState as UserUiState.Success).user.id
                } else null

                // Convert API rankings to UI rankings
                rankings = state.data.rankings.map { rankingInfo ->
                    RankingUser(
                        userId = rankingInfo.userId,
                        username = rankingInfo.username,
                        name = rankingInfo.name,
                        totalAmount = rankingInfo.totalAmount,
                        rank = rankingInfo.rank,
                        isCurrentUser = rankingInfo.userId == currentUserId
                    )
                }

                // Find my ranking
                myRanking = rankings.find { it.isCurrentUser }
            }
            else -> {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Friends Competition",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // 나의 현재 상태
        myRanking?.let { ranking ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "My Today's Rank",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Surface(
                            shape = MaterialTheme.shapes.medium,
                            color = MaterialTheme.colorScheme.primary
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "#${ranking.rank}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${ranking.totalAmount} ml",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "${((ranking.totalAmount.toFloat() / myGoal) * 100).toInt()}%",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                    LinearProgressIndicator(
                        progress = { (ranking.totalAmount.toFloat() / myGoal).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                            .height(8.dp)
                    )
                }
            }
        }

        // 오늘의 랭킹 헤더
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp, top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Today's Water Intake Ranking",
                style = MaterialTheme.typography.titleMedium
            )
        }

        when (rankingsState) {
            is RankingsUiState.Idle, is RankingsUiState.Loading -> {
                // Show loading spinner for both Idle and Loading states
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is RankingsUiState.Error -> {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Unable to load rankings",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
            is RankingsUiState.Success -> {
                if (rankings.isEmpty()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No ranking information yet",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(rankings) { ranking ->
                            RankingCard(
                                ranking = ranking,
                                goalAmount = myGoal
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RankingCard(
    ranking: RankingUser,
    goalAmount: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when {
                ranking.isCurrentUser -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                ranking.rank == 1 -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                ranking.rank == 2 -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                ranking.rank == 3 -> MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
                else -> MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 순위 배지
            Surface(
                modifier = Modifier.size(40.dp),
                shape = MaterialTheme.shapes.medium,
                color = when (ranking.rank) {
                    1 -> MaterialTheme.colorScheme.primary
                    2 -> MaterialTheme.colorScheme.secondary
                    3 -> MaterialTheme.colorScheme.tertiary
                    else -> MaterialTheme.colorScheme.surfaceVariant
                }
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = "${ranking.rank}",
                        style = MaterialTheme.typography.titleMedium,
                        color = if (ranking.rank <= 3) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // 사용자 정보
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = ranking.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                    if (ranking.isCurrentUser) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = MaterialTheme.shapes.small,
                            color = MaterialTheme.colorScheme.primary
                        ) {
                            Text(
                                text = "Me",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${ranking.totalAmount} ml",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // 달성률
            val progressPercentage = ((ranking.totalAmount.toFloat() / goalAmount) * 100).toInt()
            Text(
                text = "$progressPercentage%",
                style = MaterialTheme.typography.titleLarge,
                color = when {
                    progressPercentage >= 100 -> MaterialTheme.colorScheme.primary
                    progressPercentage >= 70 -> MaterialTheme.colorScheme.secondary
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
    }
}
