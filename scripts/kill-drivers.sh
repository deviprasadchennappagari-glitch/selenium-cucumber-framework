#!/usr/bin/env bash
# Manually kill any leftover browser-driver processes (Linux/Mac).
# The framework already does this automatically after every scenario and
# at the end of the suite - use this script only if you need to clean up
# by hand (e.g. after a hard JVM crash or manually killed build).

for name in chromedriver geckodriver msedgedriver; do
  pids=$(pgrep -f "$name")
  if [ -n "$pids" ]; then
    echo "Killing $name processes: $pids"
    kill -9 $pids
  else
    echo "No running $name processes found."
  fi
done
