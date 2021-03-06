
external object Log {

    fun v(tag: String?, msg: String)

    fun v(tag: String?, msg: String?, t: Throwable?)

    fun i(tag: String, msg: String)

    fun i(tag: String, msg: String?, t: Throwable?)

    fun d(tag: String, msg: String)

    fun d(tag: String, msg: String?, t: Throwable?)

    fun w(tag: String, msg: String)

    fun w(tag: String, msg: String?, t: Throwable?)

    fun e(tag: String?, msg: String)

    fun e(tag: String?, msg: String, t: Throwable?)
}