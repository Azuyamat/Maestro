package interfaces

import annotations.Command
import utils.firstWord
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.findAnnotation

interface RecordPlayerInterface {
    val simpleName: String
        get() = this::class.simpleName ?: throw Exception("No simple name found")
    val simplerName: String?
        get() = simpleName.firstWord()
    val permission: Any
    val functions: List<KFunction<*>>
        get() = this::class.declaredMemberFunctions.toList()
    val mainFunction: KFunction<*>?
        get() = functions.firstOrNull { it.name == "main" || it.name == "execute" || it.name == this.simplerName }
    val otherFunctions: List<KFunction<*>>?
        get() = functions.filter { it.name != mainFunction?.name }
    val useAnnotation: Boolean
        get() = this::class.findAnnotation<Command>()?.useAnnotation?:true

    fun addSubcommand(builder: Any, obj: KFunction<*>)
    fun addGroup(builder: Any, cls: KClass<*>)
    fun addCommand(builder: Any)
    fun addOption(builder: Any, param: KParameter)
}