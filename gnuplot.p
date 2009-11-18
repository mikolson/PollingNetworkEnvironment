# Gnuplot script file for plotting data in file "force.dat"
      # This file is called   force.p
            set terminal png size 1000, 900
	    set output 'out.png'

#	    set term epslatex
#	    set output "graph1.eps"

#	    set terminal latex
#	    set output "eg1.tex"
#set ytics 100
set mytics 10
#set xtics 1000
#set mxtics 10 out 0, -1.0
set grid

	    
#    set terminal postscript enhanced monochrome dashed
		
      set   autoscale                        # scale axes automatically
#      unset log                              # remove any log-scaling
 #     unset label                            # remove any previous labels
  #    set xtic auto                          # set xtics automatically
 #     set ytic auto                          # set ytics automatically
#      set title "Force Deflection Data for a Beam and a Column"
  #    set xlabel "Deflection (meters)"
   #   set ylabel "Force (kN)"
#      set key 0.01,100
 #     set label "Yield Point" at 0.003,260
  #    set arrow from 0.0028,250 to 0.003,280
  
#      set xr [0.0:0.022]
      set yr [0:150]

#    set size 1.5, 1.5;
       set multiplot layout 3, 1 title "Multiplot layout 3, 1"

#      set size 1,0.5;  
#      set origin 0.0,0.5;   
      plot "out.plot" using 1:2 title 'Queue 1' w l  , \
            "out.plot" using 1:3 title 'Queue 2' w l, \
            "out.plot" using 1:4 title 'Best-Effort Queue' w l


#      set origin 0.0,0.0;
      plot "out.plot" using 1:5 title 'Queue 1' with lines , \
            "out.plot" using 1:6 title 'Queue 2' with lines, \
            "out.plot" using 1:7 title 'Best-Effort Queue' with lines

#      set origin 0.0,0.0;

      set   autoscale                        # scale axes automatically
      set yr [0:1]

set xlabel "Iteration Number"
set ylabel "Shake percentage"

      plot "out.plot" using 1:8 title 'Shake' with lines 
      
      unset multiplot                         # exit multiplot mode

#plot [t=-2:2] t**2 + t + 1 with points, t**2 - t - 1 with impulses, sin(t) with lines, cos(t) with boxes