/*
 * Copyright 2025 sovity GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Contributors:
 *     sovity - init and continued development
 */
package de.sovity.edc.runtime.simple_di

import de.sovity.edc.runtime.simple_di.allowed.Allowed
import de.sovity.edc.runtime.simple_di.denied.Denied
import de.sovity.edc.runtime.simple_di.evaluation.MutableInstances
import org.assertj.core.api.AbstractThrowableAssert
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class SimpleDiTest {
    @Test
    fun `test instantiation`() {
        // arrange
        @Service
        class B

        @Service
        class A(val b: B)

        @Service
        class A2(val b: B)

        // act
        val instances = SimpleDi()
            .addAllowedPackage(A::class.java.packageName)
            .addClassToInstantiate(A::class.java)
            .addClassToInstantiate(A2::class.java)
            .toInstances()

        // assert
        val a = instances.getSingle<A>()
        val a2 = instances.getSingle<A2>()
        assertThat(a.b).isNotNull
        assertThat(a.b).isSameAs(a2.b)
    }

    @Test
    fun `test missing @Service annotation`() {
        // arrange
        class B

        @Service
        class A(val b: B)

        // act & assert
        assertThatThrownBy {
            SimpleDi()
                .addAllowedPackage(A::class.java.packageName)
                .addClassToInstantiate(A::class.java)
                .toInstances()
        }.hasDiException("test missing @Service annotation") { cls ->
            "Class is missing @Service and was not a part of otherDiContainers: ${cls("A")} -> ${cls("B")}"
        }
    }

    @Test
    fun `test primitive`() {
        // arrange
        @Service
        class A(val b: String)

        // act & assert
        assertThatThrownBy {
            SimpleDi()
                .addAllowedPackage(A::class.java.packageName)
                .addClassToInstantiate(A::class.java)
                .toInstances()
        }.hasDiException("test primitive") { cls ->
            "Class is missing @Service and was not a part of otherDiContainers: ${cls("A")} -> java.lang.String"
        }
    }

    @Test
    fun `test implementation provided`() {
        // arrange
        abstract class B

        @Service
        class BImpl : B()

        @Service
        class A(val b: B)

        // act
        val instances = SimpleDi()
            .addAllowedPackage(A::class.java.packageName)
            .addClassToInstantiate(A::class.java)
            .addInstance(BImpl())
            .toInstances()

        // assert
        val a = instances.getSingle<A>()
        assertThat(a.b).isInstanceOf(BImpl::class.java)
    }

    @Test
    fun `test implementation missing`() {
        // arrange
        abstract class B

        @Service
        class BImpl : B()

        @Service
        class A(val b: B)

        // act & assert
        assertThatThrownBy {
            SimpleDi()
                .addAllowedPackage(A::class.java.packageName)
                .addClassToInstantiate(A::class.java)
                .toInstances()
        }.hasDiException("test implementation missing") { cls ->
            "Class is abstract or an interface: ${cls("A")} -> ${cls("B")} Add your implementation manually or add an 'OtherDiContainer' to let the DI find the implementation"
        }
    }

    @Test
    fun `test otherDiContainer`() {
        // arrange
        class B

        @Service
        class A(val b: B)

        // act
        val instances = SimpleDi()
            .addAllowedPackage(A::class.java.packageName)
            .addClassToInstantiate(A::class.java)
            .addOtherDiContainer(otherDiContainer(B()))
            .toInstances()

        // assert
        val a = instances.getSingle<A>()
        assertThat(a.b).isInstanceOf(B::class.java)
    }

    @Test
    fun `test instance created interceptor`() {
        // arrange
        class B

        @Service
        class A(val b: B)

        val created = mutableListOf<Any>()

        // act
        val instances = SimpleDi()
            .addAllowedPackage(A::class.java.packageName)
            .addClassToInstantiate(A::class.java)
            .addOtherDiContainer(otherDiContainer(B()))
            .onInstanceCreated { created.add(it) }
            .toInstances()

        // assert
        val a = instances.getSingle<A>()
        assertThat(created).isEqualTo(listOf(a))
    }

    @Test
    fun `test circular dependency`() {
        // arrange
        @Service
        class A(val a: A)

        // act & assert
        assertThatThrownBy {
            SimpleDi()
                .addAllowedPackage(A::class.java.packageName)
                .addClassToInstantiate(A::class.java)
                .toInstances()
        }.hasDiException("test circular dependency") { cls ->
            "Circular dependency detected: ${cls("A")} -> ${cls("A")}"
        }
    }

    @Test
    fun `test two constructors`() {
        // arrange
        @Service
        class A {
            constructor()
            constructor(b: String)
        }

        // act & assert
        assertThatThrownBy {
            SimpleDi()
                .addAllowedPackage(A::class.java.packageName)
                .addClassToInstantiate(A::class.java)
                .toInstances()
        }.hasDiException("test two constructors") { cls ->
            "${cls("A")}: Exception during instantiation: Must have exactly one accessible constructor"
        }
    }


    private fun AbstractThrowableAssert<*, *>.hasDiException(
        fnName: String,
        messageFn: (clsNameFn: (cls: String) -> String) -> String
    ): AbstractThrowableAssert<*, *> {
        // These nested classes are horrible to read, this test just tries to make it a bit more readable
        val classNameFn = { cls: String -> "${SimpleDiTest::class.java.name}\$$fnName\$$cls" }
        return this.hasMessage(messageFn(classNameFn))
    }

    private fun otherDiContainer(vararg implementations: Any): OtherDiContainer {
        val instances = MutableInstances()
        instances.addAll(implementations.toList())
        return object : OtherDiContainer {
            override fun has(clazz: Class<out Any>): Boolean =
                instances.has(clazz)

            override fun getSingle(clazz: Class<out Any>): Any =
                instances.getSingle(clazz)

        }
    }

    @Test
    fun `prevent instantiation of classes that are not in allowed packages`() {
        assertThat(
            assertThrows<SimpleDi.SimpleDiException> {
                SimpleDi()
                    .addAllowedPackage(Allowed::class.java.packageName)
                    .addClassToInstantiate(Allowed::class.java)
                    .addClassToInstantiate(Denied::class.java)
                    .toInstances()
            }
        ).hasMessage(
            "Can't instantiate ${Denied::class.java.canonicalName} because it is not in an allowed package. " +
                "Allowed packages are: ${Allowed::class.java.packageName}"
        )
    }
}
