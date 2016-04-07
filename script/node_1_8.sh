#!/bin/bash
#SBATCH --time=01:00:00
#SBATCH --nodes=1
#SBATCH --ntasks=8
#SBATCH --cpus-per-task=1
module load Java/1.8.0_71
module load mpj/0.44
javac -cp .:$MPJ_HOME/lib/mpj.jar Spartan.java Message.java PatternMatch.java Processors.java ReadFileTask.java Occurence.java Settings.java 
mpjrun.sh -np 8 Spartan Melbourne