package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CalculationHistoryItem
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.ObsidianBorder
import com.example.ui.theme.ObsidianSurface
import com.example.ui.theme.ObsidianSurfaceHigh
import com.example.ui.theme.TextErrorDark
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryBottomSheet(
  history: List<CalculationHistoryItem>,
  onDismiss: () -> Unit,
  onItemClick: (CalculationHistoryItem) -> Unit,
  onClearHistory: () -> Unit,
  onCopyTriggered: () -> Unit,
  sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
  val context = LocalContext.current
  val isDark = MaterialTheme.colorScheme.background.red < 0.2f
  val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = if (isDark) ObsidianSurface else Color.White,
    dragHandle = {
      Box(
        modifier = Modifier
          .padding(top = 12.dp, bottom = 8.dp)
          .size(width = 40.dp, height = 4.dp)
          .clip(CircleShape)
          .background(if (isDark) Color(0xFF333A4D) else Color(0xFFD0D7E5))
      )
    },
    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
    modifier = Modifier.testTag("history_sheet")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight(0.65f)
        .padding(horizontal = 20.dp)
    ) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Icon(
            imageVector = Icons.Default.History,
            contentDescription = null,
            tint = ChampagneGold,
            modifier = Modifier.size(22.dp)
          )
          Text(
            text = "Calculation History",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isDark) TextPrimaryDark else Color(0xFF14171E)
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          if (history.isNotEmpty()) {
            IconButton(
              onClick = onClearHistory,
              modifier = Modifier.testTag("button_clear_history")
            ) {
              Icon(
                imageVector = Icons.Default.DeleteSweep,
                contentDescription = "Clear all history",
                tint = if (isDark) TextSecondaryDark else Color(0xFF5A667E)
              )
            }
          }
          IconButton(onClick = onDismiss) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close history",
              tint = if (isDark) TextSecondaryDark else Color(0xFF5A667E)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      if (history.isEmpty()) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
          contentAlignment = Alignment.Center
        ) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(24.dp)
          ) {
            Surface(
              shape = CircleShape,
              color = if (isDark) Color(0xFF1B202D) else Color(0xFFEDF1FA),
              modifier = Modifier.size(56.dp)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                  imageVector = Icons.Default.History,
                  contentDescription = null,
                  tint = if (isDark) TextSecondaryDark else Color(0xFF7A869E),
                  modifier = Modifier.size(28.dp)
                )
              }
            }
            Text(
              text = "No calculations yet",
              fontSize = 16.sp,
              fontWeight = FontWeight.Medium,
              color = if (isDark) TextPrimaryDark else Color(0xFF14171E)
            )
            Text(
              text = "Completed equations and results will appear here for one-tap reuse.",
              fontSize = 13.sp,
              color = if (isDark) TextSecondaryDark else Color(0xFF6B758E),
              textAlign = TextAlign.Center
            )
          }
        }
      } else {
        LazyColumn(
          modifier = Modifier.fillMaxWidth().weight(1f),
          verticalArrangement = Arrangement.spacedBy(10.dp),
          contentPadding = PaddingValues(bottom = 24.dp)
        ) {
          items(history, key = { it.id }) { item ->
            HistoryCard(
              item = item,
              timeFormatted = timeFormat.format(Date(item.timestamp)),
              isDark = isDark,
              onClick = { onItemClick(item) },
              onCopy = {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Calculation", item.result)
                clipboard.setPrimaryClip(clip)
                onCopyTriggered()
              }
            )
          }
        }
      }
    }
  }
}

@Composable
private fun HistoryCard(
  item: CalculationHistoryItem,
  timeFormatted: String,
  isDark: Boolean,
  onClick: () -> Unit,
  onCopy: () -> Unit
) {
  Surface(
    shape = RoundedCornerShape(18.dp),
    color = if (isDark) ObsidianSurfaceHigh else Color(0xFFF7F8FC),
    border = androidx.compose.foundation.BorderStroke(
      1.dp,
      if (isDark) ObsidianBorder else Color(0xFFE2E6F0)
    ),
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(18.dp))
      .clickable(onClick = onClick)
      .testTag("history_item_${item.id}")
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 12.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Text(
            text = item.expression,
            fontSize = 14.sp,
            color = if (isDark) TextSecondaryDark else Color(0xFF66718A),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
          Text(
            text = timeFormatted,
            fontSize = 10.sp,
            color = if (isDark) Color(0xFF555F75) else Color(0xFF9BA4B8)
          )
        }

        Text(
          text = "= ${item.result}",
          fontSize = 20.sp,
          fontWeight = FontWeight.SemiBold,
          color = ChampagneGold,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }

      IconButton(
        onClick = onCopy,
        modifier = Modifier.size(32.dp)
      ) {
        Icon(
          imageVector = Icons.Default.ContentCopy,
          contentDescription = "Copy result",
          tint = if (isDark) TextSecondaryDark else Color(0xFF717D96),
          modifier = Modifier.size(16.dp)
        )
      }
    }
  }
}
