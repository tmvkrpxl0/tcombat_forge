package com.tmvkrpxl0.tcombat.common.util

abstract class ForgeRunnable: Runnable {
    private var cancelled = false
    open fun isCancelled(): Boolean = cancelled
    open fun setCancelled(cancel: Boolean){
        this.cancelled = cancel
    }

}