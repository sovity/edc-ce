# Debugging

## Connect to docker images

### Configure

The images are started with the [docker-entrypoint.sh](../../launchers/docker-entrypoint.sh).

This entry point supports debugging via environment variables.

In `docker-compose-dev`, add the following environment variables in `services.edc.environment`

```yaml
  REMOTE_DEBUG: y
  REMOTE_DEBUG_BIND: 0.0.0.0:5005
```

If you also want to wait for the debugger to connect before starting the EDC, also add

```yaml
  REMOTE_DEBUG_SUSPEND: y
```

Then shutdown and restart the EDC with docker compose.

If you used the `dev` set of files:

```bash
docker compose --env-file .env.dev --file docker-compose-dev.yaml down
docker compose --env-file .env.dev --file docker-compose-dev.yaml pull
docker compose --env-file .env.dev --file docker-compose-dev.yaml up
```

### Connect

Each EDC will start on a different set of ports, but they all bind to the same address mentioned above in docker.

To connect to the EDC, in IJ, do:

* Edit configurations
* Add New Configuration
* Remote JVM debugger
* Debugger mode: attach to remote JVM
* Host: localhost
* Post: it depends on the EDC:
  * check the `ports` mapping in the docker compose file where you added the remote debugging options
  * look for the entry that is `#####:5005`
  * This `#####` port is the port you want to connect to. 
* Use module classpath: use sovity-edc-ce

Enjoy! :)
