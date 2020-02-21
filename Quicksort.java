import java.util.Random;

public class Quicksort implements Sorter{
	public int[] sort(int[] array){
		int N = array.length;
		int prev = 0;
		int median = N/2;
		int left[] = new int[median];
		int right[] = new int[median];
		left[0] = 0;
		right[0] = N - 1;
		int stackpos = 1;
		do {
			stackpos--;
			int I = left[stackpos];
			int r = right[stackpos];
			do {
			int m = array[randomNum(I, r)];
			int i = I;
			int j = r;
				do {
					while (array[i] < m){
						i++;
					}
					while (array[j] > m){
						j--;
					}
					if (i <= j) {
						prev = array[i];
						array[i] = array[j];
						array[j] = prev;
						i++;
						j--;
					}
				} while(i <= j);
				if (i < r){
					left[stackpos] = i;
					right[stackpos] = r;
					stackpos++;
				}
				r = j;
			} while(I < r);
		} while(stackpos > 0);
		return array;
	}
	public static int randomNum(int minvalue, int maxvalue){
		Random rand = new Random();
		return rand.nextInt((maxvalue+1)-minvalue)+minvalue;
	}
	public ArrayWindow sort(ArrayWindow window) {
		return new ArrayWindow(sort(window.getArray()));
	}
}