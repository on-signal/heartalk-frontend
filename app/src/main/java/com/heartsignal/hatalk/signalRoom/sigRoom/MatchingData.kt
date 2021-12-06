package com.heartsignal.hatalk.signalRoom.sigRoom

import android.content.Intent
import com.cometchat.pro.core.CometChat
import com.heartsignal.hatalk.main.data.MatchingConfirmResponse
import com.heartsignal.hatalk.model.sigRoom.MatchingModel
import com.heartsignal.hatalk.model.sigRoom.MatchingUser

class MatchingData(
    private val intent: Intent,
    private val matchingModel: MatchingModel
) {
    fun setMatchingData() {
        val intent: Intent = intent
        val matchingData = intent.getParcelableExtra<MatchingConfirmResponse>("matchingData")

        val userID: String = CometChat.getLoggedInUser().uid.toString()

        matchingModel.setGroupName(matchingData?.groupName.toString())
        matchingModel.setCaller(matchingData?.caller.toString())

        matchingModel.setMyGender(matchingData?.gender.toString())
        matchingModel.setMyId(userID)

        when (userID) {
            matchingData?.room_info?.user1?.Id.toString() -> {
                matchingModel.setMyNickname(matchingData?.room_info?.user1?.nickname.toString())
                matchingModel.setMyIcon(matchingData?.room_info?.user1?.icon.toString())
            }
            matchingData?.room_info?.user2?.Id.toString() -> {
                matchingModel.setMyNickname(matchingData?.room_info?.user2?.nickname.toString())
                matchingModel.setMyIcon(matchingData?.room_info?.user2?.icon.toString())
            }
            matchingData?.room_info?.user3?.Id.toString() -> {
                matchingModel.setMyNickname(matchingData?.room_info?.user3?.nickname.toString())
                matchingModel.setMyIcon(matchingData?.room_info?.user3?.icon.toString())
            }
            matchingData?.room_info?.user4?.Id.toString() -> {
                matchingModel.setMyNickname(matchingData?.room_info?.user4?.nickname.toString())
                matchingModel.setMyIcon(matchingData?.room_info?.user4?.icon.toString())
            }
            matchingData?.room_info?.user5?.Id.toString() -> {
                matchingModel.setMyNickname(matchingData?.room_info?.user5?.nickname.toString())
                matchingModel.setMyIcon(matchingData?.room_info?.user5?.icon.toString())
            }
            matchingData?.room_info?.user6?.Id.toString() -> {
                matchingModel.setMyNickname(matchingData?.room_info?.user6?.nickname.toString())
                matchingModel.setMyIcon(matchingData?.room_info?.user6?.icon.toString())
            }
        }

        if (matchingData?.room_info?.user1?.gender.toString() == "0") {
            val manUser = MatchingUser(
                matchingData?.room_info?.user1?.Id.toString(),
                matchingData?.room_info?.user1?.nickname.toString(),
                matchingData?.room_info?.user1?.gender.toString(),
                matchingData?.room_info?.user1?.icon.toString()
            )
            matchingModel.appendManList(manUser)
        } else {
            val womanUser = MatchingUser(
                matchingData?.room_info?.user1?.Id.toString(),
                matchingData?.room_info?.user1?.nickname.toString(),
                matchingData?.room_info?.user1?.gender.toString(),
                matchingData?.room_info?.user1?.icon.toString()
            )
            matchingModel.appendWomanList(womanUser)
        }

        if (matchingData?.room_info?.user2?.gender.toString() == "0") {
            val manUser = MatchingUser(
                matchingData?.room_info?.user2?.Id.toString(),
                matchingData?.room_info?.user2?.nickname.toString(),
                matchingData?.room_info?.user2?.gender.toString(),
                matchingData?.room_info?.user2?.icon.toString()
            )
            matchingModel.appendManList(manUser)
        } else {
            val womanUser = MatchingUser(
                matchingData?.room_info?.user2?.Id.toString(),
                matchingData?.room_info?.user2?.nickname.toString(),
                matchingData?.room_info?.user2?.gender.toString(),
                matchingData?.room_info?.user2?.icon.toString()
            )
            matchingModel.appendWomanList(womanUser)
        }

        if (matchingData?.room_info?.user3?.gender.toString() == "0") {
            val manUser = MatchingUser(
                matchingData?.room_info?.user3?.Id.toString(),
                matchingData?.room_info?.user3?.nickname.toString(),
                matchingData?.room_info?.user3?.gender.toString(),
                matchingData?.room_info?.user3?.icon.toString()
            )
            matchingModel.appendManList(manUser)
        } else {
            val womanUser = MatchingUser(
                matchingData?.room_info?.user3?.Id.toString(),
                matchingData?.room_info?.user3?.nickname.toString(),
                matchingData?.room_info?.user3?.gender.toString(),
                matchingData?.room_info?.user3?.icon.toString()
            )
            matchingModel.appendWomanList(womanUser)
        }

        if (matchingData?.room_info?.user4?.gender.toString() == "0") {
            val manUser = MatchingUser(
                matchingData?.room_info?.user4?.Id.toString(),
                matchingData?.room_info?.user4?.nickname.toString(),
                matchingData?.room_info?.user4?.gender.toString(),
                matchingData?.room_info?.user4?.icon.toString()
            )
            matchingModel.appendManList(manUser)
        } else {
            val womanUser = MatchingUser(
                matchingData?.room_info?.user4?.Id.toString(),
                matchingData?.room_info?.user4?.nickname.toString(),
                matchingData?.room_info?.user4?.gender.toString(),
                matchingData?.room_info?.user4?.icon.toString()
            )
            matchingModel.appendWomanList(womanUser)
        }

        if (matchingData?.room_info?.user5?.gender.toString() == "0") {
            val manUser = MatchingUser(
                matchingData?.room_info?.user5?.Id.toString(),
                matchingData?.room_info?.user5?.nickname.toString(),
                matchingData?.room_info?.user5?.gender.toString(),
                matchingData?.room_info?.user5?.icon.toString(),
            )
            matchingModel.appendManList(manUser)
        } else {
            val womanUser = MatchingUser(
                matchingData?.room_info?.user5?.Id.toString(),
                matchingData?.room_info?.user5?.nickname.toString(),
                matchingData?.room_info?.user5?.gender.toString(),
                matchingData?.room_info?.user5?.icon.toString()
            )
            matchingModel.appendWomanList(womanUser)
        }

        if (matchingData?.room_info?.user6?.gender.toString() == "0") {
            val manUser = MatchingUser(
                matchingData?.room_info?.user6?.Id.toString(),
                matchingData?.room_info?.user6?.nickname.toString(),
                matchingData?.room_info?.user6?.gender.toString(),
                matchingData?.room_info?.user6?.icon.toString(),
            )
            matchingModel.appendManList(manUser)
        } else {
            val womanUser = MatchingUser(
                matchingData?.room_info?.user6?.Id.toString(),
                matchingData?.room_info?.user6?.nickname.toString(),
                matchingData?.room_info?.user6?.gender.toString(),
                matchingData?.room_info?.user6?.icon.toString()
            )
            matchingModel.appendWomanList(womanUser)
        }

        for (item: String in matchingData?.question_list!!) {
            matchingModel.appendQuestionList(item)
        }
    }
}