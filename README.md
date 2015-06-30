# LuxferClient
The client handles perspective projection of a small patch from the Game of Life. It is designed to run on small embedded computers such as the Raspberry Pi.

## How to install on Raspberry Pi

1. Unzip luxfer.zip into home directory (creating directory /home/pi/luxfer)

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

## Controls

## Communication with the server

## Credits

The project uses code from two open source libraries, namely:

Perspective filter from [Java Image Filters](http://www.jhlabs.com/ip/filters/index.html) by Jerry Huxtable. License notice:

	Copyright 2006 Jerry Huxtable

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

	   http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.


JSON parser from [JSON-java](https://github.com/douglascrockford/JSON-java) by Douglas Crockford. License notice:
	
	
	Copyright (c) 2002 JSON.org

	Permission is hereby granted, free of charge, to any person obtaining a copy
	of this software and associated documentation files (the "Software"), to deal
	in the Software without restriction, including without limitation the rights
	to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
	copies of the Software, and to permit persons to whom the Software is
	furnished to do so, subject to the following conditions:

	The above copyright notice and this permission notice shall be included in all
	copies or substantial portions of the Software.

	The Software shall be used for Good, not Evil.

	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
	IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
	FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
	AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
	LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
	OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
	SOFTWARE.