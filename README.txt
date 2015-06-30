HOW TO INSTALL

1. Unzip luxfer.zip into home directory (/home/pi/lufer)

2. sudo apt-get update & sudo apt-get upgrade

3. Install Oracle's Java (if not already present)

4. Run sudo raspi-config and select boot to GUI

5. Create file ~/.config/lxsession/LXDE-pi/autostart and add line:
  
  @xset s noblank
  @xset s off
  @xset -dpms
  
   @/home/pi/luxfer/run.sh


6. Edit file /etc/lightdm/lightdm.conf and add this into the [SeatDefaults] section:

  xserver-command=X -s 0 -dpms

7. Reboot! :-)
