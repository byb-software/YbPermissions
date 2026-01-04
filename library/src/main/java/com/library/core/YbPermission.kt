package com.library.core

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.annotation.Keep
import androidx.fragment.app.Fragment
import com.library.model.DialogShow
import com.library.model.PermissionRequestMode
import com.library.model.PermissionStateData
import com.library.register.PermissionRegister

/**
 * @project: YbPermission
 * @class: PermissionBuilder.class
 * @content:供外部传入参数的类
 * @date: 2025/12/27
 * @author: byb
 */

object YbPermission {

    /**
     *  @describe: 用于生成专属activity的Builder
     *  @params: activity:当前申请权限的activity
     *  @return:
     */
    @Keep
    fun with(activity: ComponentActivity) = Builder(activity, null)


    /**
     *  @describe: 用于生成专属fragment的Builder
     *  @params: fragment:当前申请权限的fragment
     *  @return:
     */
    @Keep
    fun with(fragment: Fragment) = Builder(null, fragment)


    /**
     * 建造类，用于对外提供框架配置
     *
     * @property activity
     * @property fragment
     */

    class Builder(val activity: ComponentActivity?, val fragment: Fragment?) {

        //必选权限参数
        private var permissions: List<String>? = null //申请的权限数组


        //可选型参数
        private var dialogShow: DialogShow = DialogShow.DialogDefault() //初次拒绝时dialog提示的样式
        private var checkerRequestAction: ((notGrantedPermissions: List<String>, proceed: (permissionRequestMode: PermissionRequestMode) -> Unit) -> Unit)? =
            null //权限检测后的回调，可以接收到检测未授予的权限，再通过不同的启动方式去开通权限
        private var resultCallback: ((permissionStateData: PermissionStateData) -> Unit)? =
            null //每次申请权限后的返回结果
        private var dialogCallback: ((deniedPermission: List<String>, proceed: (permissionRequestMode: PermissionRequestMode) -> Unit) -> Unit)? =
            null //用于dialog中暂时被拒绝的权限接收，再通过不同模式的设置去进一步处理此权限

        @Keep
        //配置申请的具体权限
        fun permissions(vararg permissions: String) = apply {
            this.permissions = permissions.toList()
        }

        @Keep
        //配置权限检测后进行申请的方式
        fun checkerRequestAction(
            checkerRequestAction: ((notGrantedPermissions: List<String>, proceed: (permissionRequestMode: PermissionRequestMode) -> Unit) -> Unit)?
        ) = apply {
            this.checkerRequestAction = checkerRequestAction
        }

        @Keep
        //配置初次拒绝后进行弹窗提示的样式
        fun dialogShow(
            dialogShow: DialogShow = DialogShow.DialogDefault(),
            dialogCallback: ((denied: List<String>, proceed: (permissionRequestMode: PermissionRequestMode) -> Unit) -> Unit)?
        ) = apply {
            this.dialogShow = dialogShow
            this.dialogCallback = dialogCallback
        }

        @Keep
        //配置权限申请结果的回调
        fun resultCallback(resultCallback: (permissionStateData: PermissionStateData) -> Unit) =
            apply {
                this.resultCallback = resultCallback
            }


        /**
         *  @describe: 供外部使用的方法，用于发起框架的申请流程
         *  @params:
         *  @return:
         */
        @Keep
        fun request() {

            //校验必选参数
            if (permissions.isNullOrEmpty()) IllegalArgumentException("请配置需要申请的权限")

            //创建核心管理器
            val permissionManager = PermissionManager(PermissionRegister(activity, fragment))

            Log.i(
                "YbPermission", "request: " +
                        "permissions=$permissions," +
                        "dialogShow=$dialogShow," +
                        "checkerRequestAction=$checkerRequestAction," +
                        "resultCallback=$resultCallback," +
                        "dialogCallback=$dialogCallback"
            )

            //发起权限申请流程
            permissionManager.request(
                permissions!!,
                checkerRequestAction,
                dialogShow,
                dialogCallback,
                resultCallback
            )
        }
    }


}