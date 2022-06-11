package ru.turev.hiltcorrutinescicerone.view

import android.graphics.Path
import java.io.Writer

class Move(private val x: Float, private val y: Float) : Action {
    override fun getTargetX(): Float {
        return x
    }

    override fun getTargetY(): Float {
        return y
    }

    override fun perform(path: Path) {
        path.moveTo(x, y)
    }

    override fun perform(writer: Writer) {
        writer.write("M$x,$y")
    }
}
