Despite my best efforts, this isn't working as intended. I will be taking this repository and making it archived.

Instead of this project, I'd recommend to check this one out instead, which took basically my "idea" and made it 500x better before I even attempted this at all:

https://github.com/pixelomer/Shijima-Qt



ShimejiEE Cross Platform
========================

https://github.com/LavenderSnek/ShimejiEE-cross-platform

The releases aren't updated frequently, [build from source](docs/building.md) for the latest version.

This project is a fork of [Kilkakon's shimeji version](http://kilkakon.com/shimeji) and incorporates the work from [nonowarn's shimeji4mac](https://github.com/nonowarn/shimeji4mac)

New Features
-------
- Interactive AI Chat
  - Chat naturally with your Shimeji using AI technology
  - Control your Shimeji through chat commands (e.g., "please chase the mouse", "can you sit down?")
  - Configure OpenAI API key in settings for chat functionality
  
- Character Customization
  - Personalize your Shimeji's:
    - Name and identity
    - Personality traits and behavior
    - Greeting messages
    - Chat style and responses
  - Access character settings through the chat window
  
- Behavior Control
  - Direct your Shimeji's actions through chat commands
  - Available behaviors include:
    - Chasing the mouse
    - Sitting and standing
    - Walking and running
    - And many more!
  - Natural command recognition for intuitive control

Visit [YCamie](http://www.ycamie.com) to get your personalized Shimeji and explore more features!

Installation
-------


- macOS:
  - Download the `mac-jre` version
  - Click the file named `ShimejiEE-launcher`. 
  - If the OS doesn't let you open it because it's from an unidentified developer: 
    - Go to System preferences → Security & privacy → General, and make sure `App Store and Identified developers` is selected in the "Allow apps downloaded from:" section
    - Restart you computer, just to be safe
    - Now right-click the launcher and click open. Ignore the warnings. 
  - If it still doesn't work, try re-downloading it directly from the GitHub releases page and make sure to unzip with Archiver.app

- Linux:
  - First consider trying out [linux-shimeji](https://github.com/asdfman/linux-shimeji), it has x11 support
  - Download the `no-jre` version. 
  - Still a WIP with lots of issues, but you can use java 17 to launch `ShimejiEE.jar`. 
  - Install java from a different vendor if `libawt_xawt.so` is missing. 
  - All program files are lowercase by default but Image-sets might break due to case sensitivity.

- Windows:
  - Download the `no-jre` version. 
  - Untested but you can try it out; Just make sure to use java 17.


Credits
-------

- YCamie
  - Added AI chat functionality and character customization
  - [Homepage](https://www.ycamie.com)

- LavenderSnek
  - macOS maintainer (this fork)
  - [Github page](https://github.com/LavenderSnek/ShimejiEE-cross-platform)

- Kilkakon
  - Current maintainer. Added sounds, affordances, and japanese conf compatibility.
  - [Homepage](http://kilkakon.com/shimeji)
  - [Shimeji discord](https://discord.gg/dcJGAn3)

- nonowarn
  - Wrote the initial swing + jna mac implementation
  - [GitHub page](https://github.com/nonowarn/shimeji4mac)

- TigerHix
  - Added 64 bit support for windows
  - [GitHub page](https://github.com/TigerHix/shimeji-universal)

- The shimeji-ee Group
  - Added i18n
  - [Google Code repo](https://code.google.com/archive/p/shimeji-ee/source/default/commits)

- Group Finity (Original)
  - The original creator(s) of shimeji
  - [Homepage snapshot](https://web.archive.org/web/20140530231026/http://www.group-finity.com/Shimeji/)
  - [Vector page](https://www.vector.co.jp/soft/winnt/amuse/se476479.html)
