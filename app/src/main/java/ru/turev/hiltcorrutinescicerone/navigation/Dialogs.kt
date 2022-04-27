package ru.turev.hiltcorrutinescicerone.navigation

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import ru.turev.hiltcorrutinescicerone.R

abstract class Dialogs {

    abstract fun subscription(imageUrl: String? = null): DialogFragment

//    fun rate(context: Context, onRate: () -> Unit): Dialog {
//        return DialogRateUsBinding.inflate(context.layoutInflater).run {
//            createDialog(context, root, isCancelable = true).also { dialog ->
//                btnCancel.setOnClickListener { dialog.dismiss() }
//                btnRate.setOnClickListener {
//                    onRate()
//                    dialog.dismiss()
//                }
//            }
//        }
//    }

//    fun subscribed(context: Context, onOk: () -> Unit): Dialog {
//        return DialogSubscribedBinding.inflate(context.layoutInflater).run {
//            createDialog(context, root).also { dialog ->
//                btnOk.setOnClickListener {
//                    dialog.dismiss()
//                    onOk()
//                }
//            }
//        }
//    }

//    fun editorExit(context: Context, onExit: () -> Unit): Dialog {
//        return DialogEditorExitBinding.inflate(context.layoutInflater).run {
//            createDialog(context, root).also { dialog ->
//                btnCancel.setOnClickListener { dialog.dismiss() }
//                btnExit.setOnClickListener {
//                    dialog.dismiss()
//                    onExit()
//                }
//            }
//        }
//    }

//    fun editorTutorial(
//        context: Context,
//        uri: Uri,
//        @StringRes titleResId: Int,
//        @StringRes messageResId: Int
//    ): Dialog {
//        return DialogEditorTutorialBinding.inflate(context.layoutInflater).run {
//            wrapPlayer.clipToOutline = true
//            textTitle.setText(titleResId)
//            textMessage.setText(messageResId)
//            createDialog(context, root).also { dialog ->
//                val player = SimpleExoPlayer.Builder(context)
//                    .build()
//                    .apply {
//                        repeatMode = Player.REPEAT_MODE_ONE
//                        setVideoTextureView(texturePlayer)
//                        addMediaItem(MediaItem.fromUri(uri))
//                    }
//
//                dialog.setOnShowListener { root.postDelayed(1000) { player.playWhenReady = true } }
//                btnOk.setOnClickListener {
//                    dialog.dismiss()
//                    player.release()
//                }
//                player.prepare()
//            }
//        }
//    }

    protected fun createDialog(
        context: Context,
        view: View,
        verticalGravity: Int = Gravity.BOTTOM,
        layoutHeight: Int = WindowManager.LayoutParams.WRAP_CONTENT,
        isCancelable: Boolean = false
    ): Dialog {
        val marginHorizontal = context.resources.getDimensionPixelSize(R.dimen.dialog_marginHorizontal)
        val verticalOffset = context.resources.getDimensionPixelSize(R.dimen.dialog_marginBottom)

        return Dialog(context, R.style.AlertDialogTheme).apply {
            setCancelable(isCancelable)
            setContentView(view)
            create()
            window?.run {
                setLayout(context.resources.displayMetrics.widthPixels - 2 * marginHorizontal, layoutHeight)
                setGravity(Gravity.CENTER_HORIZONTAL or verticalGravity)
                attributes.y = verticalOffset
            }
        }
    }

    val Context.layoutInflater: LayoutInflater get() = LayoutInflater.from(this)
}
