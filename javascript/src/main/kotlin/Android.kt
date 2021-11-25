/**
 * TODO: use codegen for this.
 * The Javascript bridge supplied by Android.
 */
external object Android {

    fun toast(msg: String)

    /**
     * Although the parameter's type is [Double] at here while the bridge
     * method define [Float],javascript numeric type is insensitive.
     */
    fun transit(left: Double, top: Double, right: Double, bottom: Double)
}