package com.library.model

/**
 * @project: YbPermission
 * @class: PermissionStateData.class
 * @content:通过数据类存储申请的权限与该权限的当前状态
 * @date: 2025/12/26
 * @author: byb
 */
data class PermissionStateData(val state: Map<String, PermissionState>) {
    //是否所有权限都被授予
    val allGrantedPermission: Boolean = state.values.all { it == PermissionState.Granted }

    //所有未授权的权限，，包括shouldShowRequestPermissionRationale返回true的权限
    val deniedPermission: List<String>
        get() = state.filterValues { it == PermissionState.Denied }.keys.toList()
   //所有被永久拒绝的权限，即shouldShowRequestPermissionRationale返回false(android11之后两次拒绝则默认为勾选不在询问)
    val permanentDeniedPermission: List<String>
        get() = state.filterValues { it == PermissionState.PermanentDenied }.keys.toList()

}