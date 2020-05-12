#include <iostream>
#include <cstdlib>
#include <cmath>
#include <vector>
#include <algorithm>

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
		BTreeNode* child[NUM_KEYS+1];
		NodeType type;


		BTreeNode(NodeType type) {
			num_keys = 0;	
			parent = NULL;
			for(int i=0; i<NUM_KEYS; i++) child[i] = NULL;
			this->type = type;
		}
		~BTreeNode() {}
		NodeType getNodeType() {
			return type;
		}

		BTreeNode* sibling(bool is_left) {
			BTreeNode* parent = this->parent;
			if(parent == NULL) {
				cout << "[ERROR] BTreeNode::sibling(), parent not defined" << endl;
				exit(1);
			}
			int i;
			for(i=0; i<parent->num_keys+1; i++) {
				if(parent->child[i] == this) {
					break;
				}
			}
			if(i==parent->num_keys+1) {
				cout << "[ERROR] BTreeNode::sibling(), child not found" << endl;
				exit(1);
			}
			if(is_left) {
				// find left sibling
				if(i>0) return this->parent->child[i-1];
			}
			else {
				// find right sibling
				if(i<parent->num_keys) return this->parent->child[i+1];
			}
			return NULL;
		}

		bool too_few() {
			if(type == LEAF) {
				// leaf
				int num_val = num_keys;
				if(num_val < ceil((float)(NUM_KEYS-1)/2)) return true;
			}
			else {
				// nonleaf
				int num_ptr = num_keys+1;
				if(num_ptr < ceil((float)(NUM_KEYS)/2))
					return true;
			}
			return false;
		}

		void printLeafNode() {
			/*
			if(type != LEAF) {
				cout << "trying to call printLeafNode func which is not leaf" << endl;	
				exit(1);
			}
			*/
			//else {
				for(int i=0; i<num_keys; i++) {
					cout << keys[i] << " ";
				}
				cout << endl;
			//}
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
			for(int i=num_keys; i>=0; i--) {
				if(child[i] != NULL) return child[i];
			}
			cout << "emergency! find_last_non_null is NULL" << endl;
			return NULL;
		}
};

class BTree {  
	private:
		BTreeNode *root;
		int depth;
	public:
		// You are not allowed to change the interfaces of these public methods.
		BTree() {
			root = new BTreeNode(ROOT);
			depth=1;
		}
		~BTree() {}

		void insert_in_leaf(BTreeNode* n, long long value) {
			if(n->num_keys == 0) {
				n->keys[n->num_keys++] = value;
				return;
			}
			int idx = n->find_idx(value);
			if(idx == n->num_keys) {
				n->keys[n->num_keys++] = value;
			}
			else {
				for(int i=n->num_keys-1; i>= idx; i--)
					n->keys[i+1] = n->keys[i];
				n->keys[idx] = value;
				n->num_keys++;
			}
		}

		void insert_in_parent(BTreeNode* n1, long long value, BTreeNode* n2) {
			if(n1->getNodeType() == ROOT) {
				// create a new node r containing n1, value, n2
				// make r the root of the tree
				// make n1 to internal
				cout << "insert in parent root changes" << endl;
				BTreeNode* newroot = new BTreeNode(ROOT);
				newroot->child[0] = n1;
				newroot->keys[0] = value;
				newroot->num_keys++;
				newroot->child[1] = n2;
				if(depth == 1) {
					n1->type = LEAF;
					n2->type = LEAF;
				} 
				else {
					n1->type = INTERNAL;
					n2->type = INTERNAL;
				}
				n1->parent = newroot;
				n2->parent = newroot;
				root = newroot;
				depth++;
				return;
			}
			BTreeNode* p = n1->parent;
			if(p->num_keys+1 < NUM_KEYS) {
				// insert value and pointer n2 into p right after n1
				//cout << "room available in parent" << endl;
				for(int i=0; i<p->num_keys+1; i++) {
					if(p->child[i] == n1) {
						for(int j=p->num_keys-1; j>=i; j--) p->keys[j+1] = p->keys[j]; // move keys backward
						for(int j=p->num_keys; j>=i+1; j--) p->child[j+1] = p->child[j]; // move childs backward
						p->keys[i] = value;
						p->child[i+1] = n2;
						p->num_keys++;
						n2->parent = p;
						break;
					}
				}
			}
			else {
				vector<long long> tmp_val;
				vector<BTreeNode*> tmp_ptr;
				for(int i=0; i<p->num_keys; i++) tmp_val.push_back(p->keys[i]);
				for(int i=0; i<p->num_keys+1; i++) tmp_ptr.push_back(p->child[i]);
				for(int i=0; i<tmp_ptr.size(); i++) {
					if(tmp_ptr[i] == n1) {
						tmp_val.insert(tmp_val.begin() + i, value);
						tmp_ptr.insert(tmp_ptr.begin() + (i+1) ,n2);
						break;
					}
				}
				p->num_keys = 0;
				BTreeNode* newparent = new BTreeNode(INTERNAL);
				int index = ceil((float)(NUM_KEYS+1)/2);
				for(int i=0; i<index; i++) {
					p->child[i] = tmp_ptr[i];
					p->child[i]->parent = p;
				}
				for(int i=0; i<index-1; i++) {
					p->keys[i] = tmp_val[i];
				}
				p->num_keys = index-1;
				long long not_inserted_key = tmp_val[index];
				for(int i=index+1; i<tmp_ptr.size(); i++) {
					newparent->child[i-index-1] = tmp_ptr[i];
					newparent->child[i-index-1]->parent = newparent;
				}
				for(int i=index+1; i<tmp_val.size(); i++) {
					newparent->keys[i-index-1] = tmp_val[i];
				}
				newparent->num_keys = tmp_val.size() - index;
				insert_in_parent(p, not_inserted_key, newparent);
			}
		}

