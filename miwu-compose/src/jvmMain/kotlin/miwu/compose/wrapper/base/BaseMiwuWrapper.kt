package miwu.compose.wrapper.base

import androidx.compose.runtime.Composable
import miwu.miot.model.att.SpecAtt
import miwu.support.api.Controller
import miwu.support.base.MiwuWidget

abstract class BaseMiwuWrapper<T>(val widget: MiwuWidget<T>) : Controller {

    private var canUpdate = true
    internal val propertyListenerList = mutableMapOf<Pair<Int, Int>, (Any) -> Unit>()
    internal val actionListenerList = mutableMapOf<Pair<Int, Int>, (Any) -> Unit>()

    val description get() = widget.description
    val descriptionTranslation get() = widget.descriptionTranslation
    val translateHelper get() = widget.translateHelper
    val actionName get() = widget.actionName
    val serviceName get() = widget.serviceName
    val propertyName get() = widget.propertyName
    val siid get() = widget.siid
    val piid get() = widget.piid
    val aiid get() = widget.aiid
    val icon get() = widget.icon
    val valueList get() = widget.valueList
    val valueRange get() = widget.valueRange
    val valueStep get() = widget.valueStep
    val valueOriginUnit get() = widget.valueOriginUnit
    val valueUnit get() = widget.valueUnit
    val defaultValue get() = widget.defaultValue

    val Pair<T, T>.from get() = first
    val Pair<T, T>.to get() = second

    @Composable
    abstract fun Content()

    abstract fun onUpdateValue(value: T)
    open fun onActionCallback(value: Any) = Unit

    fun stopUpdate() {
        canUpdate = false
    }

    fun continueUpdate() {
        canUpdate = true
    }

    override fun onActionCallback(siid: Int, aiid: Int, output: Any) {
        if (widget.isMultiAttribute)
            actionListenerList[siid to piid]?.invoke(output)
        if (siid != this.siid || aiid != this.aiid) return
        if (!canUpdate) return
        onActionCallback(output)
    }

    @Suppress("UNCHECKED_CAST")
    override fun onUpdateValue(
        siid: Int, piid: Int, value: Any
    ) {
        if (widget.isMultiAttribute)
            propertyListenerList[siid to piid]?.invoke(value)
        if (siid != this.siid || piid != this.piid) return
        if (!canUpdate) return
        onUpdateValue(value as T)
    }

    fun update(value: T) {
        widget.update(value)
    }

    fun action(vararg input: Any) {
        widget.action(*input)
    }

    fun action(siid: Int, aiid: Int, vararg input: Any) {
        widget.action(siid, aiid, *input)
    }

    init {
        widget.bind(this)
    }

    fun getService(serviceName: String): SpecAtt.Service? = widget.getService(serviceName)

    fun getAction(serviceName: String, actionName: String): SpecAtt.Service.Action? =
        widget.getAction(serviceName, actionName)

    fun getProperty(serviceName: String, propertyName: String): SpecAtt.Service.Property? =
        widget.getProperty(serviceName, propertyName)

    fun register(serviceName: String, propertyName: String, block: (Any) -> Unit) {
        val siid = getService(serviceName)?.iid ?: return
        val piid = getProperty(serviceName, propertyName)?.iid ?: return
        register(siid, piid)
        propertyListenerList[siid to piid] = block
    }

    fun register(siid: Int, piid: Int) {
        if (widget.isMultiAttribute)
            widget.register(siid, piid)
    }

    open fun init() {
        initWrapper()
    }

    abstract fun initWrapper()
}