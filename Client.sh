#!/bin/bash
#If you wish for the file not to be deleted and only transmit on change, toggle the last argument to false

java -jar Client.jar input '.*Delim$' localhost 1337 true
