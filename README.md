
Eustace
===========

Android app to use with [alexhq](https://github.com/legeyda/alexhq).
App continuously monitors device position and sends to alexhq server.


Development
----------------

If android emulator fails to start with error `/dev/kvm device permission denied`,
try the following.

    sudo apt-get install qemu-kvm
    sudo adduser $USER kvm
    sudo reboot now