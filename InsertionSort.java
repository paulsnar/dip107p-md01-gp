public class InsertionSort implements Sorter, QuadraticSort {
	public int[] sort(int[] array){
		int N = array.length;
		
		for (int i = 0; i < N; i++){
			int B = array[i];
			for (int j = 0; j < i+1; j++){
				if (B > array[j]){
					if (i == j){
						B = array[j];
					}
					continue;
				} else if (B == array[j]){
					B = array[j];
				} else {
					offsetRightArr(array, j, i);
					B = array[j];
					break;
				}
			}
		}
		return array;
	}
	public static void offsetRightArr(int[] a, int b, int c){
		int change = a[c];
		for (int i = c-1; i >= b; i--){
			a[i+1] = a[i];
		}
		a[b] = change;
	}
	public ArrayWindow sort(ArrayWindow window) {
		return new ArrayWindow(sort(window.getArray()));
	}
}
