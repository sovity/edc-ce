<!--
    Copyright 2025 sovity GmbH

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

    SPDX-License-Identifier: Apache-2.0

    Contributors:
        sovity - init and continued development
-->
The `@Service` annotation is a hint to tell that the class is allowed to be instantiated via introspection by the `SimpleDi` mechanism.

This instantiation by the `SimpleDi` happens when it was not able to find the desired class via the EDC's service loading mechanism (org.eclipse.edc.spi.system.ServiceExtensionContext.getService(java.lang.Class<T>)).

It would allow a misuse of this annotation where the developer can instantiate a class that would otherwise not be accessible from the EDC because it's not part of the loaded modules.
To prevent this from happening, a list of allowed packages must be specified, to allow the `SimpleDi` to instantiate safe classes and reject classes that are out of scope but may have been accidentally flagged with `@Service`.

Another pitfall is that even if the class is annotated with `@Service`, if the `SimpleDi` found it in one of the 'otherDiContainers', e.g. the EDC Services, it will not be instantiated via the default constructor.
