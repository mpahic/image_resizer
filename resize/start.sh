#!/bin/sh

DESC="Description"
NAME=transcoder
PIDFILE=.pid
RUN_AS=root
COMMAND=/usr/bin/java -- -jar resize.jar

d_start() {
  if [ -e $PIDFILE ]
    then
      echo "Application already started"
    else   
      start-stop-daemon --start --quiet --background --make-pidfile --pidfile $PIDFILE --chuid $RUN_AS --exec $COMMAND
  fi
}

d_stop() {
    start-stop-daemon --stop --quiet --pidfile $PIDFILE
    if [ -e $PIDFILE ]
        then rm $PIDFILE
    fi
}

case $1 in
    start)
      echo -n "Starting $DESC: $NAME"
      d_start
      echo "."
      ;;
    stop)
      echo -n "Stopping $DESC: $NAME"
      d_stop
      echo "."
      ;;
    restart)
      echo -n "Restarting $DESC: $NAME"
      d_stop
      sleep 1
      d_start
      echo "."
      ;;
    *)
      echo "usage: $NAME {start|stop|restart}"
      exit 1
      ;;
esac

exit 0
