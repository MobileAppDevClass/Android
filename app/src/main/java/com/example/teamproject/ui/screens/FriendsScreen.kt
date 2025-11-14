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
import androidx.compose.ui.unit.dp

data class Friend(
    val id: String,
    val name: String,
    val todayWaterAmount: Int,
    val goalAmount: Int = 2000
) {
    val progressPercentage: Int
        get() = ((todayWaterAmount.toFloat() / goalAmount) * 100).toInt().coerceIn(0, 100)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendsScreen() {
    // 임시 친구 데이터
    var friends by remember {
        mutableStateOf(
            listOf(
                Friend("1", "김철수", 1800),
                Friend("2", "이영희", 2100),
                Friend("3", "박민수", 1500),
                Friend("4", "최지은", 2300)
            )
        )
    }

    // 나의 오늘 물 섭취량 (임시)
    val myWaterAmount = 1600
    val myGoal = 2000

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "친구와 경쟁",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // 나의 현재 상태
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "나의 오늘 기록",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$myWaterAmount ml",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "${((myWaterAmount.toFloat() / myGoal) * 100).toInt()}%",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
                LinearProgressIndicator(
                    progress = { myWaterAmount.toFloat() / myGoal },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .height(8.dp)
                )
            }
        }

        // 친구 목록 헤더
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "친구 목록",
                style = MaterialTheme.typography.titleMedium
            )
            IconButton(onClick = { /* TODO: 친구 추가 기능 */ }) {
                Icon(Icons.Default.PersonAdd, contentDescription = "친구 추가")
            }
        }

        // 랭킹 정렬된 친구 목록
        val sortedFriends = friends.sortedByDescending { it.progressPercentage }

        if (sortedFriends.isEmpty()) {
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
                        text = "아직 추가된 친구가 없습니다",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(sortedFriends) { friend ->
                    FriendRankingCard(
                        friend = friend,
                        rank = sortedFriends.indexOf(friend) + 1
                    )
                }
            }
        }
    }
}

@Composable
fun FriendRankingCard(
    friend: Friend,
    rank: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when (rank) {
                1 -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                2 -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                3 -> MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
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
                color = when (rank) {
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
                        text = "$rank",
                        style = MaterialTheme.typography.titleMedium,
                        color = if (rank <= 3) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // 친구 정보
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = friend.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${friend.todayWaterAmount} ml / ${friend.goalAmount} ml",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { friend.todayWaterAmount.toFloat() / friend.goalAmount },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // 달성률
            Text(
                text = "${friend.progressPercentage}%",
                style = MaterialTheme.typography.titleLarge,
                color = when {
                    friend.progressPercentage >= 100 -> MaterialTheme.colorScheme.primary
                    friend.progressPercentage >= 70 -> MaterialTheme.colorScheme.secondary
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
    }
}
