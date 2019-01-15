package com.caseyjbrooks.netlify.apps.slack


const val USERS_MENTION = "\\s*?<@(\\w+)>\\s*?"
const val USERS_MENTION_NO_CAPTURE = "\\s*?<@\\w+>\\s*?"
const val THING_MENTION = "\\s*?(.+)\\s*?"

const val PLUS_PLUS = "\\+\\+"
const val MINUS_MINUS = "(?:--|—)"
const val PLUS_EQUAL = "\\+=\\s*?(\\d+)"
const val MINUS_EQUAL = "-=\\s*?(\\d+)"
const val REASON = "(.*)"

val PLUS_PLUS_USERS_REGEX    = "^$USERS_MENTION$PLUS_PLUS$REASON$".toRegex()
val PLUS_PLUS_THING_REGEX    = "^$THING_MENTION$PLUS_PLUS$REASON$".toRegex()
val MINUS_MINUS_USERS_REGEX  = "^$USERS_MENTION$MINUS_MINUS$REASON$".toRegex()
val MINUS_MINUS_THING_REGEX  = "^$THING_MENTION$MINUS_MINUS$REASON$".toRegex()

val PLUS_EQUAL_USERS_REGEX   = "^$USERS_MENTION$PLUS_EQUAL$REASON$".toRegex()
val PLUS_EQUAL_THING_REGEX   = "^$THING_MENTION$PLUS_EQUAL$REASON$".toRegex()
val MINUS_EQUAL_USERS_REGEX  = "^$USERS_MENTION$MINUS_EQUAL$REASON$".toRegex()
val MINUS_EQUAL_THING_REGEX  = "^$THING_MENTION$MINUS_EQUAL$REASON$".toRegex()

val TOP_X_MENTION_REGEX = "top (\\d+)$".toRegex()
val BOTTOM_X_MENTION_REGEX = "bottom (\\d+)$".toRegex()
val USER_SCORE_REGEX = "score of$USERS_MENTION$".toRegex()
val THING_SCORE_REGEX = "score of$THING_MENTION$".toRegex()

val USER_REASONS_WHY_REGEX = "(\\d*)\\s?reasons why$USERS_MENTION$".toRegex()
val THING_REASONS_WHY_REGEX = "(\\d+)?\\s?reasons why$THING_MENTION$".toRegex()
