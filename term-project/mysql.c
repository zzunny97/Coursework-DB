#include <stdio.h>
#include <mysql.h>
#include <my_global.h>

int main(int argc, char* argv[])
{
  MYSQL *conn = mysql_init(NULL);
  if (conn==NULL){
      printf("mysql_init: %s\n", mysql_error(conn));
      exit(1);
  }

  if(mysql_real_connect(conn, "localhost", "bnam", "changethis", "dbbnam", 0, NULL, 0)==NULL){
      printf("mysql_real_connect: %s\n", mysql_error(conn));
      mysql_close(conn);
      exit(1);
  }

  char * sqlquery = "select dept_name, avg(salary) \
                     from instructor \
                     group by dept_name ";
  if(mysql_query(conn, sqlquery)){
      printf("mysql_query: %s\n", mysql_error(conn));
      mysql_close(conn);
      exit(1);
  }

  MYSQL_RES *result = mysql_store_result(conn);
  if(result == NULL){
      printf("mysql_store_result: %s\n", mysql_error(conn));
      mysql_close(conn);
      exit(1);
  }

  int num_fields = mysql_num_fields(result);

  MYSQL_ROW row;
  while ((row = mysql_fetch_row(result))) {
      for(int i=0; i<num_fields; i++){
          printf (" %s ", row[i]? row[i] : "NULL");
      }
      printf("\n");
  }

  mysql_free_result(result);
  mysql_close(conn);

  return 1;
}
