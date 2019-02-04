package org.michaelbel.tjgram.data.entity

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import java.io.Serializable

data class SubsiteContacts  (
    @Expose @SerializedName("contacts") val contacts: String,
    @Expose @SerializedName("email") val email: String
    //@Expose @SerializedName("socials") val socials: List<SubsiteSocial>
): Serializable