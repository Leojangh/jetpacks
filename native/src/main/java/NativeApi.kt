interface NativeApi {

    companion object {
        init {
            System.loadLibrary("rust")
        }

        external fun test(): Int
    }
}