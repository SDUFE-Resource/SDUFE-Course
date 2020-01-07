#### 20171847118 金正旭

### Observe the TCP/IP process via wireshark

First ，we chose a web site as we destination .  I chose the SDUFE offical web site(www.sdufe.edu.cn).

#### 1.get the IP address from ping
use the command :
```bash
$ ping www.sdufe.edu.cn
PING www.sdufe.edu.cn (210.44.128.101) 56(84) bytes of data.
```
As we see, the destination is 210.44.128.101.

#### use wireshark to capture the packet

start the capture ,we can get a lot of packet:

[![nHxSl8.png](https://s2.ax1x.com/2019/09/18/nHxSl8.png)](https://imgchr.com/i/nHxSl8)

then we rule the source and destination address to get the packet from or send to 210.44.128.101.
```
ip.src==210.44.128.101 or ic.dst==210.44.128.101
```
from the picture ,we find that trans three times TCP packets, then , start HTTP trans.
#### analyse the TCP/IP


+ client send the SYN to serve
![nHx2jS.png](https://s2.ax1x.com/2019/09/18/nHx2jS.png)
+ serve received SYN and send SYN+ACK to client
![nHx2jS.png](https://s2.ax1x.com/2019/09/18/nHx2jS.png)
+ clinet received SYN+ACK and send ACK to serve
![nbSVMT.png](https://s2.ax1x.com/2019/09/18/nbSVMT.png)

#### follow the tcp to get more information
[![nbSKo9.png](https://s2.ax1x.com/2019/09/18/nbSKo9.png =450x)](https://imgchr.com/i/nbSKo9)

### get password and id from HTTP packets

#### start wireshark
#### ping SDUFE mail get the ip address
```bash
$ ping email.sdufe.edu.cn
```
#### get HTTP packet and password
![nLZ92D.png](https://s2.ax1x.com/2019/09/19/nLZ92D.png)
![nLZ8Zn.png](https://s2.ax1x.com/2019/09/19/nLZ8Zn.png)
![nLewff.png](https://s2.ax1x.com/2019/09/19/nLewff.png)
