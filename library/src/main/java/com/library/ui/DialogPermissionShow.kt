package com.library.ui

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.library.model.DialogShow

/**
 * @project: YbPermission
 * @class: DialogPermissionShow.class
 * @content:用于展示权限被初次拒绝后提示的dialog的具体样式
 * @date: 2025/12/26
 * @author: byb
 */
object DialogPermissionShow {

    /**
     *  @describe: 默认的dialog提示具体样式
     *  @params: context-上下文，dialogShow-样式的具体标题与正文，dialogCallback:函数参数用于调用时执行发起权限申请逻辑
     *  @return:
     */
    fun setDialogDefault(context: Context,dialogShow: DialogShow.DialogDefault
    ,defaultCallback:() -> Unit){
        Log.i("DialogPermissionShow", "setDialogDefault: ")
        AlertDialog.Builder(context)
            .setTitle(dialogShow.title)
            .setMessage(dialogShow.message)
            .setPositiveButton("开启"){dialog, _ ->
                Log.i("DialogPermissionShow", "setDialogDefault: 确定")
                defaultCallback() //执行发起权限申请
                dialog.dismiss()
            }
            .setNegativeButton("取消"){dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

}