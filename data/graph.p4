graph start {

	ethernet;

}

graph ethernet {
	switch(ethertype) {
		case 0800:			 ipv4;
	}
}
graph ipv4 {
	switch(protocol) {
		case 06: tcp;
	}
}


graph tcp {

}

graph end {
}
