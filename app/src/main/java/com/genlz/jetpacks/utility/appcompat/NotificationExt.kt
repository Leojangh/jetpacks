@file:Suppress("UNUSED")

package com.genlz.jetpacks.utility.appcompat

import android.app.Notification
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat

val Notification.extrasExt get() = NotificationCompat.getExtras(this)

val Notification.actionCountExt get() = NotificationCompat.getActionCount(this)

fun Notification.getActionExt(actionIndex: Int) = NotificationCompat.getAction(this, actionIndex)

val Notification.bubbleMetadataExt get() = NotificationCompat.getBubbleMetadata(this)

val Notification.invisibleActionsExt: List<NotificationCompat.Action>
    @RequiresApi(21)
    get() = NotificationCompat.getInvisibleActions(this)

val Notification.peoplesExt: List<androidx.core.app.Person>
    get() = NotificationCompat.getPeople(this)

val Notification.contentTitleExt
    @RequiresApi(19)
    get() = NotificationCompat.getContentTitle(this)

val Notification.contentTextExt
    @RequiresApi(19)
    get() = NotificationCompat.getContentText(this)

val Notification.contentInfoExt
    @RequiresApi(19)
    get() = NotificationCompat.getContentInfo(this)

val Notification.subTextExt
    @RequiresApi(19)
    get() = NotificationCompat.getSubText(this)

val Notification.categoryExt
    get() = NotificationCompat.getCategory(this)

val Notification.isLocalOnly get() = NotificationCompat.getLocalOnly(this)

val Notification.groupExt get() = NotificationCompat.getGroup(this)

val Notification.showWhenExt
    @RequiresApi(19)
    get() = NotificationCompat.getShowWhen(this)

val Notification.isUserChronometer
    @RequiresApi(19)
    get() = NotificationCompat.getUsesChronometer(this)

val Notification.isOnlyAlertOnce get() = NotificationCompat.getOnlyAlertOnce(this)

val Notification.isAutoCancel get() = NotificationCompat.getAutoCancel(this)

val Notification.isOnGoing get() = NotificationCompat.getOngoing(this)

val Notification.colorExt get() = NotificationCompat.getColor(this)

@NotificationCompat.NotificationVisibility
val Notification.visibilityExt
    get() = NotificationCompat.getVisibility(this)

val Notification.publicVersionExt get() = NotificationCompat.getPublicVersion(this)

val Notification.isGroupSummary get() = NotificationCompat.isGroupSummary(this)

val Notification.sortKeyExt get() = NotificationCompat.getSortKey(this)

val Notification.channelIdExt get() = NotificationCompat.getChannelId(this)

val Notification.timeoutAfterExt get() = NotificationCompat.getTimeoutAfter(this)

val Notification.badgeIconTypeExt get() = NotificationCompat.getBadgeIconType(this)

val Notification.shortcutIdExt get() = NotificationCompat.getShortcutId(this)

val Notification.settingsTextExt get() = NotificationCompat.getSettingsText(this)

val Notification.locusIdExt get() = NotificationCompat.getLocusId(this)

@NotificationCompat.GroupAlertBehavior
val Notification.groupAlertBehaviorExt
    get() = NotificationCompat.getGroupAlertBehavior(this)

val Notification.isAllowSystemGeneratedContextualActionsExt
    get() = NotificationCompat.getAllowSystemGeneratedContextualActions(this)











