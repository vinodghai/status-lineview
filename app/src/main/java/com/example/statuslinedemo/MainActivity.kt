package com.example.statuslinedemo

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.titanstatusline.view.LineState
import com.example.titanstatusline.view.StatusLineView

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var statusLineView: StatusLineView

    private val lineStateList: MutableList<LineState> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        statusLineView = findViewById(R.id.statusLineView)

        lineStateList.add(
            LineState(
                "Pre Init",
                LineState.State.LOCKED()
            )
        )
        lineStateList.add(
            LineState(
                "Start",
                LineState.State.UNLOCKED(R.color.colorPrimaryDark)
            )
        )
        lineStateList.add(
            LineState(
                "Process",
                LineState.State.PASSED()
            )
        )
        lineStateList.add(
            LineState(
                "Done Ok",
                LineState.State.FAILED(R.color.random)
            )
        )

        statusLineView.setStateList(lineStateList)
    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.btLockState1 -> lineStateList[0].setState(LineState.State.LOCKED())
            R.id.btLockState2 -> lineStateList[1].setState(LineState.State.LOCKED())
            R.id.btLockState3 -> lineStateList[2].setState(LineState.State.LOCKED())
            R.id.btLockState4 -> lineStateList[3].setState(LineState.State.LOCKED())


            R.id.btUnlockState1 -> lineStateList[0].setState(LineState.State.UNLOCKED())
            R.id.btUnlockState2 -> lineStateList[1].setState(LineState.State.UNLOCKED())
            R.id.btUnlockState3 -> lineStateList[2].setState(LineState.State.UNLOCKED())
            R.id.btUnlockState4 -> lineStateList[3].setState(LineState.State.UNLOCKED())


            R.id.btPassState1 -> lineStateList[0].setState(LineState.State.PASSED())
            R.id.btPassState2 -> lineStateList[1].setState(LineState.State.PASSED())
            R.id.btPassState3 -> lineStateList[2].setState(LineState.State.PASSED())
            R.id.btPassState4 -> lineStateList[3].setState(LineState.State.PASSED())

            R.id.btRejectState1 -> lineStateList[0].setState(LineState.State.FAILED())
            R.id.btRejectState2 -> lineStateList[1].setState(LineState.State.FAILED())
            R.id.btRejectState3 -> lineStateList[2].setState(LineState.State.FAILED())
            R.id.btRejectState4 -> lineStateList[3].setState(LineState.State.FAILED())
        }

        this.statusLineView.setStateList(lineStateList)
    }
}
