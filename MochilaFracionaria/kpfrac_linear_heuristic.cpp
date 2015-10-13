#include <vector>       
#include <iostream>
#include <fstream>
#include <algorithm>
#include <iomanip>

#include "Object.h"
#include "CPUTimer.h"

Object * objects;
std::vector<Object> inserted;
unsigned int W, num_elem;

/**
 * Extracts the properties of the knapsack and objects from the input file
 *   at path `fileName`.
 *
 * @param fileName
 *      Path to the input file.
 */
void parser (std::string fileName) {
    int valor, weight, num;
    std::ifstream infile(fileName);

    infile >> num_elem;

    objects = new Object[num_elem];
    inserted.reserve(num_elem);

    for(int i = 0 ; i < num_elem; i++) {
        infile >> num >> valor >> weight;

        Object obj(weight, valor, num);

        objects[i] = obj;
    }

    infile >> W;
}

/**
 * Returns an object whose density is equal to the mean of the densities
 *   of the objects in `v`. The returned object should be used as a pivot.
 * In order to produce the same side effects as the find_kth algorithm, this
 *   routine rearranges the elements such that those smaller than the pivot 
 *   come before it and the remaining elements come after it.
 *
 * @param v
 *      Subset of the list of Objects for the current input file.
 * @param n
 *      Number of elements inside of `v`.
 * @return
 *      Pivot object whose density is equal to the average density of `v`.
 */
Object heuristic (Object * v, int n) {
    Object pivot;

    double density = 0;
    for(int i = 0; i < n; i++) {
        density += v[i].density;
    }
    pivot.density = density/((double) n);

    // Rearrange the list of objects.
    int store = 0;
    for (int i = 0; i < n; i++) {
        if (v[i] < pivot) {
            std::swap(v[i], v[store++]);
        }
    }

    return pivot;
}

/**
 * This algorithm follows the following strategy:
 *   1. Find the pivot element by density of the subset of `objects` defined
 *        by the open-ended interval [start..end). [ O(n) ]
 *   2. Break up `objects` into the following subsets: [ O(n) ]
 *      2.1. A set `R_1`, in which all elements are less than the pivot.
 *      2.2. A set `R_2`, in which all elements are equal to the pivot.
 *      2.3. A set `R_3`, in which all elements are greater than the pivot.
 *   3. If the sum of the weights of elements in `R_3` is greater than `weight`, 
 *      recurse on `R_3`. [ T(n/2) -- throw away lower half ]
 *   4. Otherwise, add all elements of `R_3` completely to the knapsack. [ O(n) ]
 *   5. Then, add as many as possible elements of `R_2` to the knapsack. [ O(n) ]
 *   6. Recurse on `R_1` with `weight` updated to (`weight` - (|`R_3`| + |`R_2`|)).
 *      [ T(n/2) -- throw away upper half ]
 *
 * In the end, it will have updated the instances of inserted objects by 
 *   assigning each its frequency.
 *
 * In the execution of this algorithm, we always traverse perform O(n) operation,
 *   then throwing away n/2 elements in the recursive calls. Therefore:
 *      T(n) <= T(n/2) + O(n)
 * From the Master Theorem, it follows that the worst-case time complexity is O(n).
 *
 * @param objects
 *      Set of objects containing the subset to be tentatively inserted into
 *        the knapsack.
 * @param length
 *      Number of elements in `objects`.
 * @param weight
 *      Free space within the knapsack.
 */
void kpfrac_linear_heuristic (Object * objects, int length, int weight) {
    // Base case: We have an empty array or a full knapsack, return;
    if (length <= 0 || weight == 0) return;

    Object * R_1, * R_2, * R_3;
    int idx_R_1, idx_R_2, idx_R_3;

    R_1 = new Object[length];
    R_2 = new Object[length];
    R_3 = new Object[length];

    idx_R_1 = idx_R_2 = idx_R_3 = 0;

    // Step 1.
    Object pivot = heuristic(objects, length); // O(length) 

    // Step 2
    int R_3_weight = 0;
    for (int i = 0; i < length; i++) { // O(length)
        // Step 2.1
        if (objects[i] < pivot) {
            R_1[idx_R_1++] = objects[i]; 
        }
        // Step 2.2. Probably won't happen, given the used heuristic.
        else if (objects[i] == pivot) {
            R_2[idx_R_2++] = objects[i];
        }
        // Step 2.3
        else {
            R_3[idx_R_3++] = objects[i];
            R_3_weight += objects[i].weight;
        }
    }

    // Step 3
    if (R_3_weight > weight) {
        kpfrac_linear_heuristic(R_3, idx_R_3, weight); // T(length/2)
    } else {
        // Step 4
        for (int i = 0; i < idx_R_3; i++) { // O(idx_R_3) < O(length)
            R_3[i].frequency = 1.0;
            weight -= R_3[i].weight;
            inserted.push_back(R_3[i]);
        }

        // Step 5. 
        // Either takes all from `R_2` completely with leftover space or exhausts
        // the knapsack.
        for (int i = 0; i < idx_R_2 && weight > 0; i++) {
            if (R_2[i].weight < weight) {
                R_2[i].frequency = 1.0;
                weight -= R_2[i].weight;
            } else {
                R_2[i].frequency = weight * 1.0 / R_2[i].weight;
                weight = 0;
            }

            inserted.push_back(R_2[i]);
        }

        // Step 6.
        kpfrac_linear_heuristic(R_1, idx_R_1, weight); // T(length/2)
    }

    delete [] R_1;
    delete [] R_2;
    delete [] R_3;
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

        for (int i = 0; i < num_elem; i++)
            temp[i] = objects[i];

        timer.reset();        

        int it = 0;
        while (timer.getCPUTotalSecs() < 5.0)
        {
            inserted.clear();
            timer.start();
            kpfrac_linear_heuristic(objects, num_elem, W); 
            timer.stop();      
            it++;
            for(int j = 0; j < num_elem; j++)
                objects[j] = temp[j];
        }

        double media = timer.getCPUTotalSecs() / it;

        std::cout << argv[fileIdx] << "," << media << "," << it; 

        // Loop over the array of objects and display which were inserted and with
        //   what frequency.
        
        double totalValue = 0;

        #ifdef DEBUG
            std::cout << "Elem | Value | Weight | Density | Frequency" << std::endl;
        #endif

        for (int i = 0, len = inserted.size(); i < len; i++) {
            Object obj = inserted[i];

            #ifdef DEBUG
                std::cout << "Elem | Value | Weight | Density | Frequency" << std::endl;
                std::cout << obj.elem   << " " << obj.value   << " " 
                          << obj.weight << " " << obj.density << " "
                          << obj.frequency << std::endl;
            #endif

            totalValue  += obj.frequency * obj.value;
        }
        
        std::cout << std::setprecision(15) << "," << totalValue << std::endl;

        delete [] objects;
        delete [] temp;
        inserted.clear();
    }

    return 0;
}
