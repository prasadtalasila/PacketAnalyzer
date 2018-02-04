# -*- mode: ruby -*-
# vi: set ft=ruby :

Vagrant.configure("2") do |config|
  config.vm.box = "ubuntu/xenial64"
  config.vm.provision :shell, privileged: true, path: "scripts/vagrant.sh"
  config.vm.network :forwarded_port, guest: 22, host: 12914, id: 'ssh'
  config.vm.network "forwarded_port", guest: 8080, host: 8080
  config.vm.network "forwarded_port", guest: 9200, host: 9200
  config.vm.synced_folder ".", "/home/vagrant/darshini"
  config.vm.provider :virtualbox do |v|
  	v.customize ["modifyvm", :id, "--memory", 4096]
  	v.customize ["modifyvm", :id, "--cpus", 4]
    v.customize ["modifyvm", :id, "--natdnshostresolver1", "on"]
    v.customize ["modifyvm", :id, "--natdnsproxy1", "on"]
  end
end
