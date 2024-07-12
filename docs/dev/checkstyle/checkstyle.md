# Checkstyle

## Note about Copyrights

While it's close to impossible to write a regex to validate all the possible copyright messages, the one currently configured in [checkstyle-config.xml](checkstyle-config.xml) matches almost all the files in this project.

Failing to use this template will make it progressively harder to fix the checkstyle errors as the copyright warnings will accumulate and dilute those errors, wasting precious brain time.

IntelliJ has a feature to help maintain consistent copyright headers in

`Settings > Editor > Copyright > Copyright profiles`.

The copyright below passes the checkstyle when put in a Java multiline comment.

```
Copyright (c) 2024 sovity GmbH

 This program and the accompanying materials are made available under the
 terms of the Apache License, Version 2.0 which is available at
 https://www.apache.org/licenses/LICENSE-2.0

 SPDX-License-Identifier: Apache-2.0

 Contributors:
      sovity GmbH - initial API and implementation

```

Which once inserted at the top of a file will look like

```java
/*
 *  Copyright (c) 2024 sovity GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       sovity GmbH - initial API and implementation
 *
 */

package foo.bar;
```
