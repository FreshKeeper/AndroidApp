package com.freshkeeper.service

import com.freshkeeper.model.Membership

interface MembershipService {
    suspend fun getMembershipStatus(): Membership
}
