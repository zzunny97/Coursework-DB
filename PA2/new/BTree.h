#include <iostream>
#include <cstdlib>
#include <cmath>
#include <vector>
#include <algorithm>

using namespace std;

// Note: You need to add additional member variables and functions as you need.

//#define NUM_KEYS 10 
#define NUM_KEYS 4
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


		BTreeNode(NodeType _type);
		~BTreeNode() {}
		NodeType getNodeType();
		string getNodeType2();
		bool can_merge(BTreeNode* sibling);
		bool can_borrow(BTreeNode* sibling);

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
			//cout << "num_keys: " << num_keys << endl;
			if(type == LEAF) {
				// leaf
				int num_val = num_keys;
				if(num_val < ceil((float)(NUM_KEYS)/2)) return true;
			}
			else if(type == INTERNAL) {
				// nonleaf
				int num_ptr = num_keys+1;
				if(num_ptr < ceil((float)(NUM_KEYS+1)/2))
					return true;
			}
			else {
				if(num_keys < 1) return true;
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
			if(n1->type == ROOT) {
				// create a new node r containing n1, value, n2
				// make r the root of the tree
				// make n1 to internal
				//cout << n1->getNodeType2() << " " << n2->getNodeType2() << endl;
				BTreeNode* newroot = new BTreeNode(ROOT);
				newroot->child[0] = n1;
				newroot->keys[0] = value;
				newroot->child[1] = n2;
				newroot->num_keys++;
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
			if(p->num_keys < NUM_KEYS) {
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
				// insert original parent keys and pointers
				//cout << "parent also doesn't have room" << endl;

				vector<long long> tmp_val;
				vector<BTreeNode*> tmp_ptr;
				for(int i=0; i<p->num_keys; i++) {
					tmp_val.push_back(p->keys[i]);
				}
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

				// prev node get more
				int index = ceil((float)(NUM_KEYS)/2);

				for(int i=0; i<index+1; i++) {
					p->child[i] = tmp_ptr[i];
					p->child[i]->parent = p;
				}
				for(int i=0; i<index; i++) {
					p->keys[i] = tmp_val[i];
				}
				p->num_keys = index;

				long long not_inserted_key = tmp_val[index];
				
				for(int i=index+1; i<tmp_ptr.size(); i++) {
					newparent->child[i-(index+1)] = tmp_ptr[i];
					newparent->child[i-(index+1)]->parent = newparent;
				}
				for(int i=index+1; i<tmp_val.size(); i++) {
					newparent->keys[i-(index+1)] = tmp_val[i];
				}
				newparent->num_keys = tmp_val.size() - (index+1);
				//p->printLeafNode();
				//newparent->printLeafNode();
				//cout <<"not inserted key: " << not_inserted_key << endl;
				insert_in_parent(p, not_inserted_key, newparent);

				// next node get more
				// int index = ceil((float)(NUM_KEYS+1)/2);

				// for(int i=0; i<index; i++) {
				// 	p->child[i] = tmp_ptr[i];
				// 	p->child[i]->parent = p;
				// }
				// for(int i=0; i<index-1; i++) {
				// 	p->keys[i] = tmp_val[i];
				// }
				// p->num_keys = index-1;

				// long long not_inserted_key = tmp_val[index-1];
				
				// for(int i=index; i<tmp_ptr.size(); i++) {
				// 	newparent->child[i-index] = tmp_ptr[i];
				// 	newparent->child[i-index]->parent = newparent;
				// }
				// for(int i=index; i<tmp_val.size(); i++) {
				// 	newparent->keys[i-index] = tmp_val[i];
				// }
				// newparent->num_keys = tmp_val.size() - index;
				// insert_in_parent(p, not_inserted_key, newparent);
			}
		}

		void insert(long long value) {
			cout << "[INST]: INSERT: " << value << endl;
			BTreeNode* cur = root;
			//cout << "traverse before insert" << endl;
			while(cur->getNodeType() != LEAF) {
				//cout << "cur node print" << endl;
				//cur->printLeafNode();
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
				//cout << "insert in leaf now" << endl;
				insert_in_leaf(cur, value);
			}
			else {
				//cout << "overfull issue, split first..." << endl;
				BTreeNode* newleaf = new BTreeNode(LEAF);
				// allocate temp vector to split overfull node easily
				vector<long long> tmp;
				for(int i=0; i<cur->num_keys; i++)   tmp.push_back(cur->keys[i]); // copy original keys to tmp_value
				tmp.push_back(value);	
				sort(tmp.begin(), tmp.end());
				newleaf->child[NUM_KEYS] = cur->child[NUM_KEYS]; // new leaf becomes right sibling of cur
				//for(int i=0; i<tmp.size(); i++) cout << tmp[i] << " ";
				//	cout << endl;
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
				
				insert_in_parent(cur, smallest_key_of_newleaf, newleaf);
			}
		}

		void delete_entry(BTreeNode* n, long long value) {
			cout << "****func: delete_entry****" << endl;
			cout << n->getNodeType2() << endl;
			n->printLeafNode();
			cout << "value: " << value << endl;
			cout << "cur_keys: " << n->num_keys << endl;
			// delete key from node n
			int deleted_idx = 10000;
			for(int i=0; i<n->num_keys; i++) {
				if(n->keys[i] == value) {
					deleted_idx = i;

					cout << "deleted_idx: " << i << endl;
					
					// if(n->type != LEAF) {
					// 	//n->child[i+1] = n->child[i];
					// 	// find minimum from its right subtree and update to deleted_idx
					// 	// cout << "deleted_idx is 0, find minimum from right subtree" << endl;
					// 	BTreeNode* cur = n->child[i+1];
					// 	while(cur->child[0] != NULL) {
					// 		cur = cur->child[0];
					// 	}
					// 	n->keys[i] = cur->keys[0];
					// 	cout << "print after find submax"  << endl;
					// 	n->printLeafNode();
						
					// }
					// else {
						if(i==0) {
							bool done = false;
							BTreeNode* p = n->parent;
							BTreeNode* c = n;
							while(p!= NULL && !done) {
								for(int j=0; j<p->num_keys; j++) {
									if(p->keys[j] == n->keys[i]) {
										p->keys[j] = n->keys[1];
										done = true;
										break;

									}
								}
								c = p;
								p = p->parent;
							}
						}

						cout << "shift<<<" << endl;
						for(int j=i; j<n->num_keys-1; j++) {
							n->keys[j] = n->keys[j+1];	
						}
						for(int j=i+1; j<n->num_keys; j++) {
							n->child[j] = n->child[j+1];
						}
						n->num_keys--;
						
					//}
					//n->printLeafNode();
					break;
				}
			}
			//cout << "number of key of root: " << root->num_keys << endl;

			n->printLeafNode();

			
			if(n->type == ROOT && n->num_keys == 0) {
				cout << "n is root and only have one remaining child" << endl;
				if(depth == 1) return;
				// make the child of n the new root
				// and delete n
				root = n->child[0];
				//n->child[0]->printLeafNode();
				depth--;
				n->child[0]->type = ROOT;
				delete n;
			}
			
			else if(n->too_few()) {
				BTreeNode* prev = n->sibling(true); // true means left sibling flag
				BTreeNode* next = n->sibling(false); // false means right sibling flag
				if(prev != NULL) {
					cout << "prev node: " << prev->type << endl;
					prev->printLeafNode();
				}
				if(next != NULL) {
					cout << "next node: " << next->type << endl;
					next->printLeafNode();
				}
				BTreeNode* parent = n->parent;
				long long k;

				if(n->can_borrow(prev) || n->can_borrow(next)) {
					cout << "It can borrow a key from its sibling" << endl;
					//n->printLeafNode();
					if(n->can_borrow(prev)) {
						// borrow key from left
						cout << "zzunny" << endl;
						if(n->type == LEAF) {
							int last_key_of_left = prev->keys[prev->num_keys-1];
							prev->num_keys--;
							// shift right of all keys in n
							for(int i=n->num_keys-1; i>=0; i--) {
								n->keys[i+1] = n->keys[i];
							}
							n->keys[0] = last_key_of_left;
							n->num_keys++;
						}
						else {
							cout << "borrow from left internal nodes" << endl;
						}
						
					}
					else {
						// borrow key from right
						cout << "zzunny2" << endl;
						if(n->type == LEAF) {
							int first_key_of_right = next->keys[0];
							for(int i=0; i<next->num_keys; i++ ) {
								next->keys[i] = next->keys[i+1];
							}
							next->num_keys--;
							// push to rightmost in n
							n->keys[n->num_keys++] = first_key_of_right;
						}
						else {
							cout << "borrow from right internal nodes" << endl;
						}
					}
					//n->printLeafNode();
					for(int i=0; i<n->parent->num_keys+1; i++) {
						if(n->parent->child[i] == n) {
							n->parent->keys[i-1] = n->keys[0];
							break;
						}
					}
					if(n->type != ROOT) delete_entry(n->parent, value);
					//cout << "end of node" << endl;
				}

				else if(n->can_merge(prev) || n->can_merge(next)) { // if can merge
					if(n->can_merge(prev)) {
						cout << "merge with left" << endl;
						// merge with left sibling
						// find k, the value between prev and n in parent	
						for(int i=0; i<parent->num_keys+1; i++) {
							if(parent->child[i] == prev && parent->child[i+1] == n) {
								k = parent->keys[i];
								// for(int j=i; j<n->parent->num_keys; j++)
								// 	n->parent->keys[j] = n->parent->keys[j+1];
								// for(int j=i+1; j<n->parent->num_keys+1; j++)
								// 	n->parent->child[j] = n->parent->child[j+1];
								//n->parent->child[i+1] = NULL;
								//n->parent->num_keys--;
								break;
							}
						}
						cout << "k: " << k << endl;
						if(n->type != LEAF) {
							// append k and all pointers and values in n to prev
							//prev->printLeafNode();
							prev->keys[prev->num_keys++] = k;
							//prev->printLeafNode();
							int to_be_inserted = prev->num_keys;
							for(int i=0; i<n->num_keys; i++) {
								prev->keys[prev->num_keys++] = n->keys[i];
							}
							for(int i=0; i<n->num_keys+1; i++) {
								prev->child[to_be_inserted++] = n->child[i];
								n->child[i]->parent = prev;
							}
							//prev->printLeafNode();
						}
						else {
							for(int i=0; i<n->num_keys; i++) {
								prev->keys[prev->num_keys++] = n->keys[i];
							}
							prev->child[NUM_KEYS] = n->child[NUM_KEYS];
						}
						
						prev->printLeafNode();
						
						delete_entry(n->parent, k);
						delete n;

					}
					else {
						cout << "merge with right" << endl;
						// merge with right sibling
						// find k, the value between n and next in parent
						for(int i=0; i<parent->num_keys+1; i++) {
							if(parent->child[i] == n && parent->child[i+1] == next) {
								k = parent->keys[i];
								// for(int j=i; j<n->parent->num_keys; j++)
								// 	n->parent->keys[j] = n->parent->keys[j+1];
								// for(int j=i+1; j<n->parent->num_keys+1; j++)
								// 	n->parent->child[j] = n->parent->child[j+1];
								//n->parent->child[i] = NULL;
								//n->parent->num_keys--;
								break;
							}
						}
						cout << "k: " << k << endl;
						if(next->type != LEAF) {
							// append k and all pointers and values in next to n
							n->keys[n->num_keys++] = k;
							int to_be_inserted = n->num_keys;
							for(int i=0; i<next->num_keys; i++) {
								n->keys[n->num_keys++] = next->keys[i];
							}
							for(int i=0; i<next->num_keys+1; i++) {
								n->child[to_be_inserted++] = next->child[i];
								next->child[i]->parent = n;
							}
						}
						else {
							for(int i=0; i<next->num_keys; i++) {
								n->keys[n->num_keys++] = next->keys[i];
							}
							n->child[NUM_KEYS] = next->child[NUM_KEYS];
						}
						//delete_entry(next->parent, k, next);
						n->printLeafNode();
						delete_entry(next->parent, k);
						delete next;
					}
				} // end-if can merge
				else { // else redistribute
					// redistribute
					if(prev != NULL) {
						cout << "redistribute with left" << endl;
						// find k, the value between prev and n in parent	
						int k_idx;
						for(int i=0; i<parent->num_keys+1; i++) {
							if(parent->child[i] == prev && parent->child[i+1] == n) {
								k = parent->keys[i];
								k_idx = i;
								break;
							}
						}

						if(n->type != LEAF) {
							BTreeNode* last_ptr = prev->child[prev->num_keys];
							long long  last_val = prev->keys[prev->num_keys-1];
							prev->num_keys--;
							for(int i=n->num_keys-1; i>=0; i--) n->keys[i+1] = n->keys[i];
							for(int i=n->num_keys; i>=0; i--)   n->child[i+1] = n->child[i];
							n->keys[0] = k;
							n->child[0] = last_ptr;
							n->num_keys++;
							parent->keys[k_idx] = last_val;
						}
						else {
							long long last_val = prev->keys[prev->num_keys-1];
							prev->num_keys--;
							for(int i=n->num_keys-1; i>=0; i--) n->keys[i+1] = n->keys[i];
							n->keys[0] = last_val;	
							n->num_keys++;
							parent->keys[k_idx] = last_val;
						}
					}

					else if(next != NULL) {
						cout << "redistribute with right" << endl;
						// find k, the value between prev and n in parent	
						// prev -> n
						// n -> next:
						int k_idx;
						for(int i=0; i<parent->num_keys+1; i++) {
							if(parent->child[i] == n && parent->child[i+1] == next) {
								k = parent->keys[i];
								k_idx = i;
								break;
							}
						}

						if(next->type != LEAF) {
							BTreeNode* last_ptr = n->child[n->num_keys];
							long long  last_val = n->keys[n->num_keys-1];
							n->num_keys--;
							for(int i=next->num_keys-1; i>=0; i--) next->keys[i+1] = next->keys[i];
							for(int i=next->num_keys; i>=0; i--)   next->child[i+1] = next->child[i];
							next->keys[0] = k;
							next->child[0] = last_ptr;
							next->num_keys++;
							parent->keys[k_idx] = last_val;
						}
						else {
							long long last_val = n->keys[n->num_keys-1];
							n->num_keys--;
							for(int i=next->num_keys-1; i>=0; i--) next->keys[i+1] = next->keys[i];
							next->keys[0] = last_val;	
							next->num_keys++;
							parent->keys[k_idx] = last_val;
						}

					}
					else {
						cout << "[ERROR] BTree::delete_entry(): prev and next both NULL" << endl;
						exit(1);
					}
				} // else redistribute

			}
			else { // until now, no underfull
				cout << "Nothing happend, check if the deleted_idx is 0" << endl;
				//cout << "deleted_idx: " << deleted_idx << endl;

				/*
				if(n->type != ROOT) {
					if(deleted_idx==0) {
					//n->parent->printLeafNode();
						for(int i=0; i<n->parent->num_keys+1; i++) {
							if(n->parent->child[i] == n) {
								n->parent->keys[i-1] = n->keys[0];
								break;
							}
						}
						//n->parent->printLeafNode();
					}

					delete_entry(n->parent, value);
				}*/
				if(n->type != ROOT)
					delete_entry(n->parent, value);
			}

		}

		void remove(long long value) {
			cout << "[INST]: REMOVE: " << value << endl;
			BTreeNode* cur = root;
			while(cur->getNodeType() != LEAF) {
				//cur->printLeafNode();
				if(depth==1)  break;
				int i = cur->find_idx(value);
				//cout << "find_idx returns " << i << endl;
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

		void printRoot() {
			cout << "[INST]: PRINT_ROOT" << endl;
			BTreeNode* cur = root;
			for(int i=0; i<cur->num_keys; i++) {
				cout << cur->keys[i] << " ";
			}
			cout << endl;
		}

		void printAllLeafNode() {
			cout << "[INST]: PRINT_ALL_LEAF_NODE" << endl;
			BTreeNode* cur = root;
			while(cur->type != LEAF) {
				if(depth==1) break;
				cur = cur->child[0];
			}
			while(cur != NULL) {
				for(int i=0; i<cur->num_keys; i++) {
					cout << cur->keys[i] << " ";
				}
				cout << " // ";
				cur = cur->child[NUM_KEYS];
			}
			cout << endl;
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
			//cur->printLeafNode();
			int i;
			for(i=0; i<cur->num_keys; i++) {
				if(cur->keys[i] >= low) break;
			}
			if(i==cur->num_keys) i = 1 + cur->num_keys;
			bool done = false;
			while(!done) {
				int n = cur->num_keys;
				//cout << "i: " << i << " n: " << n << endl;
				if(i<n && cur->keys[i] <= high) {
					cout << cur->keys[i] << ", ";
					i++;
				}
				else if(i<n && cur->keys[i] > high) {
					done = true;
				}
				else if(i>=n && cur->child[NUM_KEYS] != NULL) {
					cur = cur->child[NUM_KEYS];
					i=0;
				}
				else {
					done = true;
				}
			}
			cout << endl;
		}
		// print all found keys (low <= keys < high), separated by comma (e.g., 10, 11, 13, 15\n) 
};
