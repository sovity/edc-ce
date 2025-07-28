# Example Use Case App Tutorial - Chat App

Welcome to the **Chat App** tutorial!

In this guide, you'll learn step-by-step how to build the EDC interaction for the example Blueprint Use Case Application **Chat App**.

Whether you're new to the sovity EDC or looking for practical implementation examples, this tutorial will walk you through key concepts and coding tasks.

Let's get started!

## Prerequisites

See parent [README](../README.md) for prerequisites.

## Getting Started

### 1. Clone the repository

```bash
# Bash or Git Bash
mkdir edc-ce && cd edc-ce
git clone https://github.com/sovity/edc-ce.git .
cd examples/chat-app
```

### 2. Check Prerequisites

```bash
# Bash or Git Bash

# Starting the finished example should work
(cd source-final && ./start.sh)
```

Two Chat App UIs should be available under http://localhost:13000 and http://localhost:23000 respectively.

### 3. Opening the Tutorial Starter

```bash
# Bash or Git Bash

cd source-starter-for-tutorial
```

This project contains three points of interest:

- The [frontend](../source-starter-for-tutorial/frontend) written with Next.js, React, ShadCN-UI and Tailwind
  - The frontend is complete. This tutorial focuses on the backend's interactions with the EDC
- The [backend](../source-starter-for-tutorial/backend) written with Quarkus, Kotlin and Gradle
  - The backend will be completed in the scope of this tutorial.
- A [Docker Compose Project](../source-starter-for-tutorial/docker-compose) starting two EDCs and two Chat Apps that will be used throughout the tutorial.
  - This is currently the only way to develop and start this project.

### 4. Follow the Tutorial

The tutorial will take you through multiple areas of interest to demonstrate different mechansims of how we currently think use case applications should be built.

- [Chapter 1: Including Libraries](./chapter-1-include-libraries.md)
- [Chapter 2: Auto-Setup your EDC](./chapter-2-auto-setup-edc.md)
- [Chapter 3: Establishing New Connections](./chapter-3-establishing-connections.md)
- [Chapter 4: Receiving Messages](./chapter-4-receiving-messages.md)
- [Chapter 5: Sending Messages](./chapter-5-sending-messages.md)

### 5. Check the results

Launch two instances via the `start.sh` script. See the parent [README](../README.md#running-the-finished-example) on where to find the links to each Chat App.
