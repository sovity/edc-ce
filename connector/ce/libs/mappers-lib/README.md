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
## About this module

Contains multiple utilities for developing **with** edc connectors:

- Mappers for common sovity EDC API Models such as UiPolicy and UiAsset
- Utilities around `CatalogService#fetchCatalog` that returns `byte[]` in the Eclipse EDC

## Why does this module exist?

For use in either use case applications that want to reference the JSON-LD Vocab or EDC-related
components that want to be able to parse DCAT Data Offers in a readable way.
