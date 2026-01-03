package com.library.model

/**
 * @project: YbPermission
 * @class: DialogShow.class
 * @content:标记当前使用时权限被用户初次拒绝后展示dialog提示的样式
 * @date: 2025/12/26
 * @author: byb
 */
sealed class DialogShow {
    object DialogCustom : DialogShow() //展示自定义样式
    data class DialogDefault(val title: String = "权限申请",val message: String = "需要获取该权限才能正常使用此功能") : DialogShow() //展示默认样式
}