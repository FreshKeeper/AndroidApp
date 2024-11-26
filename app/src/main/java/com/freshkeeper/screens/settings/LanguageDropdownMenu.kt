package com.freshkeeper.screens.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.freshkeeper.R
import com.freshkeeper.ui.theme.AccentTurquoiseColor
import com.freshkeeper.ui.theme.ComponentStrokeColor
import com.freshkeeper.ui.theme.GreyColor
import com.freshkeeper.ui.theme.TextColor
import java.util.Locale

@Suppress("ktlint:standard:function-naming")
@Composable
fun LanguageDropdownMenu(
    currentLanguage: String,
    onLanguageSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedLanguage by remember { mutableStateOf(currentLanguage) }

    val languages =
        listOf(
            "de" to R.drawable.flag_germany,
            "en" to R.drawable.flag_usa,
        )

    val languageDisplayName =
        mapOf(
            "de" to stringResource(R.string.german),
            "en" to stringResource(R.string.english),
        )
    val selectedFlagRes = languages.find { it.first == selectedLanguage }?.second
    val selectedLanguageDisplay = languageDisplayName[currentLanguage] ?: currentLanguage

    Column {
        OutlinedTextField(
            value = selectedLanguageDisplay,
            onValueChange = { },
            modifier =
                Modifier
                    .fillMaxWidth(),
            label = { Text(stringResource(R.string.change_language), color = TextColor) },
            leadingIcon = {
                selectedFlagRes?.let {
                    Image(
                        painter = painterResource(id = it),
                        contentDescription = null,
                        modifier = Modifier.size(25.dp),
                    )
                }
            },
            trailingIcon = {
                Icon(
                    imageVector =
                        if (expanded) {
                            Icons.Filled.KeyboardArrowUp
                        } else {
                            Icons.Filled.KeyboardArrowDown
                        },
                    contentDescription = null,
                    modifier = Modifier.clickable { expanded = !expanded },
                )
            },
            readOnly = true,
            colors =
                OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = ComponentStrokeColor,
                    focusedBorderColor = AccentTurquoiseColor,
                    unfocusedLabelColor = TextColor,
                    focusedLabelColor = AccentTurquoiseColor,
                    focusedTextColor = TextColor,
                    unfocusedTextColor = TextColor,
                ),
            shape = RoundedCornerShape(10.dp),
            singleLine = true,
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier =
                Modifier
                    .width(200.dp)
                    .background(GreyColor)
                    .clip(RoundedCornerShape(10.dp)),
        ) {
            languages.forEach { (languageCode, flagRes) ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = languageDisplayName[languageCode] ?: languageCode.uppercase(Locale.ROOT),
                            color = TextColor,
                        )
                    },
                    leadingIcon = {
                        Image(
                            painter = painterResource(id = flagRes),
                            contentDescription = null,
                            modifier = Modifier.size(25.dp),
                        )
                    },
                    onClick = {
                        selectedLanguage = languageCode
                        expanded = false
                        onLanguageSelected(languageCode)
                    },
                )
            }
        }
    }
}
