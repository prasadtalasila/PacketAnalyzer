echo "PS1='\[\e[0;31m\]\u\[\e[m\] \[\e[1;34m\]\w\[\e[m\] \$ '" >> /home/vagrant/.bashrc
echo "cd /home/vagrant/darshini" >>  /home/vagrant/.bashrc
source /home/vagrant/.bashrc
cd /home/vagrant/darshini

#run the installation script
bash scripts/install.sh
