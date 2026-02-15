package com.focusclinic.app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import focusclinic.composeapp.generated.resources.Res
import focusclinic.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun CalendarHeatmap(
    year: Int,
    month: Int,
    completionCounts: Map<Int, Int>,
    selectedDay: Int? = null,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onDayClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val daysInMonth = daysInMonth(year, month)
    val firstDayOfWeek = firstDayOfWeek(year, month)

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onPreviousMonth) {
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = stringResource(Res.string.calendar_previous),
                )
            }
            Text(
                text = "${monthName(month)} $year",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            IconButton(onClick = onNextMonth) {
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = stringResource(Res.string.calendar_next),
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        val weekdayHeaders = listOf(
            stringResource(Res.string.weekday_mon),
            stringResource(Res.string.weekday_tue),
            stringResource(Res.string.weekday_wed),
            stringResource(Res.string.weekday_thu),
            stringResource(Res.string.weekday_fri),
            stringResource(Res.string.weekday_sat),
            stringResource(Res.string.weekday_sun),
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            weekdayHeaders.forEach { header ->
                Text(
                    text = header,
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        var dayCounter = 1
        val totalCells = firstDayOfWeek + daysInMonth
        val rows = (totalCells + 6) / 7

        for (row in 0 until rows) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                for (col in 0 until 7) {
                    val cellIndex = row * 7 + col
                    if (cellIndex < firstDayOfWeek || dayCounter > daysInMonth) {
                        Box(modifier = Modifier.weight(1f).aspectRatio(1f))
                    } else {
                        val day = dayCounter
                        val count = completionCounts[day] ?: 0
                        DayCell(
                            day = day,
                            completionCount = count,
                            isSelected = day == selectedDay,
                            onClick = { onDayClick(day) },
                            modifier = Modifier.weight(1f),
                        )
                        dayCounter++
                    }
                }
            }
        }
    }
}

@Composable
private fun DayCell(
    day: Int,
    completionCount: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = when {
        completionCount >= 5 -> MaterialTheme.colorScheme.primary
        completionCount >= 3 -> MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
        completionCount >= 1 -> MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
        else -> Color.Transparent
    }
    val textColor = when {
        completionCount >= 3 -> MaterialTheme.colorScheme.onPrimary
        else -> MaterialTheme.colorScheme.onSurface
    }
    val selectionBorder = if (isSelected) {
        Modifier.border(2.dp, MaterialTheme.colorScheme.tertiary, CircleShape)
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(CircleShape)
            .then(selectionBorder)
            .background(backgroundColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = day.toString(),
            style = MaterialTheme.typography.bodySmall,
            color = textColor,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
        )
    }
}

private fun daysInMonth(year: Int, month: Int): Int {
    return when (month) {
        1, 3, 5, 7, 8, 10, 12 -> 31
        4, 6, 9, 11 -> 30
        2 -> if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) 29 else 28
        else -> 30
    }
}

private fun firstDayOfWeek(year: Int, month: Int): Int {
    // Zeller's congruence to find day of week for the 1st of the month
    // Returns 0=Monday, 6=Sunday
    var y = year
    var m = month
    if (m < 3) {
        m += 12
        y -= 1
    }
    val q = 1
    val k = y % 100
    val j = y / 100
    val h = (q + (13 * (m + 1)) / 5 + k + k / 4 + j / 4 + 5 * j) % 7
    // Convert from Zeller (0=Sat) to Monday-start (0=Mon)
    return ((h + 5) % 7)
}

@Composable
private fun monthName(month: Int): String = when (month) {
    1 -> stringResource(Res.string.calendar_january)
    2 -> stringResource(Res.string.calendar_february)
    3 -> stringResource(Res.string.calendar_march)
    4 -> stringResource(Res.string.calendar_april)
    5 -> stringResource(Res.string.calendar_may)
    6 -> stringResource(Res.string.calendar_june)
    7 -> stringResource(Res.string.calendar_july)
    8 -> stringResource(Res.string.calendar_august)
    9 -> stringResource(Res.string.calendar_september)
    10 -> stringResource(Res.string.calendar_october)
    11 -> stringResource(Res.string.calendar_november)
    12 -> stringResource(Res.string.calendar_december)
    else -> ""
}
