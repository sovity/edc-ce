/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.modules.policy_utils.creator

import org.eclipse.edc.policy.model.Operator
import org.eclipse.edc.spi.monitor.Monitor

class PolicyComparator(
    private val monitor: Monitor
) {

    /**
     * Compares two sides with the given operators.
     *
     * Both left and right should only contain a single element. Due to the nature of the policy model, it could
     * be a list of items.
     *
     * We try to make the operators make sense as mathematically possible, without being too mean in terms of
     * edge case avoidance.
     */
    fun <T : Comparable<T>> compare(left: List<T>, operator: Operator, right: List<T>): Boolean {
        return when (operator) {
            Operator.EQ -> {
                left == right
            }

            Operator.NEQ -> {
                left != right
            }

            Operator.IN, Operator.IS_ANY_OF -> {
                right.toSet().containsAll(left.toSet())
            }

            Operator.IS_NONE_OF -> {
                right.toSet().intersect(left.toSet()).isEmpty()
            }

            Operator.IS_ALL_OF -> {
                left.toSet().containsAll(right.toSet())
            }

            Operator.LT -> {
                val min = right.min()
                left.all { it < min }
            }

            Operator.LEQ -> {
                val min = right.min()
                left.all { it <= min }
            }

            Operator.GT -> {
                val max = right.max()
                left.all { it > max }
            }

            Operator.GEQ -> {
                val max = right.max()
                left.all { it >= max }
            }

            else -> {
                monitor.warning("Unsupported operator: $operator")
                false
            }
        }
    }



    /**
     * See [compare], but treats EQ like IN. This is useful for policy functions that support comma-separated
     * lists of values, where EQ should match any of the given values.
     */
    fun <T : Comparable<T>> compareWithEqWorkingLikeIn(left: List<T>, operator: Operator, right: List<T>): Boolean =
        if (operator == Operator.EQ) {
            compare(left, Operator.IN, right)
        } else {
            compare(left, operator, right)
        }
}
