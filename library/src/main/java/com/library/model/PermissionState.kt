package com.library.model

/**
 * @project: YbPermission
 * @class: PermissionState.class
 * @content:标记权限状态的枚举类
 * @date: 2025/12/26
 * @author: byb
 */
enum class PermissionState {
    Granted, //权限已授予
    Denied, //权限被拒绝过且未选择"不在询问"
    PermanentDenied //权限被拒绝且勾选"不在询问"，不展示dialog提示时只要被拒绝都归到此类
}