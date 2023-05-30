package space.mrandika.dicogram.component

import android.content.Context
import android.graphics.Canvas
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import android.view.View
import com.google.android.material.textfield.TextInputEditText
import space.mrandika.dicogram.R

class DGEditText : TextInputEditText {

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }

    private fun init() {
        // Using the class value doesn't seems to work?
        // https://developer.android.com/reference/android/text/InputType#TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        // https://developer.android.com/reference/android/text/InputType#TYPE_TEXT_VARIATION_PASSWORD
        when (inputType) {
            // Email
            // Value show 33, the documentation show 32
            33 -> {
                checkEmailInput()
            }

            // Password
            // Value show 129, the documentation show 128
            129 -> {
                checkPasswordInput()
            }
        }
    }

    private fun checkEmailInput() {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Do nothing.
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                error = if (!Patterns.EMAIL_ADDRESS.matcher(s.toString()).matches()) {
                    resources.getString(R.string.error_email_invalid)
                } else {
                    null
                }
            }

            override fun afterTextChanged(s: Editable) {
                // Do nothing.
            }
        })
    }

    private fun checkPasswordInput() {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Do nothing.
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                error = if (s.toString().length < 8) {
                    resources.getString(R.string.error_password_invalid)
                } else {
                    null
                }
            }

            override fun afterTextChanged(s: Editable) {
                // Do nothing.
            }
        })
    }
}