package com.rochiee.classsync.domain.usecase.notification

import android.content.Context
import com.rochiee.classsync.data.notification.NotificationPermissionHelper

class OpenNotificationAccessSettingsUseCase(
    private val context: Context
) {
    operator fun invoke() {
        NotificationPermissionHelper.openNotificationListenerSettings(context)
    }
}
