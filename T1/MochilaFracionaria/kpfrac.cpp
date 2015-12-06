#include <vector>       
#include <iostream>
#include <fstream>
#include <iomanip>

#include "Object.h"
#include "CPUTimer.h"

Object * objects = NULL;
int W;
int num_elem;

void parser (std::string fileName) {
    int valor, weight, num;
    std::ifstream infile(fileName);

    infile >> num_elem;

    objects = new Object[num_elem];

    for(int i = 0 ; i < num_elem; i++)
    {
        infile >> num >> valor >> weight;
        Object obj(weight, valor, num);
        objects[i] = obj;
    }
    infile >> W;
}

void kpfrac(Object * obj)
{
    int i, weight = 0;

    std::sort(obj, obj + num_elem);    

    i = num_elem - 1;
    while (i >= 0 && weight < W)
    {   
        if (weight + obj[i].weight <= W)
        {
            obj[i].frequency = 1.0;
            weight += obj[i].weight;
        } else {
            obj[i].frequency = (W - weight) * 1.0 / obj[i].weight;
            weight = W;
        }
        i--;
    }
}

/**
 * Reads in the input file, performs the KP-frac linear time strategy on the 
 *   input and displays the optimal solution.
 */
int main (int argc, char * argv[])
{
    if (argc <= 1) {
        std::cout << "Please indicate the name of the input file." << std::endl;
        return -1;
    }

    CPUTimer timer;

    std::cout << "Instance, Avg Running Time (s), Number of Iterations, Value" << std::endl; 

    for (int fileIdx = 1; fileIdx < argc; fileIdx++) {
        parser(argv[fileIdx]);

        Object * temp = new Object[num_elem];

        for (int i = 0; i < num_elem; i++) {
            temp[i] = objects[i];
        }

        timer.reset();        

        int it = 0;
        while (timer.getCPUTotalSecs() < 5.0)
        {
            for(int j = 0; j < num_elem; j++) {
                objects[j] = temp[j];
            }

            timer.start();
            kpfrac(objects);
            timer.stop();

            it++;
        }

        double media = timer.getCPUTotalSecs() / it;

        std::cout << argv[fileIdx] << "," << media << "," << it; 

        // Loop over the array of objects and display which were inserted and with
        //   what frequency.
        
        double totalValue = 0.0;

        #ifdef DEBUG
            std::cout << std::endl << "Elem | Value | Weight | Density | Frequency" << std::endl;
        #endif

        // objects should be ordered in increasing fashion, so start from last element.
        for (int i = num_elem - 1; i >= 0 && objects[i].frequency > 0.0; i--) {
            Object obj = objects[i];

            #ifdef DEBUG
              std::cout << obj.elem   << " " << obj.value   << " " 
                        << obj.weight << " " << obj.density << " "
                        << obj.frequency << std::endl;
            #endif

            totalValue += obj.frequency * obj.value;
        }
        
        std::cout << std::setprecision(15) << "," << totalValue << std::endl;

        delete [] objects;
        delete [] temp;
    }

    return 0;
}

