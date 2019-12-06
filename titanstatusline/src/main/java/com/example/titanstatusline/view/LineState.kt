package com.example.titanstatusline.view

import androidx.annotation.ColorRes
import com.example.titanstatusline.R

class LineState(private var status: String, private var state: State) {

    constructor(state: State) : this("", state)

    private var textWidth: Int = 0


    fun getStatus(): String {
        return status
    }

    fun setStatus(status: String) {
        this.status = status
    }

    fun getState(): State {
        return state
    }

    fun setState(state: State) {
        this.state = state
    }

    internal fun getTextWidth(): Int {
        return textWidth
    }

    internal fun setTextWidth(textWidth: Int) {
        this.textWidth = textWidth
    }

    sealed class State(val color: Int) {

        class LOCKED(@ColorRes color: Int = R.color.grey) : State(color)
        class UNLOCKED(@ColorRes color: Int = R.color.black) : State(color)
        class PASSED(@ColorRes color: Int = R.color.green) : State(color)
        class FAILED(@ColorRes color: Int = R.color.red) : State(color)
    }
}
