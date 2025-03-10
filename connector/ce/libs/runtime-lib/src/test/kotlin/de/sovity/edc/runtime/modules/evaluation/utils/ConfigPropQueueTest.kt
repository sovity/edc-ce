/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.runtime.modules.evaluation.utils

import de.sovity.edc.runtime.modules.model.ConfigProp
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

class ConfigPropQueueTest {

    @Test
    fun test_addAll() {
        val a = prop("a")
        val a2 = prop("a")
        val b = prop("b")

        // []
        val queue = ConfigPropQueue()
        assertThat(queue.hasNext()).isFalse()

        // [("a", "a2"), ("b")]
        queue.addAll(listOf(a, a2, b))
        assertThat(queue.hasNext()).isTrue()
        assertThat(queue.nextProperty()).isEqualTo("a")
        assertThat(queue.nextProperty()).isEqualTo("a") // should not pop
        assertThat(queue.isVisited("a")).isFalse()
        assertThat(queue.isCompleted("a")).isFalse()

        // [("a", "a2") visited, ("b")]
        assertThat(queue.visit("a")).containsExactlyInAnyOrder(a, a2)
        assertThat(queue.hasNext()).isTrue()
        assertThat(queue.nextProperty()).isEqualTo("b")
        assertThat(queue.isVisited("a")).isTrue()
        assertThat(queue.isCompleted("a")).isFalse()

        // [("a", "a2") done, ("b")]
        queue.complete("a")
        assertThat(queue.hasNext()).isTrue()
        assertThat(queue.nextProperty()).isEqualTo("b")
        assertThat(queue.isVisited("a")).isTrue()
        assertThat(queue.isCompleted("a")).isTrue()

        // [("a", "a2") done, ("b") visited]
        assertThat(queue.visit("b")).containsExactlyInAnyOrder(b)
        assertThat(queue.hasNext()).isFalse()
        assertThat(queue.isVisited("a")).isTrue()
        assertThat(queue.isCompleted("a")).isTrue()
        assertThat(queue.isVisited("b")).isTrue()
        assertThat(queue.isCompleted("b")).isFalse()


        // [("a", "a2") done, ("b") done]
        queue.complete("b")
        assertThat(queue.hasNext()).isFalse()
        assertThat(queue.isVisited("b")).isTrue()
        assertThat(queue.isCompleted("b")).isTrue()
    }

    @Test
    fun readVisited() {
        val a = prop("a")
        val a2 = prop("a")

        val queue = ConfigPropQueue()
        queue.addAll(listOf(a))
        queue.visit("a")

        assertThatThrownBy { queue.addAll(listOf(a2)) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Properties 'a' have already been visited and are now being added again. This can happen if a property used for module decisions is also used in a child module. Whether the solution is to fix this *aLgOrItHm* or to 'not do what you tried to do' is up to you. Allowing for re-adding of properties that are 'completed' would be feasible but you'd have to add a check that the re-evaluated property did not end up producing another value and thus causing a conflict.")
    }

    @Test
    fun testRevisitVisited() {
        // arrange
        val a = prop("a")
        val b = prop("b")

        val queue = ConfigPropQueue()
        queue.addAll(listOf(a, b))
        queue.visit("a")

        // act & assert
        assertThatThrownBy { queue.visit("a") }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Property 'a' already visited.")
    }

    @Test
    fun testRevisitCompleted() {
        // arrange
        val a = prop("a")

        val queue = ConfigPropQueue()
        queue.addAll(listOf(a))
        queue.visit("a")
        queue.complete("a")

        // act & assert
        assertThatThrownBy { queue.visit("a") }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Property 'a' already visited.")
    }

    @Test
    fun testVisitUndefined() {
        // arrange
        val queue = ConfigPropQueue()

        // act & assert
        assertThatThrownBy { queue.visit("a") }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Property 'a' not defined at this moment.")
    }

    private fun prop(property: String): ConfigProp = mock {
        on { this.property } doReturn property
    }
}
