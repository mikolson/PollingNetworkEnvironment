//#include <stdio.h>
//#include <math.h>
//#include <scsl_blas.h>
//#if defined(VAMPIR_TRACE)
//#include <vpt.h>
//#endif
//#include <mpi.h>
//
//#define N 10
//#define A 10
//#define LDA N
//
//double wektor_x[N];
//double wektor_y[N*A];
//
//double macierz_P[N*A][N];
//double macierz_V[N];
//double macierz_R[N][N][A];
//int rank;
//int size;
//
//
//double max_xy(double x, double y) {
//  if (x >= y)  return x;
//  else  return y;
//}
//
//int min_xy(int x, int y) {
//  if (x < y)  return x;
//  else  return y;
//}
//
//double suma_V(int i, int a) {
//  int j;  
//  double result;
//  
//  for (j = 0; j < N; j++) {
//    result += macierz_P[i*A+a][j] * macierz_V[j];
//  }
//  
//  return result;
//}
//double suma_V_fast(int i, int a) {
//  double result[1];
//  dgemv("T",N,1,1,(double *)macierz_P[i*A+a],LDA,macierz_V,1,0,result,1);
//  return result[0];
//}
//
//double suma_Vii(int i, int a) {
//  int j;  double result = 0;
//  
//  for (j = 0; j < i; j++) {
//    result += macierz_P[i*A+a][j] * macierz_V[j];
//  }
//  for (j = i+1; j < N; j++) {
//    result += macierz_P[i*A+a][j] * macierz_V[j];
//  }
//  
//  return result;
//}
//
//double suma_A(int i, int a) {
//  int j; double result = 0;
//  for (j=0; j < N; j++) {
//    result += macierz_R[i][j][a] * macierz_P[i*A+a][j];
//  }
//  return result;
//}
//
//void iteracja_V() { 
// int i,j,a,s;
// double delta;
// double max_delta, max_a;
// double v; 
// double tmp[N];
// double r[N][A];
// double beta = 0.4;
// double epsilon = 0.00001;
//
// double rec_wektor_V[size*N];
// int rec_count[size];
// int displs[size];
// int podzial = 0;
// int podzialy[size+1];
//
// printf("Size: %d, Rank: %d", size,rank);
//
// 
//  for(i=0; i<N;i++) {
//    macierz_V[i] = i;
//  }
//  
//  for(i=0; i < size; i++){
//    rec_count[i] = N;
//    displs[i] = i*N;
//  }
// 
// for(a = 0; a < A; a++){
//  for (i = 0; i < N; i++){
//     r[i][a] = suma_A(i,a); 
//  }
// }
// 
// podzial = N / size;
// for (i=0; i<(size+1); i++) {
//  podzialy[i] = i*podzial;
// }
// podzialy[size-1] = N;
//
//  MPI_Barrier(MPI_COMM_WORLD);
// 
// do {
//  // Obliczenie funkcji wartosci dla n-tej chwili czasu na 
//  // podstawie stanu systemu z chwili N-1
//  max_a = -1;
//  
//  for(i=0; i<N;i++) {
//    tmp[i] = 0;
//  }  
////  for (i = podzialy[rank]; i < podzialy[rank+1]; i++) {
//  for (i = 0; i < N; i++) {
//   for (a = 0; a < N; a++) {
//      v = r[i][a] + beta * suma_V(i,j);
//      if (v > max_a) max_a = v;
//    }
//    tmp[i] = max_a;
//    max_a = -1;
//  }
//  
//  if (rank==0){
//  printf("\n---\n");
//  for (i=0; i<N;i++){
//    printf("%d: %f\n",i,tmp[i]);
//  }
//  }
//  
//  
//  MPI_Allgatherv(tmp,N,MPI_DOUBLE, rec_wektor_V, rec_count, displs, MPI_DOUBLE, MPI_COMM_WORLD);
//  a=0;
//  
///*
//  if (rank==0){
//  printf("\n---\n");
//  for (i=0; i<size;i++){
//   for (j=0; j < N; j++) {
//    printf("%f |", rec_wektor_V[i*N+j]);
//   }
//   printf("\n");
//  }
//  }
//  */
//  
//  for(i=0; i<size; i++) {
//    for(j=podzialy[i]; j< podzialy[i+1];j++){
////    tmp[a++] = rec_wektor_V[i*N+j];
//      delta = rec_wektor_V[i*N+j];
//     
//    }
//  }
//  MPI_Barrier(MPI_COMM_WORLD);
//   
//  max_delta = 0; 
//  // Przepisanie nowych wartosci V do macierzy funkcji wartosci
//  for (s=0; s < N; s++) {
//    delta = fabsl(macierz_V[s] - tmp[s]);
//    if (delta > max_delta) max_delta = delta;
//    macierz_V[s] = tmp[s];
//  }
//  
//  printf("Delta: %f\n", max_delta);
// } while(  (beta/(1-beta)*max_delta) >= epsilon);// while
//
//}
//
//
//void wypelnij_R() {
//  int i,j,a;   
//  for (i = 0; i < N; i++){
//    for (j = 0; j < N; j++) {
//      for (a = 0; a < A; a++) {
//         macierz_R[i][j][a] = exp(sinl(i+j+a+3)); // +3 ze wzgledu na indeksy od 0
//      }
//    }
//  } 
//}
//
//// Juz nie potrzebne
//double oblicz_P(int i, int j, int a){
//  double result = 0;
//  double sum = 0;
//  int x;
//  double s; 
//  
//  s = fabs(pow((double)sinl(i - a),2));
//  result = s+fabs(sinl(j-a));
//    
//  for (x = 1; x<= N; x++ ){
//     sum += fabs((double)sinl(x-a));
//  }
//  
//  result = (double)result / (N * (double)s + (double)sum);
//  return result;
//}
//
//void wypelnij_P() {
//   int i,j,a;
//   double sum, s;
//
//   for(a=0; a < A; a++) {
//      sum = 0;
//      for(j=0; j < N; j++) {
//        sum += fabs(sin(j-a));
//      }        
//   
//     for(i = 0; i < N; i++) {
//       for(j=0; j < N; j++) {
//         s = fabs(pow(sin(i - a),2));
//	 if ((s + fabs(sin(j-a)))/(N*s + sum) > 1) {
//	  printf(">1 %d %d %d1;3B\n", i,j,a);
//	  printf("\t,%f\n",(s + fabs(sin(j-a))));
//
//	  printf("\t,%f\n",sum);
//	  printf("\t,%f\n",N*s);
//
//	 } 
//         macierz_P[i*A+a][j] = (s + fabs(sin(j-a)))/(N*s + sum);
//       }
//     }
//   }
//   
///*   for (i=0; i < N; i++) {
//     for (a=0; a < A; a++) {
//       for (j=0; j < N; j++) {
//         printf(" %f3:31;5C",macierz_P[i*A+a][j]);
//       }
//       printf("\n");
//     } 
//   }*/
//}
//
//void wypelnij_x() {
//  int i,j;    
//  for (i = 0; i < N; i++) {
//    for(j=0; j<1; j++) {
//     wektor_x[i] = 1;
//    }
//  }
//}
//
//
//// Na wyjsciu wypelnia wartosciami wektor Y
//// Mnozac macierzS przez wektor X
//void mnozenie() {
// int i,j,a;
// printf("Mnozenie macierzy\n");
// for(a=0; a < A; a++) {
//  for(i=0; i<N; i++){
//     for(j=0; j<N; j++) {
//	 wektor_y[i*A+a] += macierz_P[i*A+a][j];
//     }
//  }
// }
//}
//
///*
//NAME 
//DGEMV - perform one of the matrix-vector operations y := alpha*A*x + beta*y, or y := alpha*A'*x + beta*y,
//SYNOPSIS 
//
//SUBROUTINE DGEMV
//    ( TRANS, M, N, ALPHA, A, LDA, X, INCX, BETA, Y, INCY ) 
//        DOUBLE PRECISION ALPHA, BETA 
//        INTEGER INCX, INCY, LDA, M, N 
//        CHARACTER*1 TRANS 
//        DOUBLE PRECISION A( LDA, * ), X( * ), Y( * )
//*/
//void mnozenie_BLAS() {
//  dgemv("T",N,N*A,1,(double *)macierz_P,LDA,wektor_x,1,0,wektor_y,1);
//}
//
//void wyswietl() {
//  int j;
//    for(j=0; j < N*A; j++) {
//      printf("%f, ",wektor_y[j]);
//  }
//  
//  printf("-----\n");
//}
//
//void wyswietl_V() {
// int s; 
// for(s = 0; s < N; s++){
//   printf("%f\n", macierz_V[s]);
// }
//}
//
//
//main(int argc, char *argv[]) {
//
//
//#if defined(VAMPIR_TRACE)
//  _vptsetup();
//  _vptenter(300);
//  MPI_Init_rep( &argc, &argv);
//  MPI_Comm_size_rep( MPI_COMM_WORLD, &size);
//  MPI_Comm_rank_rep( MPI_COMM_WORLD, &rank);
//#else
//  MPI_Init( &argc, &argv);
//  MPI_Comm_size( MPI_COMM_WORLD, &size);
//  MPI_Comm_rank( MPI_COMM_WORLD, &rank);
//#endif
//  
//  
//  printf("Wartosc: %f\n", oblicz_P(3,2,4));  
//  _vptenter(310);
//  wypelnij_P(); printf("Wyplnilem P\n");
//  wypelnij_R(); printf("Wypelnilem R\n");
//  wypelnij_x(); printf("Wypelnilem wektor testowy\n");
//  _vptleave(310);
//  
//  _vptenter(311);
//  mnozenie(); printf("Pomnozylem macierze\n");
//  //wyswietl(); printf("Wyswietlilem wektor testowy\n");
//  _vptleave(311);
//  
//  _vptenter(312);
//  mnozenie_BLAS(); printf("BLAS method\n");
//  _vptleave(312);
//  
//  //wyswietl();
//  
//  _vptenter(313);
//  iteracja_V(); printf("Iteracja wartosci MDP - OK\n");
//  // wyswietl_V();
//  _vptleave(313);
//  //MPI_Finalize();
//
//#if defined(VAMPIR_TRACE)
//  _vptleave(300);
//  MPI_Finalize_rep();
//  _vptflush();
//#else
//  MPI_Finalize();
//#endif
//
//}
//
