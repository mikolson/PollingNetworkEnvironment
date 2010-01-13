#!/usr/bin/python
import fileinput
import numpy as N

# Experiment; Iteration;Kolejka
# Sredni Czas Oczekiwania;Czas Oczekiwania pierwszego;Ilosc zgloszen;
#Ilosc przybyc;Ilosc obsluzonych


def read_file(file_name):
	queue = []
	
	for line in fileinput.input(file_name):
		if not line.startswith("#"):
			data = line.strip().split(";")[0:]
			exp = int(data[0])
			iter = int(data[1])
			kolejka = int(float(data[2])) 

			try:
				queue[iter]
			except Exception as ex:
				queue.append([])
			
			
			try:
				queue[iter][kolejka]
			except Exception as ex:
				queue[iter].append([ [] for x in data[3:] ])
			
			
#			if exp > 1 : break
				
			for index, val in enumerate(data[3:]):
				queue[iter][kolejka][index].append(float(val)) 

#			print "%d - %d - %d" % (exp, iter, kolejka)
# queue[iter][kolejka][0] - > sredni czas oczekiwania
# queue[iter][kolejka][1] - > Czas oczekiwania
# queue[iter][kolejka][2] - > Ilosc zgloszen
# ilosc przybyc 
# ilosc obslug


	# wygenerowanie zapisu plot

	# Zapisanie przeanalizowanych danych do pliku wyjsciowego
	f = open("queue.gplot","w")
	f.write("")
	for iteration, tab in enumerate(queue):
		f.write("%d\t" % iteration)
		for queue_number, values in enumerate(tab):
			for i in range(len(values)):
				f.write("%f\t%f\t"%(N.mean(values[i]), N.std(values[i]))) # sredni czas oczekiwania
#			f.write("%f\t%f"%(N.mean(values[1]), N.std(values[1]))) # czas oczekiwania
#			f.write("%f\t%f"%(N.mean(values[1]), N.std(values[1]))) # czas oczekiwania
		f.write("\n")
		
		
#    with open(file_name) as f:
#        while (data = f.readline()):
#            elements = data.strip().split(";")
#            print elements


read_file("../../plot-data/Serwer_1_pl.wroc.pwr.iis.simulation.TestBledow.queue");


