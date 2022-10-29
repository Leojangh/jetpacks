import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.io.inputStream
import java.io.IOException
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString

val actionId = "jump2CurrentActivitySourceFile"

val sdkDir: String = Properties().run {
    val properties = Properties()
    try {
        properties.load(
            Path(
                ide.project.basePath ?: error("Can't retrieve project path!"),
                "local.properties"
            ).inputStream()
        )
        properties.getProperty("sdk.dir")
    } catch (e: IOException) {
        error("local.properties not found!")
    } finally {
        properties.clear()
    }
}
val adb = Path(sdkDir, "platform-tools", "adb").absolutePathString()
val pattern = Regex("""(top|m)ResumedActivity: ActivityRecord\{\w+ u\d+ (.+)/(.+) t\d+}""")

ide.registerAction(actionId, actionId) { e ->
    //目前只支持一台设备连接的情况
    val pb = ProcessBuilder(
        adb,
        "shell",
        "dumpsys",
        "activity",
        "|",
        "grep",
        "ResumedActivity"
    )
    val output = pb.start().inputStream.bufferedReader().readText()
    val matchResult = pattern.find(output) ?: error("Can't find ResumedActivity from adb.")
    val (_, pkg, activity) = matchResult.destructured
    val fqActivityName = if (activity.startsWith('.')) pkg + activity else activity
    JavaPsiFacade.getInstance(ide.project).findClass(
        fqActivityName,
        GlobalSearchScope.allScope(ide.project)
    )?.let {
        OpenFileDescriptor(ide.project, it.containingFile.virtualFile).navigate(true)
    }

}
ide.addShortcut(actionId, "ctrl 1")
