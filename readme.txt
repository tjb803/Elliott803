Elliott 803 Simulation
======================
This project is a simulation of an Elliott 803B computer from the early
1960s.  The program is written entirely in Java and should therefore
run easily on most systems. 

Full details can be found at:
   The project homepage:  http://elliott803.sourceforge.net
   Latest documentation:  http://elliott803.sourceforge.net/docs
   The download pages:    http://sourceforge.net/projects/elliott803

The simulation is reasonably complete and includes the following
hardware elements:

 - CPU, ALU and FPU
 - Core Store
 - Operator's Console 
 - Paper Tape Readers and Punches
 - Output Teletype
 - Graph Plotter
  
A Java Swing GUI interface allows the simulation to be operated in a 
manner very similar to a real Elliott 803 and also provides a 
visualisation of the workings of the machine. 
  
Off-line utility programs are included to:

 - Display and Prepare Paper Tapes
 - Examine System Dumps and Instruction Traces  
  
A selection of sample Elliott 803 programs are included and others can
be written using:

 - A simple assembler for short test programs
 - The original Elliott ALGOL 60 compiler     

Installation and Operation 
--------------------------
The program, sample code and a copy of the latest documentation are 
provided as a single ZIP file archive.  Download the latest package from
the SourceForge download link and unzip to a suitable directory on your
system.

The program can be run by executing the 'elliott803-n.n.n.jar' file.

For further details refer to the 'Quick Start' and 'Operation' sections
in the documentation.
   
Latest Improvements
-------------------
The latest release will always include various minor bug fixes.  In 
addition the following are notable improvements:

v1.0.0:
 - Added the Graph Plotter support
 - Added full save and restore of machine image and window layout

v0.7.0:
 - Added 'Manual Data' button to the console
 - Teletype window resizes correctly
 - Teletype 'Save' button saves current output as well as any subsequent
   output
 - A saved 'machine image' can be provided on startup as a quick way to 
   restore the core-store contents   

Future Work
-----------
The following items (and probably more) are currently missing from
the simulation and can hopefully be added in a future release:

 - Sound
 - Film Handlers

Acknowledgements
----------------
Many thanks to the following people for providing a huge amount of
information, both on the web and via email, and for much testing
and debugging: 

   Bill Purvis
   Peter Onion
   Bob Firth

Release History
---------------
v0.5.0  December 2009    First public release
v0.6.0  December 2009    Includes Algol compiler and samples
v0.7.0  June 2010        Bug fixes and small improvements
v1.0.0  August 2010      Initial intended function fully complete

===========
Tim Baldwin
tjb803@tinymail.co.uk