package com.genlz.share.sugar4kt

import android.os.Build
import androidx.annotation.RequiresApi
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
import java.util.function.Consumer
import java.util.function.Supplier

@RequiresApi(Build.VERSION_CODES.N)
class CompletableFutureEx {

    companion object {

        /**
         * @see CompletableFuture.thenAcceptAsync
         */
        fun <T> CompletableFuture<T>.thenAcceptAsyncKt(
            executor: Executor,
            action: Consumer<in T>,
        ) = thenAcceptAsync(action, executor)

        /**
         * @see CompletableFuture.supplyAsync
         */
        fun <U> supplyAsync(
            executor: Executor,
            supplier: Supplier<U>,
        ) = CompletableFuture.supplyAsync(supplier, executor)
    }
}