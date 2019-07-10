package com.caseyjbrooks.netlify

import com.caseyjbrooks.netlify.api.Clog
import com.caseyjbrooks.netlify.api.router.chat.ChatReply
import kotlin.test.assertTrue

class AssertionBuilder<T>(val subject: T) {
    fun describe(message: String) {
        Clog.i("$message: ")
    }

    fun pass(message: String) {
        Clog.i("  ✓ $message")
    }

    fun fail(message: String) {
        Clog.e("  ✗ $message")
        assertTrue(false)
    }
}

fun <T> expectThat(subject: T): AssertionBuilder<T> {
    return AssertionBuilder(subject)
}

fun <T> AssertionBuilder<T>.isEqualTo(other: Any) = apply {
    describe("[$subject] is equal to [$other]")
    if (subject == other) {
        pass("subject is equal to other")
    } else {
        fail("subject is equal to other")
    }
}

fun <T> AssertionBuilder<List<T>>.containsExactly(vararg other: T) = apply {
    describe("subject with [${subject.size}] elements contains exactly [\n  - ${other.joinToString("\n  - ")}\n]")
    if (subject == listOf(*other)) {
        pass("subject is equal to other")
    } else {
        fail("subject is equal to other. Subject had [\n  - ${subject.joinToString("\n  - ")}\n]")
    }
}

fun AssertionBuilder<ChatReply?>.hasReply(message: String) = apply {
    describe("A reply was sent with value [$message]")
    if (subject != null && subject.message == message) {
        pass("Correct reply was sent")
    } else if (subject != null && subject.message != message) {
        fail("The wrong reply was sent. Received [${subject.message}]")
    } else {
        fail("No reply was sent")
    }
}