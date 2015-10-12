#include <vector>       
#include <iostream>
#include <fstream>

#include "Object.h"
#include "CPUTimer.h"

Object * objects = NULL;
int W;
int num_elem;

void parser(std::string fileName)
{
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

void mergeSort(Object * objects, int start, int end) {
    int length = end - start + 1;
    if (length <= 1) {
        return;
    }

    int mid = (start + end + 1) / 2;
    mergeSort(objects, start, mid - 1);
    mergeSort(objects, mid, end);

    int i, j, k;
    for (k = start, i = start, j = mid; (k < end) && (i < j) && (k-i!=1); k++) {
        if(k - i == 1) {
            break;
        }

        if (objects[i] < objects[j]) {
            std::swap(objects[i], objects[k]);

            if (!(j - i == 1)) {
                i++;
            } else if (i - k != 1) {
                i = k;
            }
        } else {
            std::swap(objects[j], objects[k]);

            if (j < end) {
                j++;
            }

            if (i < mid) {
                i = j - 1;
            }

            if (i == k) {
                i++;
            }
        }
    }
}

void kpfrac(Object * obj)
{
    int i, weight = 0;
    double frac[num_elem];

    mergeSort(obj, 0, num_elem);

    for(i = 0; i < num_elem; i++) {
        frac[i] = 0;
    }
    
    i = 0;
    while (weight < W)
    {   
        if (weight + obj[i].weight <= W)
        {
            objects[i].frequency = 1.0;
            weight += objects[i].weight;
        } else {
            objects[i].frequency = (W - weight) * 1.0 / obj[i].weight;
            weight = W;
        }
        i++;
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

    std::cout << "Instance, Avg Running Time (s), Number of Iterations" << std::endl; 

    for (int fileIdx = 1; fileIdx < argc; fileIdx++) {
        parser(argv[fileIdx]);

        Object * temp = new Object[num_elem];

        for (int i = 0; i < num_elem; i++)
            temp[i] = objects[i];

        timer.reset();        

        int it = 0;
        while (timer.getCPUTotalSecs() < 5.0)
        {
            timer.start();
            kpfrac(objects); 
            timer.stop();      
            it++;
            for(int j = 0; j < num_elem; j++)
                objects[j] = temp[j];
        }

        double media = timer.getCPUTotalSecs() / it;

        std::cout << argv[fileIdx] << "," << media << "," << it << std::endl; 

        // Loop over the array of objects and display which were inserted and with
        //   what frequency.
        #ifdef DEBUG
            int totalValue = 0;
            double totalWeight = 0;

            std::cout << "Elem | Value | Weight | Density | Frequency" << std::endl;
            for (int i = 0; objects[i].frequency > 0; i++) {
                Object obj = objects[i];

                std::cout << obj.elem   << " " << obj.value   << " " 
                          << obj.weight << " " << obj.density << " "
                          << obj.frequency << std::endl;

                totalValue  += obj.frequency * obj.value;
                totalWeight += obj.frequency * obj.weight;
            }
            std::cout << "Weight: " << totalWeight << "/" << W << std::endl;
            std::cout << "Value : " << totalValue << std::endl;
        #endif

        delete [] objects;
    }

    return 0;
}

