package org.michaelbel.tjgram.data.persistence

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import org.michaelbel.tjgram.data.entity.AdvancedAccess
import org.michaelbel.tjgram.data.entity.Counters
import org.michaelbel.tjgram.data.entity.CoverUser
import org.michaelbel.tjgram.data.entity.SocialAccount

@Entity(tableName = "users")
data class LocalUser(
        @ColumnInfo(name = "id") @PrimaryKey var id: Int = 0,
        @ColumnInfo(name = "name") var name: String = "",
        @ColumnInfo(name = "karma") var karma: Long = 0L,
        @ColumnInfo(name = "date") var createdDate: Int = 0,
        @ColumnInfo(name = "date_rfc") var createdDateRFC: String = "",
        @ColumnInfo(name = "avatar_url") var avatarUrl: String = "",
        @ColumnInfo(name = "push_topic") var pushTopic: String = "",
        @ColumnInfo(name = "url") var url: String = "",
        @ColumnInfo(name = "user_hash") var userHash: String = "",

        // AdvancedAccess
        var advancedAccessHash: String = "",
        var needAdvancedAccess: Boolean = false,

        // AdvancedAccessActions
        var readComments: Boolean = false,
        var writeComments: Boolean = false,

        // Subscription
        @ColumnInfo(name = "tj_subscription") var tjSubscriptionActive: Boolean = false,
        var tjSubscriptionActiveUntil : Long = 0L,

        @Ignore val advancedAccess: AdvancedAccess? = null,
        @Ignore val counters: Counters? = null,
        @Ignore val cover: CoverUser? = null,
        @Ignore val socialAccounts: List<SocialAccount>? = null
)