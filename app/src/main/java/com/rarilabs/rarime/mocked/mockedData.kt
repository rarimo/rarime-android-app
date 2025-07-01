package com.rarilabs.rarime.mocked

import com.rarilabs.rarime.api.points.models.BaseEvents
import com.rarilabs.rarime.api.points.models.PointsEventAttributes
import com.rarilabs.rarime.api.points.models.PointsEventData
import com.rarilabs.rarime.api.points.models.PointsEventMeta
import com.rarilabs.rarime.api.points.models.PointsEventMetaDynamic
import com.rarilabs.rarime.api.points.models.PointsEventMetaStatic
import com.rarilabs.rarime.api.points.models.PointsEventStatuses
import com.rarilabs.rarime.ui.components.MARKDOWN_CONTENT

val CONST_MOCKED_EVENTS_LIST = listOf(
    PointsEventData(
        id = "1",
        type = "balance",
        attributes = PointsEventAttributes(
            status = PointsEventStatuses.OPEN.value,
            createdAt = 0,
            updatedAt = 0,
            meta = PointsEventMeta(
                static = PointsEventMetaStatic(
                    name = BaseEvents.PASSPORT_SCAN.value,
                    reward = 50,
                    title = "Lorem ipsum 1",
                    description = MARKDOWN_CONTENT,
                    shortDescription = "Lorem ipsum dolor sit amet!",
                    frequency = "",
                    startsAt = null,
                    expiresAt = null,
                    actionUrl = "",
                    logo = "https://images.unsplash.com/photo-1717263608216-51a63715d209?q=80&w=3540&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                    flag = "",
                ),
                dynamic = PointsEventMetaDynamic(
                    id = "",
                ),
            ),
            pointsAmount = 0,
            balance = null,
        )
    ),
    PointsEventData(
        id = "2",
        type = "balance",
        attributes = PointsEventAttributes(
            status = PointsEventStatuses.CLAIMED.value,
            createdAt = 0,
            updatedAt = 0,
            meta = PointsEventMeta(
                static = PointsEventMetaStatic(
                    name = BaseEvents.REFERRAL_COMMON.value,
                    reward = 50,
                    title = "Lorem ipsum 2",
                    description = MARKDOWN_CONTENT,
                    shortDescription = "Lorem ipsum dolor sit amet concestetur!",
                    frequency = "",
                    startsAt = null,
                    expiresAt = "",
                    actionUrl = "",
                    logo = "https://images.unsplash.com/photo-1717263608216-51a63715d209?q=80&w=3540&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                    flag = ""
                ),
                dynamic = PointsEventMetaDynamic(
                    id = "",
                ),
            ),
            pointsAmount = 0,
            balance = null,
        ),
    ),
    PointsEventData(
        id = "3",
        type = "balance",
        attributes = PointsEventAttributes(
            status = PointsEventStatuses.FULFILLED.value,
            createdAt = 0,
            updatedAt = 0,
            meta = PointsEventMeta(
                static = PointsEventMetaStatic(
                    name = BaseEvents.FREE_WEEKLY.value,
                    reward = 500,
                    title = "Lorem ipsum 3",
                    description = MARKDOWN_CONTENT,
                    shortDescription = "Lorem ipsum dolor sit amet concestetur! Lorem ipsum dolor sit amet concestetur!",
                    frequency = "",
                    startsAt = null,
                    expiresAt = "",
                    actionUrl = "",
                    logo = "https://images.unsplash.com/photo-1717263608216-51a63715d209?q=80&w=3540&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                    flag = ""
                ),
                dynamic = PointsEventMetaDynamic(
                    id = "",
                ),
            ),
            pointsAmount = 0,
            balance = null,
        )
    ),
)