## 2024-02-29

Created new Lightsail instance. 512 MB RAM, 2 vCPUs, 20 GB SSD in ap-southeast-2a.
Turned off all IPv6 firewall rules (no inbound connections allowed). Allowed TCP 80 and 22 to only my home IPv4 address.

Ran the following commands:

```bash
sudo yum install docker
sudo systemctl enable docker
sudo curl -SL https://github.com/docker/compose/releases/download/v2.24.6/docker-compose-linux-x86_64 -o /usr/local/lib/docker/cli-plugins/docker-compose
sudo mkdir -p /usr/local/lib/docker/cli-plugins
sudo chmod +x /usr/local/lib/docker/cli-plugins/docker-compose
sudo yum install git htop
git clone https://github.com/un1r8okq/url-shortener.git
cd url-shortener
cp .env.example .env
nano .env # Setup db password etc
docker compose up -d
```

The build didn't work because NPM ran out of memory. I then increased SWAP with

```bash
sudo fallocate -l 1G /swapfile
sudo chmod 600 /swapfile
sudo mkswap /swapfile
sudo swapon /swapfile
```

and tried again.
