/**
Written By Ziwei Wu
Used as a way to better understand how logic circuits work by implementing them into
software.
**/
public class MotherBoard {

	public static void main(String[] args) {
		int[] s = decimalToBinary(4,4);
		int[] f = decimalToBinary(-4,4);
		int[] g = multiplier(s, f);
		printOut(g);
		System.out.println(binaryToDecimal(g));
	}
	public static int logicalAnd1Bit(int a, int b) {
		if(a == 1 && b == 1) return 1;
		else return 0;
	}
	public static int logicalOr1Bit(int a, int b) {
		if(a==1 || b==1) return 1;
		else return 0;
	}
	public static int logicalXor1Bit(int a, int b) {
		if((a==1 && b==0) || (a==0 && b==1)) return 1;
		else return 0;
	}
	public static int logicalNot1Bit(int a) {
		if(a==1) return 0;
		else return 1;
	}
	public static int[] logicalNot32Bit(int[] a) {
		int[] s = a.clone();
		for(int i =0; i<a.length; i++) {
			s[i] = logicalNot1Bit(a[i]);
		}
		return s;
	}
	public static int twoToOneMultiplexer(int a, int b, int x) {
		//x is the select bit
		//if 0, then select a
		//if 1, then select b
		return logicalOr1Bit(logicalAnd1Bit(a, logicalNot1Bit(x)), logicalAnd1Bit(b,x));
	}
	public static int[] twoToOneMultiplexer32Bit(int[] a, int[] b, int x) {
		//x is the select bit
		//if 0, then select a
		//if 1, then select b
		int[] s = new int[a.length];
		for(int i =0; i<a.length; i++) {
			s[i] = twoToOneMultiplexer(a[i], b[i], x);
		}
		return s;
	}
	public static int[] halfAdder(int a, int b) {
		int[] s = new int[2];
		//s[0] is the sum, s[1] is the carry bit
		s[0] = logicalXor1Bit(a,b);
		s[1] = logicalAnd1Bit(a,b);
		return s;
	}
	public static int[] fullAdder1Bit(int a, int b, int c) {
		int[] s = new int[2];
		//s[0] is the sum, s[1] is the carry bit
		int[] h = halfAdder(a,b);
		s[0] = logicalXor1Bit(c, h[0]);
		s[1] = logicalOr1Bit(h[1], logicalAnd1Bit(c, logicalXor1Bit(a, b)));
		return s;
	}
	public static int[] fullAdder(int[] a, int[] b, int m) {
		int c = m;
		int[] s = new int[a.length];
		for(int i =0; i<a.length; i++) {
			int[] r = fullAdder1Bit(a[i], b[i], c);
			s[i] = r[0];
			c = r[1];
		}
		return s;
	}
	public static int[] fullAdderWithCarry(int[] a, int[] b, int m) {
		int c = m;
		int[] s = new int[a.length+1];
		for(int i =0; i<a.length; i++) {
			int[] r = fullAdder1Bit(a[i], b[i], c);
			s[i] = r[0];
			c = r[1];
		}
		s[s.length-1] = c;
		return s;
	}
	public static int[] addOrSub(int[] a, int[] b, int mode) {
		for(int i =0; i<b.length; i++) {
			b[i] = logicalXor1Bit(mode, b[i]);
		}
		return fullAdder(a,b,mode);
	}
	public static int[] shiftRight(int[] a) {
		int[] s = new int[a.length];
		for(int i =0; i<a.length-1; i++) {
			s[i] = a[i+1];
		}
		s[a.length-1] = 0;
		return s;
	}
	public static int[] shiftLeft(int[] a) {
		int[] s = new int[a.length];
		for(int i =1; i<a.length; i++) {
			s[i] = a[i-1];
		}
		s[0] = 0;
		return s;
	}
	public static int[] shiftRightWithNum(int[] a, int num) {
		int[] s = shiftRight(a);
		s[a.length-1] = num;
		return s;
	}
	public static int[] shiftLeftWithNum(int[] a, int num) {
		int[] s = shiftLeft(a);
		s[0] = num;
		return s;
	}
	public static int[] ReduceRight(int[]a) {
		//gets rid of the LSB 
		//kinda like shift right, but the size of the array is shrunk by 1
		int[] s = new int[a.length-1];
		for(int i =1; i<a.length; i++) {
			s[i-1] = a[i]; 
		}
		return s;
	}
	public static int[] oneToManyAnds(int[]a, int b) {
		int[] s = new int[a.length];
		for(int i =0; i<a.length; i++) {
			s[i] = logicalAnd1Bit(a[i], b);
		}
		return s;
	}
	public static int[] signExt(int[] a, int size) {
		int[] s = new int[size];
		for(int i =0; i<a.length; i++) {
			s[i] = a[i];
		}
		for(int i = a.length; i<s.length; i++) {
			s[i] = a[a.length-1];
		}
		return s;
	}
	public static int[] multiplier(int[] a, int[] b) {
		int[] s = new int[a.length + b.length];
		int aNeg = a[a.length-1], bNeg = b[b.length-1];
		a = twoToOneMultiplexer32Bit(a, twosComp(a), aNeg);
		b = twoToOneMultiplexer32Bit(b, twosComp(b), bNeg);
		int[] tempHold = oneToManyAnds(a, b[0]);
		s[0] = tempHold[0];
		tempHold = shiftRight(tempHold);
		for(int i =1; i<a.length; i++) {
			int[] bAndA = oneToManyAnds(a, b[i]);
			tempHold = fullAdderWithCarry(tempHold, bAndA, 0);
			s[i] = tempHold[0];
			tempHold = ReduceRight(tempHold);
		}
		for(int i = 0; i<tempHold.length; i++) {
			s[s.length-i-1] = tempHold[tempHold.length-i-1];
		}
		s = twoToOneMultiplexer32Bit(s, twosComp(s), logicalXor1Bit(aNeg, bNeg));
		return s;
	}
	public static int[] decimalToBinary(int aa, int size) {
		int[] s = new int[size];
		int a = abs(aa);
		for(int i =0; i<size; i++) {
			s[i] = a%2;
			a /= 2;
		}
		if(aa<0) {
			s = twosComp(s);
		}
		return s;
	}
	public static int[] twosComp(int[] a) {
		int[] s = addOrSub(logicalNot32Bit(a), decimalToBinary(1,a.length), 0);
		return s;
	}
	public static int abs(int a) {
		if(a >= 0) return a;
		else return a*-1; 
	}
	public static int binaryToDecimal(int[] a) {
		int x = 0;
		boolean isNeg = false;
		if(a[a.length-1]==1) {
			isNeg = true;
			a= twosComp(a);
		}
		for(int i =0; i<a.length; i++) {
			if(a[i] != 0) {
				x+=exp(2, i);
			}
		}
		if(isNeg) x*= -1;
		return x;
	}
	public static int exp(int a, int b) {
		int i =1;
		for(int x =0; x<b; x++) {
			i*=a;
		}
		return i;
	}
	public static void printOut(int[] a) {
		String s = "";
		for(int i = a.length-1; i>=0; i--) {
			s+= a[i];
		}
		System.out.println(s);
	}
}
