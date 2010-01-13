#!/usr/bin/python
import fileinput
import numpy as N

# Experyment ; Iteracja ; Obslugiwana kolejka; Ocena Stanu
def read_file(file_name):
	obsl_kolejka = []
	ocena_stanu = []
	
	for line in fileinput.input(file_name):
		if not line.startswith("#"):
			data = line.strip().split(";")[1:]
			index = int(data[0])
			stan = float(data[2])
			kolejka = int(float(data[1])) 

			try:
				ocena_stanu[index]
			except Exception as ex:
				ocena_stanu.append([])
				obsl_kolejka.append([])

			ocena_stanu[index].append(stan)
			obsl_kolejka[index].append(kolejka)
	
	

	# Zapisanie przeanalizowanych danych do pliku wyjsciowego
	f = open("serwer.gplot","w")
	f.write("# Iteracja; Srednia ocena stanu; Odchylenie standardowe")
	for index,i in enumerate(ocena_stanu):
		f.write("%d\t%f\t%f\n"%(index,N.mean(i), N.std(i)))
		
#    with open(file_name) as f:
#        while (data = f.readline()):
#            elements = data.strip().split(";")
#            print elements


read_file("../../plot-data/Serwer_1_pl.wroc.pwr.iis.simulation.TestBledow.serwer");


