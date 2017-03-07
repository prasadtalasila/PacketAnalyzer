# -*- mode: ruby -*-
# vi: set ft=ruby :

Vagrant.configure("2") do |config|
  config.vm.box = "ubuntu/trusty64"
  config.vm.provision :shell, privileged: true, path: "conf/vagrant.sh"
  config.vm.network :forwarded_port, guest: 22, host: 12914, id: 'ssh'
  config.vm.network "forwarded_port", guest: 8080, host: 8080
  config.vm.synced_folder ".", "/home/vagrant/darshini"
end
