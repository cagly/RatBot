### Setup Guide for RATBOT

#### 1. Connect to instance
Connect to the VM using SSH.
#### 2. Make Swapfile  
```
sudo fallocate -l 2G /swapfile
sudo chmod 600 /swapfile
sudo mkswap /swapfile
sudo swapon /swapfile
sudo swapon --show
echo /swapfile none swap sw 0 0 | sudo tee -a /etc/fstab
```
#### 3. Install Dependencies
```
sudo apt-get update
sudo apt-get upgrade
sudo apt-get install openjdk-11-jre openjdk-11-jdk
```

#### 4. Make ratbot user and copy actions runner to ratbot home folder
```
sudo useradd -m ratbot
sudo cp -r ~/actions-runner ~/../ratbot
```

#### 5. Github Actions Runner Setup  
Go to github repo. Settings -> Actions -> Runners  
Add new self-hosted runner and follow the displayed guide **in ratbot home folder**.  
Once you get "permission denied" error, switch to ratbot user and continue the setup. (command at end of file)


**NB!** To run Ratbot CI/CD, github runner must constantly be running.  
However, if you just regularly run the runner, it will close after disconnecting SSH.  
One solution to avoid that is to use Terminal Multiplexers, or tmux.

Command to run tmux is the following:  
```tmux```  
Following keybinds can be used to navigate tmux:
```
First press CTRL + B, then one of the following:
D - Detach, closes tmux, but keeps terminals alive
X - Kills current terminal
C - creates new terminal
W - List all tmux sessions
```
To close all tmux sessions, use the following command:
```
tmux kill-server
```

#### 6. Set up database for ratbot
```
sudo apt install mysql-server
sudo systemctl start mysql.service
sudo mysql 

ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'password';
exit

mysql_secure_installation
```
Follow the setup, choose whatever makes sense at the time, choose a decent password.  
After mysql is made to exist, we create a user for RatBot. RatBot shouldn't have root access to all of MySQL generally.
```
mysql -u root -p
CREATE USER 'ratbot'@'localhost' IDENTIFIED BY 'password';
CREATE DATABASE ratbot;
GRANT ALL PRIVILEGES ON ratbot.* TO 'ratbot'@'localhost';
FLUSH PRIVILEGES;
exit
```
Choose a decent password for RatBro too.

#### 7. Create resource folders and resources for ratbot.
```
sudo su ratbot
cd ~
touch BotToken.java
nano BotToken.java
```
Insert the following into the file, replacing the empty string with RatBot's secret bot key:
```
package bot.rat.privateResources;

public class BotToken {
    public static final String TOKEN = "";
}

```
Then we create database info file:
```
touch DatabaseInfo.java
nano DatabaseInfo.java
```
Contents be following, replacing database username and password with what was chosen at database setup:
```
package bot.rat.privateResources;

public class DatabaseInfo {
    public static final String USERNAME = "ratbot";
    public static final String PASSWORD = "";
}

```

#### x. Make Ratbot service file and give ratbot user permissions to use it
```
cd /etc/systemd/system/
sudo touch ratbot.service
sudo nano ratbot.service
```
Make sure the contents are the following:
```
[Unit]
Description=dashboard ratbot service
After=network.target

[Service]
Type=simple
User=ratbot
WorkingDirectory=/home/ratbot/actions-runner
ExecStartPre=sudo docker-compose build
ExecStart=sudo docker-compose up
ExecStop=sudo docker-compose down
Restart=on-abort

[Install]
WantedBy=multi-user.target
```

#### x. Start Github Runner
Navigate to folder ~/actions-runner
```
cd ~/actions-runner
./run.sh
```


### X. Useful commands
```
htop - See memory and CPU usage
df -H - See current hard drive usage
sudo su ratbot - Take control of ratbot user ("exit" to undo)
```