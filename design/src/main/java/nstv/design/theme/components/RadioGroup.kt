package nstv.design.theme.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import nstv.design.theme.Grid

@Composable
fun <T : Any> RadioGroup(
    modifier: Modifier = Modifier,
    options: List<T>,
    displayValue: (T) -> String,
    onSelected: (T) -> Unit
) {

    val (selectedOption, onOptionSelected) = remember { mutableStateOf(options[2]) }
    Column(modifier.selectableGroup()) {
        options.forEach { item ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .selectable(
                        selected = (item == selectedOption),
                        onClick = {
                            onOptionSelected(item)
                            onSelected(item)
                        },
                        role = Role.RadioButton
                    )
                    .padding(horizontal = Grid.One, vertical = Grid.Half),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (item == selectedOption),
                    onClick = null // null recommended for accessibility with screenreaders
                )
                Text(
                    text = displayValue(item),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
    }
}
