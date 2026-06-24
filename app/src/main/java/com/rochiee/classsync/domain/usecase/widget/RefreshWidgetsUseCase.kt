package com.rochiee.classsync.domain.usecase.widget

import android.content.Context
import com.rochiee.classsync.widget.ClassSyncWidgetUpdater

class RefreshWidgetsUseCase(
    private val context: Context
) {
    operator fun invoke() {
        ClassSyncWidgetUpdater.updateAllWidgets(context)
    }
}
