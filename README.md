# Java_ProcessBuilders

## Using a ProcessBuilder to take backups of your Postgresql DB, using the pg_dumpcommand. Please note that you are not doing database connectons.
```
Introducing a means to take the backups every 1 minute, and dump the backups in a folder. Once
they reach 10 backups, rotate them by discarding the oldest backup, to ensure you maintain a
maximum of only 10.
```
## In a separate program, detecting the type of operating system you application is running on. 
```
Use a processbuilder to list all running processes in the OS. Create a folder in the current directory
using the mkdir command, and change directory to that folder. Using the echo command, write
to a ﬁle called “running_processes.txt” all the processes running in the OS.
```
### Using a processbuilder to display your disk information e.g. partions and their sizes.
