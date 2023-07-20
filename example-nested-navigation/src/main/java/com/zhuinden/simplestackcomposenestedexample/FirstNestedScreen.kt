package com.zhuinden.simplestackcomposenestedexample

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.ServiceBinder
import com.zhuinden.simplestackcomposeintegration.core.BackstackProvider
import com.zhuinden.simplestackcomposeintegration.services.rememberService
import com.zhuinden.simplestackextensions.servicesktx.add
import com.zhuinden.simplestackextensions.servicesktx.rebind
import kotlinx.parcelize.Parcelize

class FirstNestedModel(
    val backstack: Backstack
): FirstNestedScreen.ActionHandler {
    override fun navigateToSecond() {
        backstack.goTo(SecondNestedKey())
    }

    override fun goToParentThird() {
        backstack.parentServices?.goTo(ThirdKey)
    }
}

@Immutable
@Parcelize
data class FirstNestedKey(val title: String) : ComposeKey() {
    constructor() : this("Hello First Nested Screen!")

    @Composable
    override fun ScreenComposable(modifier: Modifier) {
        FirstNestedScreen(title, modifier)
    }

    override fun bindServices(serviceBinder: ServiceBinder) {
        with(serviceBinder) {
            val firstModel = FirstNestedModel(backstack)

            add(firstModel)
            rebind<FirstNestedScreen.ActionHandler>(firstModel)
        }
    }
}

class FirstNestedScreen private constructor() {
    interface ActionHandler {
        fun navigateToSecond()
        fun goToParentThird()
    }

    companion object {
        @Composable
        @SuppressLint("ComposableNaming")
        operator fun invoke(title: String, modifier: Modifier = Modifier) {
            val eventHandler = rememberService<ActionHandler>()
            val firstModel = rememberService<FirstNestedModel>()

            Column(
                modifier = modifier
                    .background(Color(0x80, 0x80, 0xFF))
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(onClick = {
                    // onClick is not a composition context, must get ambients above
                    eventHandler.navigateToSecond()
                }, content = {
                    Text(title)
                })
                if (firstModel.backstack.parentServices != null) {
                    Button(onClick = {
                        eventHandler.goToParentThird()
                    }, content = {
                        Text("Go to parent third")
                    })
                }
            }
        }
    }
}

@Preview
@Composable
fun FirstNestedScreenPreview() {
    BackstackProvider(backstack = Backstack()) {
        MaterialTheme {
            FirstNestedScreen("This is a preview")
        }
    }
}
