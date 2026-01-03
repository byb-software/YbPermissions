package com.library.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log

/**
 * @project: YbPermission
 * @class: PermissionIntent.class
 * @content:用来跳转到权限开启页或指定权限设置页的工具类
 * @date: 2025/12/27
 * @author: byb
 */
object PermissionIntent {
    /**
     *  @describe: 用来跳转对应设置页面，默认为当前应用的权限开通页面
     *  @params: context-上下文，action-自定义action
     *  @return: 
     */
    @SuppressLint("QueryPermissionsNeeded")
    fun navigationToSetting(context: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts(
                "package", //注意就是"package",不用改成自己的包名
                context.packageName,
                null
            )
            flags = Intent.FLAG_ACTIVITY_NEW_TASK

        }
        if (intent.resolveActivity(context.packageManager) != null) {
            Log.e("PermissionIntent", "startActivity")
            context.startActivity(intent)
        } else {
            Log.e("PermissionIntent", "没有能够处理该intent的页面，请确认输入的action")
        }
    }
}