public class SquareRootSort implements Sorter {
	public int[] sort(int []a){
		int N = a.length;
		int gCnt = (int)Math.ceil(Math.sqrt(N));
		int gLen = (int)Math.sqrt(N);
		int[] buf = new int[gCnt];
		int[][] gBorders = new int[gCnt][2];
		gBorders[0][0] = 0;
		gBorders[0][1] = gLen;
		for (int i = 1; i <= gCnt-1; i++){
			gBorders[i][0] = gBorders[i-1][0] + gLen;
			gBorders[i][1] = gBorders[i-1][1] + gLen;
		}
		gBorders[gCnt-1][1] = N-1;
		
		int min = 0;
		for (int i = 0; i < gCnt;i++) {
			min = gBorders[i][0];
			for (int j = gBorders[i][0]+1; j <= gBorders[i][1];j++) {
				if (a[min] > a[j]) {
					min = j;
				}
			}
			buf[i] = a[min];
			a[min] = Integer.MAX_VALUE;
		}
		
		int b[] = new int[N];
		int prev = buf[0];
		int bmin = 0;
		for (int k = 0; k < N;k++) {
			bmin = minVal(buf);
			prev = buf[0];
			b[k] = buf[bmin];
			buf[bmin] = Integer.MAX_VALUE;
			min = gBorders[bmin][0];
			for (int i = gBorders[bmin][0]+1; i <= gBorders[bmin][1];i++) {
				if (a[min] > a[i]) {
					min = i;
				}
			}
			buf[bmin] = a[min];
			a[min] = Integer.MAX_VALUE;
		}
		for (int i = 0; i < N; i++) {
			a[i] = b[i];
		}
		return a;
	}
	public int minVal(int []arr) {
		int min = arr[0];
		int index = 0;
		for (int i = 0; i < arr.length;i++) {
			if (arr[i] < min) {
				min = arr[i];
				index = i;
			}
		}
		return index;
	}
	public ArrayWindow sort(ArrayWindow window) {
		return new ArrayWindow(sort(window.getArray()));
	}
}