---
icon: cube
---

# Deployment Goal: Production

Every deployment has to decide the following:

- Which dataspace do you want to roll-in to?
  - This decides features, auth mechanisms, policies and compatibility to other connectors
- Which setup do you want to deploy?
  - Every variant now supports both the setups "Control Plane with integrated Data Plane" and "Control Plane + Data Plane (Standalone)"

### Control Plane with integrated Data Plane

This deployment requires only one backend and one DB.

See [Deployment Goal: Production: Control Plane with integrated Data Plane](cp-with-integrated-dp.md).

### Control Plane + Data Plane (Standalone)

This deployment requires two backends and two DBs.

Note, that you could theoretically always deploy additional standalone data planes, even if the first data plane is integrated into the control plane.

See [Deployment Goal: Production: Control Plane + Data Plane (Standalone)](cp-dp-standalone.md).
