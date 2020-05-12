#include <iostream>
#include <stdlib.h>
#include <math.h>

using namespace std;

// Note: You need to add additional member variables and functions as you need.

#define NUM_KEYS 10 
// NUM_KEYS should be set to make each tree node as large as 4KB. 
// But for this assignment, we will set the degree of node to 10 just to make debugging and grading easy
// Please do not change this number. 
// In practice, DBMS employs 'slotted page structure' to store variable-length records in B+tree.
// But again, the purpose of this assignment is to make students familiar with B-tree itself. 

enum NodeType {
	ROOT,
	INTERNAL,
	LEAF
};

class BTreeNode {   
	public:
		long long keys[NUM_KEYS];
		int num_keys;
		BTreeNode* parent;
		BTreeNode* right_sibling;
		BTreeNode* child[NUM_KEYS+1];
		NodeType type;


		BTreeNode(NodeType _type) {
			num_keys = 0;	
			parent = NULL;
			right_sibling = NULL;
			for(int i=0; i<NUM_KEYS; i++) child[i] = NULL;
			type = type;
		}
		~BTreeNode() {}
		NodeType getNodeType() {
			return type;
		}
		void printLeafNode() {
			if(type != LEAF) {
				cout << "trying to call printLeafNode func which is not leaf" << endl;	
				exit(1);
			}
			else {
				for(int i=0; i<num_keys; i++) {
					cout << keys[i] << " ";
				}
				cout << endl;
			}
		}
		int find_idx(long long value) {
			int i;
			for(i=0; i<num_keys; i++) {
				if(value <= keys[i]) break;
			}
			return i;
		}

		int get_child_num() {
			return num_keys+1;
		}

		BTreeNode* find_last_non_null() {
			for(int i=0; i<num_keys+1; i++) {
				if(child[i] != NULL) return child[i];
			}
			cout << "emergency! find_last_non_null is NULL" << endl;
			return NULL;
		}
};

class BTree {  
	private:
		BTreeNode *root;
	public:
		// You are not allowed to change the interfaces of these public methods.
		BTree() {
			root = new BTreeNode(ROOT);
		}
		~BTree() {}

		void inert_in_leaf(BTreeNode* n, long long value) {
			if(value < n->keys[0]) {
				for(int i=n->num_keys-1; i>=0; i--) {
					n->keys[i+1] = n->keys[i];
				}
				n->keys[0] = value;
				n->num_keys++;
			}
			else {
				for(int i=0; i<n->num_keys; i++) {
					if(n->keys[i] > value) {
						for(int j=n->num_keys-1; j>=i; j--) {
							n->keys[j+1] = n->keys[j];	
						}
						n->keys[i] = value;
						n->num_keys++;
						break;
					}
				}
			}
		}

		void insert_in_parent(BTreeNode* n1, long long value, BTreeNode* n2) {
			if(n1->getNodeType() == ROOT) {
				// create a new node r containing n1, value, n2
				// make r the root of the tree
				// make n1 to internal
				return;
			}
			BTreeNode* p = n1->parent;
			if(p->get_child_num() < NUM_KEYS) {
				for(int i=0; i<p->num_keys+1; i++) {
					if(p->child[i] == n1) {
						for(int j=n1->num_keys-1; j>=i; j--) {
							n1->keys[j+1] = n1->keys[j];
						}
						n1->keys[i] = value;
						for(int j=n1->num_keys; j>=i+1; j--) {
							n1->child[j+1] = n1->child[j];
						}
						n1->child[i+1] = n2;
						n2->parent = n1;
						break;
					}
				}
			}
			else {
				BTreeNode tmp(LEAF);

			}
		}

		void insert(long long value) {
			BTreeNode* cur = root;
			while(cur->getNodeType() != LEAF) {
				int i = cur->find_idx(value);
				if(i==cur->num_keys) {
					cur = cur->find_last_non_null();	
				}
				else if(value == cur->keys[i]) {
					cur = cur->child[i+1];
				}
				else {
					cur = cur->child[i];
				}
			}
			if(cur->num_keys < NUM_KEYS-1) {
				insert_in_leaf(cur, value);
			}
			else {
				BTreeNode* newleaf = new BTreeNode(LEAF);
				BTreeNode tmp(LEAF);
				for(int i=0; i<cur->num_keys; i++) {
					tmp.keys[tmp.num_keys++] = cur->keys[i];
				}
				insert_in_leaf(&tmp, value);
				newleaf->child[NUM_KEYS] = cur->child[NUM_KEYS];
				cur->child[NUM_KEYS] = newleaf;
				cur->num_keys = 0;
				for(int i=0; i<tmp.num_keys; i++) {
					if(i < ceil((float)NUM_KEYS/2)) {
						cur->keys[cur->num_keys++] = tmp.keys[i];	
					} else {
						newleaf->keys[newleaf->num_keys++] = tmp.keys[i];
					}
				}
				insert_in_parent(cur, newleaf->keys[0], newleaf);
			}
		}

		void remove(long long value) {}
		void printLeafNode(long long value) {
			BTreeNode* cur = root;
			while(cur->getNodeType() != LEAF) {
				int i = cur->find_idx(value);
				if(i==cur->num_keys) {
					cur = cur->find_last_non_null();	
				}
				else if(value == cur->keys[i]) {
					cur = cur->child[i+1];
				}
				else {
					cur = cur->child[i];
				}
			}
			bool is_found = false;
			for(int i=0; i<cur->num_keys; i++) {
				if(value == cur->keys[i]) {
					is_found = true;
				} 
			}
			if(is_found) {
				for(int i=0; i<cur->num_keys; i++) {
					cout << cur->keys[i] << ", ";
				}
				cout << endl;
			}
		}
		// find the leaf node that contains 'value' and print all values in the leaf node.
		void pointQuery(long long value) {
			BTreeNode* cur = root;
			while(cur->getNodeType() != LEAF) {
				int i = cur->find_idx(value);
				if(i==cur->num_keys) {
					cur = cur->find_last_non_null();	
				}
				else if(value == cur->keys[i]) {
					cur = cur->child[i+1];
				}
				else {
					cur = cur->child[i];
				}
			}
			for(int i=0; i<cur->num_keys; i++) {
				if(value == cur->keys[i]) {
					cout << value << endl;
				} else {
					cout << "NOT FOUND" << endl;
				}
			}
		}
		// print the found value or "NOT FOUND" if there is no value in the index
		void rangeQuery(long long low, long long high) {
			BTreeNode* cur = root;
			while(cur->getNodeType() != LEAF) {
				int i = cur->find_idx(low);
				if(i==cur->num_keys) {
					cur = cur->find_last_non_null();
				}
				else if(low == cur->keys[i]) {
					cur = cur->child[i+1];
				}
				else {
					cur = cur->child[i];
				}
			}
			int i;
			for(int i=0; i<cur->num_keys; i++) {
				if(cur->keys[i] >= low) break;
			}
			if(i==cur->num_keys+1) i = 1+cur->num_keys;
			bool done = false;
			while(!done) {
				int n = cur->num_keys;
				if(i<=n && cur->keys[i] <= high) {
					cout << cur->keys[i] << ", ";
					i++;
				}
				else if(i<=n && cur->keys[i] > high) {
					done = true;
				}
				else if(i>n && cur->right_sibling != NULL) {
					cur = cur->right_sibling;
					i=1;
				}
				else {
					done = true;
				}
			}
			cout << endl;
		}
		// print all found keys (low <= keys < high), separated by comma (e.g., 10, 11, 13, 15\n) 
};
