package com.freshkeeper.screens.home.service

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.freshkeeper.ui.theme.ComponentBackgroundColor
import com.freshkeeper.ui.theme.ComponentStrokeColor
import com.freshkeeper.ui.theme.Grey
import com.freshkeeper.ui.theme.TextColor
import com.freshkeeper.ui.theme.White

@Suppress("ktlint:standard:function-naming")
@Composable
fun FoodList(
    title: String,
    image: Painter,
    items: List<Pair<String, String>>,
) {
    Column(
        modifier =
            Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(ComponentBackgroundColor)
                .border(1.dp, ComponentStrokeColor, RoundedCornerShape(20.dp))
                .padding(16.dp),
    ) {
        Row {
            Image(
                modifier = Modifier.size(25.dp),
                contentDescription = null,
                painter = image,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextColor,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        items.forEach { (item, date) ->
            Row(
                modifier =
                    Modifier
                        .padding(bottom = 8.dp)
                        .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Box(
                    modifier =
                        Modifier
                            .clip(RoundedCornerShape(topStart = 10.dp, bottomStart = 10.dp))
                            .weight(1f)
                            .background(White)
                            .padding(horizontal = 10.dp, vertical = 2.dp),
                ) {
                    Text(
                        text = item,
                        style = MaterialTheme.typography.labelLarge,
                        color = ComponentBackgroundColor,
                        maxLines = 1,
                    )
                }

                Box(
                    modifier =
                        Modifier
                            .clip(RoundedCornerShape(topEnd = 10.dp, bottomEnd = 10.dp))
                            .weight(1f)
                            .background(Grey)
                            .padding(horizontal = 10.dp, vertical = 2.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = date,
                        style = MaterialTheme.typography.labelLarge,
                        color = TextColor,
                        maxLines = 1,
                    )
                }
            }
        }
    }
}