		void insert(long long value) {
			cout << "[INST]: INSERT: " << value << endl;
			BTreeNode* cur = root;
			//cout << "traverse before insert" << endl;
			while(cur->getNodeType() != LEAF) {
				cout << "cur node print" << endl;
				cur->printLeafNode();
				if(depth==1) break;
				int i = cur->find_idx(value);
				//cout << i << endl;
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
			if(cur->num_keys < NUM_KEYS) {
				cout << "insert in leaf now" << endl;
				insert_in_leaf(cur, value);
			}
			else {
				cout << "overfull issue, split first..." << endl;
				BTreeNode* newleaf = new BTreeNode(LEAF);
				// allocate temp vector to split overfull node easily
				vector<long long> tmp;
				for(int i=0; i<cur->num_keys; i++)   tmp.push_back(cur->keys[i]); // copy original keys to tmp_value
				tmp.push_back(value);	
				sort(tmp.begin(), tmp.end());
				newleaf->child[NUM_KEYS] = cur->child[NUM_KEYS]; // new leaf becomes right sibling of cur
				//for(int i=0; i<tmp.size(); i++) cout << tmp[i] << " ";
				//cout << endl;
				cur->child[NUM_KEYS] = newleaf;
				cur->num_keys = 0;
				for(int i=0; i<tmp.size(); i++) {
					if(i < ceil((float)(NUM_KEYS+1)/2)) {
						cur->keys[cur->num_keys++] = tmp[i];
						//cur->printLeafNode();
					} else {
						newleaf->keys[newleaf->num_keys++] = tmp[i];
						//newleaf->printLeafNode();
					}
				}
				int smallest_key_of_newleaf = newleaf->keys[0];
				//cout << "smallest_key_of_newleaf: " << smallest_key_of_newleaf << endl;
				//cout << "function : insert_in_parent" << endl;
				insert_in_parent(cur, smallest_key_of_newleaf, newleaf);
			}
			cout << "print inserted node" << endl;
			printLeafNode(value);
			//cout << "insert end" << endl;
		}

		void delete_entry(BTreeNode* n, long long value) {
			// delete key from node n
			for(int i=0; i<n->num_keys; i++) {
				if(n->keys[i] == value) {
					for(int j=i; j<n->num_keys; j++) {
						n->keys[j] = n->keys[j+1];	
					}
					n->num_keys--;
				}
			}
			if(n->type == ROOT && n->num_keys==0) {
				// make the child of n the new root
				// and delete n
				root = n->child[0];
				delete n;
			}
			else if(n->too_few()) {
				BTreeNode* left_sibling = n->sibling(true); // true means left sibling flag
				BTreeNode* right_sibling = n->sibling(false); // false means right sibling flag
				if(n->CanMerge(left_sibling) || n->CanMerge(right_sibling)) {
					
				}
			}
		}

		void remove(long long value) {
			cout << "[INST]: REMOVE: " << value << endl;
			BTreeNode* cur = root;
			while(cur->getNodeType() != LEAF) {
				if(depth==1)  break;
				int i = cur->find_idx(value);
				if(i==cur->num_keys) {
					cur = cur->find_last_non_null();
					return;
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
				if(value == cur->keys[i]) is_found = true;
			}
			if(is_found) {
				delete_entry(cur, value);
			}
			else {
				cout << "No such key" << endl;
				return;
			}
		}
		void printLeafNode(long long value) {
			cout << "[INST]: PRINT_LEAF_NODE: " << value << endl;
			BTreeNode* cur = root;
			while(cur->getNodeType() != LEAF) {
				if(depth==1) break;
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
			cout << "[INST]: POINT_QUERY: " << value << endl;
			BTreeNode* cur = root;
			while(cur->getNodeType() != LEAF) {
				if(depth==1) break;
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
			if(is_found) cout << value << endl;
			else cout << "NOT FOUND" << endl;
		}
		// print the found value or "NOT FOUND" if there is no value in the index
		void rangeQuery(long long low, long long high) {
			cout << "[INST]: RANGE_QUERY: " << low << " " << high << endl;
			BTreeNode* cur = root;
			while(cur->getNodeType() != LEAF) {
				if(depth==1) break;
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
			cur->printLeafNode();
			int i;
			for(i=0; i<cur->num_keys; i++) {
				if(cur->keys[i] >= low) break;
			}
			if(i==cur->num_keys+1) i = 1+cur->num_keys;
			bool done = false;
			while(!done) {
				int n = cur->num_keys;
				cout << "i: " << i << " n: " << n << endl;
				if(i<n && cur->keys[i] <= high) {
					cout << cur->keys[i] << ", ";
					i++;
				}
				else if(i<n && cur->keys[i] > high) {
					done = true;
				}
				else if(i>=n && cur->child[NUM_KEYS] != NULL) {
					cur = cur->child[NUM_KEYS];
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
