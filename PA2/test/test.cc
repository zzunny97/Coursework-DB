#include <iostream>
#include <vector>

using namespace std;

void print_vec(vector<int>& v) {
	for(auto iter = v.begin(); iter!=v.end(); iter++)
		cout << *iter << " ";
	cout << endl;
}

int main() {
	vector<int> v;
	for(int i=0; i<10; i++) {
		v.push_back(i);
	}

	print_vec(v);

	v.insert(v.begin() + 1, 1000);

	print_vec(v);

}
