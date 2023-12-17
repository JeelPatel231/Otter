package tel.jeelpa.otter.ui.generic

import android.text.InputFilter
import android.text.Spanned

class InputFilterMinMax(private var min: Int, private var max: Int) : InputFilter {

//    constructor(min:String, max:String) {
//        this.min = Integer.parseInt(min)
//        this.max = Integer.parseInt(max)
//    }

    override fun filter(source:CharSequence, start:Int, end:Int, dest: Spanned, dstart:Int, dend:Int): CharSequence? {
        try {
            val input = Integer.parseInt(dest.toString() + source.toString())
            if (isInRange(min, max, input))
                return null
        } catch (_:NumberFormatException) {}
        return ""
    }
    private fun isInRange(a:Int, b:Int, c:Int):Boolean {
        return if (b > a) c in a..b else c in b..a
    }
}