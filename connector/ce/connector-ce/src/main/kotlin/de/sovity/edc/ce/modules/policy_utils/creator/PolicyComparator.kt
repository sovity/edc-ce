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

            Operator.IN -> {
                right.toSet().containsAll(left.toSet())
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

            // TODO: Implement IS_ALL_OF, IS_ANY_OF, IS_NONE_OF, IS_ONE_OF

            else -> {
                monitor.warning("Unsupported operator: $operator")
                false
            }
        }
    }

}
