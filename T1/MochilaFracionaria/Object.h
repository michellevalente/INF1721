#ifndef OBJECT_H
#define OBJECT_H

class Object {

public:
    int weight, value, elem;
    double density, frequency;

    Object ();

    Object (int p, int v, int n);

    Object( const Object &obj);

    const Object &operator=(const Object& obj);
};

bool operator< (const Object& lhs, const Object& rhs);

bool operator> (const Object& lhs, const Object& rhs);

bool operator<=(const Object& lhs, const Object& rhs);

bool operator>=(const Object& lhs, const Object& rhs);

bool operator==(const Object& lhs, const Object& rhs);

bool operator!=(const Object& lhs, const Object& rhs);


/**
 * Object comparison.
 */
bool object_compare (Object obj1, Object obj2);

#endif
