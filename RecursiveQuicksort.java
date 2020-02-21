import java.util.Random;

class RecursiveQuicksort implements Sorter {
	private Random rand = new Random();
	public int[] sort (int []array){
		quickSort(array, 0, array.length-1);
		return array;
	}
	public void quickSort(int a[], int I, int r){
		int m = a[randomNumber(I, r)];
		int i = I;
		int j = r;
		int prev = 0;
		do {
			while (a[i] < m){
				i++;
			}
			while (a[j] > m){
				j--;
			}
			if (i <= j){
				prev = a[i];
				a[i] = a[j];
				a[j] = prev;
				i++;
				j--;
			}
		} while(i <= j);
		if (j > I) {
			quickSort(a, I, j);
		}
		if (r > i){
			quickSort(a, i, r);
		}
	}
	public int randomNumber(int min, int max){
		return rand.nextInt(max+1-min)+min;
	}
	public ArrayWindow sort(ArrayWindow window) {
	    return new ArrayWindow(sort(window.getArray()));
	  }
}
