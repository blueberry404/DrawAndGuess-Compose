package root

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import home.HomeComponent

interface DAGRootComponent {

    val routerState: Value<ChildStack<*, DAGChild>>

    sealed class DAGChild {
        class HomeChild(val component: HomeComponent): DAGChild()
    }
}