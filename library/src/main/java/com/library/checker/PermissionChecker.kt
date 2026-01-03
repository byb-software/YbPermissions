package com.library.checker

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.library.model.PermissionState
import com.library.model.PermissionStateData

/**
 * @project: YbPermission
 * @class: PermissionChecker.class
 * @content:检测类，用于权限申请过程中所需要的检测
 * @date: 2025/12/26
 * @author: byb
 */

object PermissionChecker {

    /**
     *  @describe: 检测当前权限是否被授权
     *  @params: context-上下文，permission-权限名
     *  @return: 返回键值对形式的权限名与其对应的状态
     */
    fun checkPermission(
        context: Context,
        permissions: List<String>
    ): PermissionStateData {
        val states = mutableMapOf<String, PermissionState>()
        permissions.forEach { permission ->
            val isGranted = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
            states[permission] =  if(isGranted) PermissionState.Granted else PermissionState.Denied
        }
        Log.i("PermissionChecker", "checkPermission:states=${states} ")
        return PermissionStateData(states)
    }

    /**
     *  @describe: 适配activity
     *    用shouldShowRequestPermissionRationale检测是否为永久拒绝
     *  @params:
     *     target:申请权限的当前页面，activity/fragment
     *     permission:权限名
     *  @return: true->用户拒绝后未勾选不在询问，false->用户拒绝后勾选不在询问(Android11后两次拒绝默认勾选)
     */
    fun classifyDeniedActivity(target: Any, permission: String): Boolean? {
        when(target){
            is Fragment ->{
                Log.i("PermissionChecker", "classifyDeniedActivity:fragment ")
                return target.shouldShowRequestPermissionRationale(permission)}
            is Activity ->{
                Log.i("PermissionChecker", "classifyDeniedActivity:Activity ")

                return target.shouldShowRequestPermissionRationale(permission)}
        }
        Log.i("PermissionChecker", "classifyDeniedActivity is null")

       return null
    }

}