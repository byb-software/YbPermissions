package com.library.register

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner

/**
 * @project: YbPermission
 * @class: PermissionHost.class
 * @content:用于对接收的activity/fragment进行处理与register的注册处理
 * @date: 2025/12/26
 * @author: byb
 */
class PermissionRegister(activity: ComponentActivity?, fragment: Fragment?) {

    //判空
    val owner = fragment ?: activity ?: throw IllegalStateException("请配置 Activity/Fragment 载体")

    //context获取
    val context: Context
        get() = if (owner is Fragment) owner.requireContext() else (owner as Context)

    /**
     *  @describe: 注册返回结果
     *  @params: 返回结果的数据类型
     *  @return: 返回的对象用来启动申请
     */
    fun register(registerCallback: (Map<String, Boolean>) -> Unit): ActivityResultLauncher<Array<String>> {

        if ((owner as LifecycleOwner).lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED))
            throw IllegalStateException("请在 onStart()生命周期或之前使用with(this)，禁止在页面可见后注册")

        //注册返回结果
        return (owner as ActivityResultCaller).registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions(),
            registerCallback
        )
    }

}